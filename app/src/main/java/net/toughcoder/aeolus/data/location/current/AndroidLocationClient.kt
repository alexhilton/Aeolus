package net.toughcoder.aeolus.data.location.current

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import android.os.CancellationSignal
import android.os.SystemClock
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.timeout
import net.toughcoder.aeolus.logd
import java.util.concurrent.Executor

class AndroidLocationClient(
    context: Context
) : LocationProvider(context) {
    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun getLocation(): Flow<MyLocation> = flow {
        if (missingPermission()) {
            emit(emptyLocation(ERROR_NO_PERM))
            return@flow
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val providers = listOf(
            LocationManager.GPS_PROVIDER,
            LocationManager.NETWORK_PROVIDER,
            LocationManager.FUSED_PROVIDER,
            LocationManager.PASSIVE_PROVIDER
        )
        val locations = providers
            .mapNotNull { locationManager.getLastKnownLocation(it) }
            .filter(::upToDate)

        if (locations.isNotEmpty()) {
            val loc = locations.maxByOrNull { it.elapsedRealtimeNanos }
            loc?.also {
                logd(LOG_TAG, "Get cached location from last known!")
            }?.let { emit(MyLocation(it.latitude, it.longitude)) }
        } else {
            providers.asFlow()
                .flatMapLatest { current(it, locationManager, context.mainExecutor) }
                .timeout(REQUEST_TIMEOUT)
                .catch {
                    logd(LOG_TAG, "Failed to get location: ${it.message}")
                    emit(MyLocation(ERROR_NO_LOCATION, ERROR_NO_LOCATION))
                }
                .collect {
                    logd(LOG_TAG, "Get current location fix!")
                    emit(MyLocation(it.latitude, it.longitude))
                }
        }
    }.flowOn(Dispatchers.IO)

    private fun upToDate(loc: Location): Boolean {
        val now = SystemClock.elapsedRealtime()
        return now - loc.elapsedRealtimeNanos / 1000L <= LOCATION_AGE
    }

    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    private fun lastKnown(manager: LocationManager): Flow<Location> = flow {
        val locations = mutableListOf<Location>()
        val fromGps = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (fromGps != null) {
            locations.add(fromGps)
        }
        val fromNetwork = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (fromNetwork != null) {
            locations.add(fromNetwork)
        }
        val fromFused = manager.getLastKnownLocation(LocationManager.FUSED_PROVIDER)
        if (fromFused != null) {
            locations.add(fromFused)
        }
        val fromPassive = manager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        if (fromPassive != null) {
            locations.add(fromPassive)
        }
        if (locations.isNotEmpty()) {
            locations.sortByDescending { it.time }
            emit(locations[0])
        }
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun current(
        provider: String,
        manager: LocationManager,
        executor: Executor
    ): Flow<Location> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val builder = LocationRequest.Builder(500)
                .setDurationMillis(60 * 1000) // Stop requesting after 1 min.
                .setMaxUpdates(1)

            currentS(provider, manager, builder.build(), executor)
        } else {
            currentR(provider, manager, executor)
        }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun currentS(
        provider: String,
        manager: LocationManager,
        request: LocationRequest,
        executor: Executor
    ): Flow<Location> = callbackFlow {
        val cancelSignal = CancellationSignal()

        manager.getCurrentLocation(provider, request, cancelSignal, executor) { loc ->
            loc?.let { trySend(it) }
        }

        awaitClose {
            if (!cancelSignal.isCanceled) {
                cancelSignal.cancel()
            }
        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.R)
    private fun currentR(
        provider: String,
        manager: LocationManager,
        executor: Executor
    ): Flow<Location> = callbackFlow {
        val cancelSignal = CancellationSignal()
        manager.getCurrentLocation(provider, cancelSignal, executor) { loc ->
            loc?.let { trySend(it) }
        }

        awaitClose {
            if (!cancelSignal.isCanceled) {
                cancelSignal.cancel()
            }
        }
    }
}