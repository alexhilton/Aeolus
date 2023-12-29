package net.toughcoder.aeolus.data.location

import kotlinx.coroutines.flow.Flow

interface LocationProvider {
    fun getLocation(): Flow<MyLocation>
}

data class MyLocation(
    val latitude: Double,
    val longitude: Double
) {
    fun isEmpty() = latitude > 0 && longitude > 0
}