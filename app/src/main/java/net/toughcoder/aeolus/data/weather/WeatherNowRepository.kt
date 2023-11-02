package net.toughcoder.aeolus.data.weather

import net.toughcoder.aeolus.data.WeatherLocation
import net.toughcoder.aeolus.data.local.LocalDataSource

class WeatherNowRepository(
    private val local: LocalDataSource,
    private val network: WeatherNowDataSource
) {
    suspend fun getWeatherNow(location: WeatherLocation): WeatherNow {
        return local.loadWeatherNow(location)
    }

    suspend fun fetchWeatherNow(location: WeatherLocation): WeatherNow {
        val bundle = network.loadWeatherNow(location)
        if (bundle.successful) {
            // Update database
            local.updateWeatherNow(location, bundle)
        }
        return bundle
    }
}