package net.toughcoder.aeolus.data.weather

import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.HourlyWeather
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.WeatherNow

interface WeatherDataSource {
    suspend fun loadWeatherNow(loc: WeatherLocation, lang: String, measure: String): WeatherNow

    suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNow)

    suspend fun loadDailyWeather(loc: WeatherLocation): List<DailyWeather>

    suspend fun load7DayWeathers(loc: WeatherLocation): List<DailyWeather>

    suspend fun updateDailyWeather(loc: WeatherLocation, dailyWeathers: List<DailyWeather>)

    suspend fun load24HourWeathers(loc: WeatherLocation): List<HourlyWeather>
}