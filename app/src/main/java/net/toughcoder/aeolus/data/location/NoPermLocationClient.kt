package net.toughcoder.aeolus.data.location

import android.content.Context
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class NoPermLocationClient(
    context: Context
) : LocationProvider(context) {
    override fun getLocation(): Flow<MyLocation> = flow {
        delay(1300)

        emit(MyLocation(ERROR_NO_PERM, ERROR_NO_PERM))
    }
}