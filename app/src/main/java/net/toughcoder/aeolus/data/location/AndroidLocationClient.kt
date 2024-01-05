package net.toughcoder.aeolus.data.location

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationManager
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

class AndroidLocationClient(
    context: Context
) : LocationProvider(context) {
    @RequiresApi(Build.VERSION_CODES.S)
    @SuppressLint("MissingPermission")
    override fun getLocation(): Flow<MyLocation> = flow {
        if (missingPermission()) {
            emit(emptyLocation(ERROR_NO_PERM))
            return@flow
        }

        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        val locations = mutableListOf<Location>()
        val fromGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        if (fromGps != null) {
            locations.add(fromGps)
        }
        val fromNetwork = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        if (fromNetwork != null) {
            locations.add(fromNetwork)
        }
        val fromFused = locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER)
        if (fromFused != null) {
            locations.add(fromFused)
        }
        val fromPassive = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER)
        if (fromPassive != null) {
            locations.add(fromPassive)
        }
        if (locations.isEmpty()) {
            emit(emptyLocation(ERROR_NO_LOCATION))
        }
        locations.sortByDescending { it.time }
        emit(MyLocation(locations[0].latitude, locations[0].longitude))
    }.flowOn(Dispatchers.IO)
}