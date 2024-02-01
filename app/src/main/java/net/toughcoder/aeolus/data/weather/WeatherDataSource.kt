package net.toughcoder.aeolus.data.weather

import net.toughcoder.aeolus.data.qweather.QWeatherAirDTO
import net.toughcoder.aeolus.data.qweather.QWeatherDayDTO
import net.toughcoder.aeolus.data.qweather.QWeatherHourDTO
import net.toughcoder.aeolus.data.qweather.QWeatherIndexDTO
import net.toughcoder.aeolus.data.qweather.QWeatherNowDTO
import net.toughcoder.aeolus.data.room.DailyWeatherEntity
import net.toughcoder.aeolus.data.room.WeatherNowEntity
import net.toughcoder.aeolus.model.AirQuality
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.DailyWeatherIndex
import net.toughcoder.aeolus.model.HourlyWeather
import net.toughcoder.aeolus.model.WeatherIndex
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.WeatherNow

interface WeatherDataSource {
    suspend fun loadWeatherNow(loc: WeatherLocation, lang: String, measure: String): QWeatherNowDTO?

    suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNowEntity)

    suspend fun loadDailyWeather(loc: WeatherLocation, lang: String, measure: String): List<DailyWeather>

    suspend fun load7DayWeathers(loc: WeatherLocation, lang: String, measure: String, types: List<Int>): List<QWeatherDayDTO>

    suspend fun updateDailyWeather(loc: WeatherLocation, dailyWeathers: List<DailyWeatherEntity>)

    suspend fun load24HourWeathers(loc: WeatherLocation, lang: String, measure: String): List<QWeatherHourDTO>

    suspend fun loadWeatherIndices(loc: WeatherLocation, type: List<Int>, lang: String): List<QWeatherIndexDTO>

    suspend fun loadDailyWeatherIndices(loc: WeatherLocation, types: List<Int>, lang: String): Map<String, List<QWeatherIndexDTO>>

    suspend fun loadAirQualityNow(loc: WeatherLocation, lang: String): QWeatherAirDTO?

    suspend fun loadDailyAirQuality(loc: WeatherLocation, lang: String): List<QWeatherAirDTO>
}