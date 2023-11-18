package net.toughcoder.aeolus.data.weather

import android.util.Log
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.data.qweather.QWeatherService
import net.toughcoder.aeolus.data.qweather.toModel
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.WeatherNow
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class QWeatherNowDataSource(
    private val api: QWeatherService
) : WeatherNowDataSource {
    companion object {
        const val LOG_TAG = "QWeatherNowDataSource"
    }
    override suspend fun loadWeatherNow(loc: WeatherLocation): WeatherNow {
        try {
            val response = api.fetchWeatherNow(loc.id)
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
                        updateTime = parseTime(response.updateTime)
                    )
                }
            } else {
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
        val response = api.fetchWeather3D(loc.id)
        return if (response.code == "200") {
            response.dayList.map { it.toModel() }
        } else {
            listOf()
        }
    }

    override suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNow) {
        // Not implemented
    }
}