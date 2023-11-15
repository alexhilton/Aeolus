package net.toughcoder.aeolus.data.location

import kotlinx.coroutines.delay
import net.toughcoder.aeolus.model.WeatherLocation

class LocationRepository {
    suspend fun getLocation(): WeatherLocation {
        delay(500)
        return WeatherLocation(
            "101190101",
            "Nanjing"
        )
    }
}