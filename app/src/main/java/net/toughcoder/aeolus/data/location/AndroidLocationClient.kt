package net.toughcoder.aeolus.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.location.LocationRequest
import android.os.Build
import android.os.SystemClock
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import net.toughcoder.aeolus.logd
import java.util.concurrent.Executor

class AndroidLocationClient(
    context: Context
) : LocationProvider(context) {
    @OptIn(ExperimentalCoroutinesApi::class)
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
            val loc = locations.maxByOrNull { it.elapsedRealtimeMillis }
            loc?.also {
                logd(LOG_TAG, "Get cached location from last known!")
            }?.let { emit(MyLocation(it.latitude, it.longitude)) }
        } else {
            val builder = LocationRequest.Builder(500)
                .setDurationMillis(60 * 1000) // Stop requesting after 1 min.
                .setMaxUpdates(1)

            providers.asFlow()
                .flatMapLatest { current(it, locationManager, builder.build(), context.mainExecutor) }
                .collect {
                    logd(LOG_TAG, "Get current location fix!")
                    emit(MyLocation(it.latitude, it.longitude))
                }
        }
    }.flowOn(Dispatchers.IO)

    private fun upToDate(loc: Location): Boolean {
        val now = SystemClock.elapsedRealtime()
        return now - loc.elapsedRealtimeMillis <= TIMEOUT
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

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.S)
    private fun current(
        provider: String,
        manager: LocationManager,
        request: LocationRequest,
        executor: Executor
    ): Flow<Location> = callbackFlow {
        manager.getCurrentLocation(provider, request, null, executor) { loc ->
            loc?.let { trySend(it) }
        }

        awaitClose {}
    }
}