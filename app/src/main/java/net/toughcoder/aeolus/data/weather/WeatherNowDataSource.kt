package net.toughcoder.aeolus.data.weather

import net.toughcoder.aeolus.data.WeatherLocation

interface WeatherNowDataSource {
    suspend fun loadWeatherNow(loc: WeatherLocation): WeatherNow

    suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNow)
}