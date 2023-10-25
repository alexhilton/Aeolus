package net.toughcoder.aeolus.data

import kotlinx.coroutines.delay

class LocationRepository {
    suspend fun getLocation(): WeatherLocation {
        delay(500)
        return WeatherLocation(
            "101190101",
            "Nanjing"
        )
    }
}