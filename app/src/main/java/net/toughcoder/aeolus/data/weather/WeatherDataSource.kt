package net.toughcoder.aeolus.data.weather

import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.WeatherNow

interface WeatherDataSource {
    suspend fun loadWeatherNow(loc: WeatherLocation): WeatherNow

    suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNow)

    suspend fun loadDailyWeather(loc: WeatherLocation): List<DailyWeather>
}