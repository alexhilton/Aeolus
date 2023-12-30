package net.toughcoder.aeolus.data.location

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

        emit(MyLocation(116.41, 39.92))
    }
}