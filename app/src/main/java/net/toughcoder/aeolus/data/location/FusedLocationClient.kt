package net.toughcoder.aeolus.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class FusedLocationClient(
    context: Context
) : LocationProvider(context) {
    private val fusedClient = LocationServices.getFusedLocationProviderClient(context)

    @SuppressLint("MissingPermission")
    override fun getLocation(): Flow<MyLocation> = callbackFlow {
        if (missingPermission()) {
            send(emptyLocation(ERROR_NO_PERM))
            return@callbackFlow
        }

        val token = CancellationTokenSource()
        fusedClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token)
            .addOnSuccessListener { location: Location? ->
                if (location == null) {
                    launch { send(emptyLocation(ERROR_NO_LOCATION)) }
                } else {
                    launch { send(toMyLocation(location)) }
                }
            }
            .addOnFailureListener {
                launch { send(emptyLocation(ERROR_FAILURE)) }
            }
        awaitClose {}
    }
}