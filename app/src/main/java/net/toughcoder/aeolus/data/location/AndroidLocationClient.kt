package net.toughcoder.aeolus.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Criteria
import android.location.LocationManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AndroidLocationClient(
    context: Context
) : LocationProvider(context) {
    @SuppressLint("MissingPermission")
    override fun getLocation(): Flow<MyLocation> = flow {
        if (missingPermission()) {
            emit(emptyLocation(ERROR_NO_PERM))
            return@flow
        }
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val criteria = Criteria()
        criteria.accuracy = Criteria.NO_REQUIREMENT
        criteria.isCostAllowed = true
        criteria.powerRequirement = Criteria.POWER_LOW
        criteria.isAltitudeRequired = false
        criteria.isBearingRequired = false

        val provider = locationManager.getBestProvider(criteria, true)
        val location = provider?.let { locationManager.getLastKnownLocation(it) }
        if (location == null) {
            emit(emptyLocation(ERROR_NO_LOCATION))
        } else {
            emit(MyLocation(location.latitude, location.longitude))
        }
    }.flowOn(Dispatchers.IO)
}