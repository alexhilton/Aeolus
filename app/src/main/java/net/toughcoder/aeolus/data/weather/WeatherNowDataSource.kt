package net.toughcoder.aeolus.data.weather

import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.WeatherNow

interface WeatherNowDataSource {
    suspend fun loadWeatherNow(loc: WeatherLocation): WeatherNow

    suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNow)
}