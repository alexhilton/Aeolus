package net.toughcoder.aeolus.data.location.current

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.random.Random

class FakeLocationClient(
    context: Context
) : LocationProvider(context) {
    override fun getLocation(): Flow<MyLocation> = flow {
        delay((2000f * Random.nextFloat()).toLong())

        emit(MyLocation(39.92, 116.41))
    }
}