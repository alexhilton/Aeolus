package net.toughcoder.aeolus.data.weather

import android.util.Log
import net.toughcoder.aeolus.data.WeatherLocation
import net.toughcoder.aeolus.data.weather.api.QWeatherService
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class QWeatherNowDataSource : WeatherNowDataSource {
    companion object {
        const val LOG_TAG = "QWeatherNowDataSource"
    }
    override suspend fun loadWeatherNow(loc: WeatherLocation): WeatherNow {
        val api = QWeatherService.create()
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
    }

    private fun parseTime(t: String): Long {
        val d = LocalDateTime.parse(t, DateTimeFormatter.ISO_DATE_TIME)
        Log.d(LOG_TAG, "parseTime $t -> $d, long ${d.toEpochSecond(ZoneOffset.UTC)}")
        return d.toEpochSecond(ZoneOffset.UTC)
    }
}