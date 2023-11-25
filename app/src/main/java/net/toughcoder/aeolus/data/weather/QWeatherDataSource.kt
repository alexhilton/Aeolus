package net.toughcoder.aeolus.data.weather

import android.util.Log
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.data.qweather.QWeatherService
import net.toughcoder.aeolus.data.qweather.toModel
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.HourlyWeather
import net.toughcoder.aeolus.model.MEASURE_IMPERIAL
import net.toughcoder.aeolus.model.WeatherNow
import net.toughcoder.aeolus.model.toModel
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class QWeatherDataSource(
    private val api: QWeatherService
) : WeatherDataSource {
    companion object {
        const val LOG_TAG = "QWeatherNowDataSource"
    }
    override suspend fun loadWeatherNow(loc: WeatherLocation, lang: String, measure: String): WeatherNow {
        try {
            val qpm = if (measure == MEASURE_IMPERIAL) "i" else "m"
            val response = api.fetchWeatherNow(loc.id, lang, qpm)
            return if (response.code == "200") {
                with(response.now) {
                    WeatherNow(
                        successful = true,
                        nowTemp = temp,
                        feelsLike = feelsLike,
                        icon = icon,
                        text = text,
                        windDegree = windDegree,
                        windDir = windDir,
                        windScale = windScale,
                        windSpeed = windSpeed,
                        humidity = humidity,
                        airPressure = pressure,
                        visibility = visibility,
                        cloud = cloud,
                        updateTime = parseTime(response.updateTime),
                        measure = measure
                    )
                }
            } else {
                Log.d(LOG_TAG, "Error code: ${response.code}")
                WeatherNow(successful = false)
            }
        } catch (exception: Exception) {
            return WeatherNow(successful = false)
        }
    }

    private fun parseTime(t: String): Long {
        val d = LocalDateTime.parse(t, DateTimeFormatter.ISO_DATE_TIME)
        Log.d(LOG_TAG, "parseTime $t -> $d, long ${d.toEpochSecond(ZoneOffset.UTC)}")
        return d.toEpochSecond(ZoneOffset.UTC)
    }

    override suspend fun loadDailyWeather(loc: WeatherLocation): List<DailyWeather> {
        try {
            val response = api.fetchWeather3D(loc.id)
            return if (response.code == "200") {
                response.dayList.map { it.toModel() }
            } else {
                emptyList()
            }
        } catch(exception: Exception) {
            Log.d(LOG_TAG, "Failed to load daily weather ${exception.message}")
        }
        return emptyList()
    }

    override suspend fun load7DayWeathers(loc: WeatherLocation): List<DailyWeather> {
        try {
            val response = api.fetchWeather7D(loc.id)
            return if (response.code == "200") {
                response.dayList.map { it.toModel() }
            } else {
                emptyList()
            }
        } catch(exception: Exception) {
            Log.d(LOG_TAG, "Failed to load 7 days weather ${exception.message}")
        }
        return emptyList()
    }

    override suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNow) {
        // Not implemented
    }

    override suspend fun updateDailyWeather(
        loc: WeatherLocation,
        dailyWeathers: List<DailyWeather>
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun load24HourWeathers(loc: WeatherLocation): List<HourlyWeather> {
        try {
            val response = api.fetchWeather24H(loc.id)
            return if (response.code == "200") {
                response.hourList.map { it.toModel() }
            } else {
                emptyList()
            }
        } catch (exception: Exception) {}
        return emptyList()
    }
}