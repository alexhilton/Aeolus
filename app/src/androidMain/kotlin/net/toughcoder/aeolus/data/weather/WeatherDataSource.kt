package net.toughcoder.aeolus.data.weather

import net.toughcoder.aeolus.data.qweather.QWeatherAirDTO
import net.toughcoder.aeolus.data.qweather.QWeatherDayDTO
import net.toughcoder.aeolus.data.qweather.QWeatherHourDTO
import net.toughcoder.aeolus.data.qweather.QWeatherIndexDTO
import net.toughcoder.aeolus.data.qweather.QWeatherNowDTO
import net.toughcoder.aeolus.data.room.DailyWeatherEntity
import net.toughcoder.aeolus.data.room.WeatherNowEntity

interface WeatherDataSource {
    suspend fun loadWeatherNow(loc: String, lang: String, measure: String): QWeatherNowDTO?

    suspend fun updateWeatherNow(loc: String, weatherNow: WeatherNowEntity)

    suspend fun load3DayWeathers(loc: String, lang: String, measure: String): List<QWeatherDayDTO>

    suspend fun load7DayWeathers(loc: String, lang: String, measure: String, types: List<Int>): List<QWeatherDayDTO>

    suspend fun updateDailyWeather(loc: String, dailyWeathers: List<DailyWeatherEntity>)

    suspend fun load24HourWeathers(loc: String, lang: String, measure: String): List<QWeatherHourDTO>

    suspend fun loadWeatherIndices(loc: String, type: List<Int>, lang: String): List<QWeatherIndexDTO>

    suspend fun loadDailyWeatherIndices(loc: String, types: List<Int>, lang: String): Map<String, List<QWeatherIndexDTO>>

    suspend fun loadAirQualityNow(loc: String, lang: String): QWeatherAirDTO?

    suspend fun loadDailyAirQuality(loc: String, lang: String): List<QWeatherAirDTO>
}