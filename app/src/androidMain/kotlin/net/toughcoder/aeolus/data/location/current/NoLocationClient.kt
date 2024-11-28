package net.toughcoder.aeolus.data.location.current

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.toughcoder.aeolus.data.location.current.LocationProvider
import net.toughcoder.aeolus.data.location.current.MyLocation
import kotlin.random.Random

class NoLocationClient(
    context: Context
) : LocationProvider(context) {
    override fun getLocation(): Flow<MyLocation> = flow {
        delay((2000 * Random.nextFloat()).toLong())

        val lat = ERROR_NO_LOCATION
        emit(MyLocation(lat, lat))
    }
}