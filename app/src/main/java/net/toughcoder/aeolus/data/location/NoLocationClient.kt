package net.toughcoder.aeolus.data.location

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
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