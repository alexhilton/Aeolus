package net.toughcoder.aeolus.data.weather

import net.toughcoder.aeolus.model.AirQuality
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.DailyWeatherIndex
import net.toughcoder.aeolus.model.HourlyWeather
import net.toughcoder.aeolus.model.WeatherIndex
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.WeatherNow

interface WeatherDataSource {
    suspend fun loadWeatherNow(loc: WeatherLocation, lang: String, measure: String): WeatherNow

    suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNow)

    suspend fun loadDailyWeather(loc: WeatherLocation, lang: String, measure: String): List<DailyWeather>

    suspend fun load7DayWeathers(loc: WeatherLocation, lang: String, measure: String, types: List<Int>): List<DailyWeather>

    suspend fun updateDailyWeather(loc: WeatherLocation, dailyWeathers: List<DailyWeather>)

    suspend fun load24HourWeathers(loc: WeatherLocation, lang: String, measure: String): List<HourlyWeather>

    suspend fun loadAirQualityNow(loc: WeatherLocation, lang: String): AirQuality

    suspend fun loadWeatherIndices(loc: WeatherLocation, type: List<Int>, lang: String): List<WeatherIndex>
}