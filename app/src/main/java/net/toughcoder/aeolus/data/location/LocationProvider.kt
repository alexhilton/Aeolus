package net.toughcoder.aeolus.data.location

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow

abstract class LocationProvider(
    val context: Context
) {
    abstract fun getLocation(): Flow<MyLocation>

    fun missingPermission(): Boolean {
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

    fun toMyLocation(loc: Location): MyLocation {
        return MyLocation(loc.latitude, loc.longitude)
    }

    fun emptyLocation(error: Double) = MyLocation(error, error)

    companion object {
        const val LOG_TAG = "LocationClient"
        const val ERROR_NO_PERM = -1.0
        const val ERROR_NO_LOCATION = -2.0
        const val ERROR_FAILURE = -3.0
        const val TIMEOUT = 5 * 60 * 1000 // 5 minutes
    }
}

data class MyLocation(
    val latitude: Double,
    val longitude: Double
) {
    fun isEmpty() = latitude <= 0 || longitude <= 0
}