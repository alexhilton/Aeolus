package net.toughcoder.aeolus.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class LocationClient(
    private val context: Context
) : LocationProvider {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getLocation(): Flow<MyLocation> = callbackFlow {
        if (missingPermission()) {
            send(MyLocation(ERROR_NO_PERM, ERROR_NO_PERM))
            return@callbackFlow
        }

        val token = CancellationTokenSource()
        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token)
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    launch { send(MyLocation(ERROR_NO_LOCATION, ERROR_NO_LOCATION)) }
                } else {
                    launch { send(toMyLocation(location)) }
                }
            }
            .addOnFailureListener {
                launch { send(MyLocation(ERROR_FAILURE, ERROR_FAILURE)) }
            }
        awaitClose {}
    }

    private fun missingPermission(): Boolean {
        val coarsed = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
        val fined = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return !coarsed || !fined
    }

    private fun toMyLocation(loc: Location): MyLocation {
        return MyLocation(loc.latitude, loc.longitude)
    }

    companion object {
        const val LOG_TAG = "LocationClient"
        const val ERROR_NO_PERM = -1.0
        const val ERROR_NO_LOCATION = -2.0
        const val ERROR_FAILURE = -3.0
    }
}