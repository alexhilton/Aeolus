package net.toughcoder.aeolus.data.weather

import net.toughcoder.aeolus.data.WeatherLocation

interface WeatherNowDatasource {
    suspend fun loadWeatherNow(loc: WeatherLocation): WeatherNow
}