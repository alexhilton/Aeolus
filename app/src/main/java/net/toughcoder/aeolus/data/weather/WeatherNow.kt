package net.toughcoder.aeolus.data.weather

import net.toughcoder.aeolus.data.local.WeatherNowEntity

data class WeatherNow(
    val successful: Boolean = false,
    val nowTemp: String = "",
    val feelsLike: String = "",
    val icon: String = "",
    val text: String = "",
    val windDegree: String = "",
    val windDir: String = "",
    val windScale: String = "",
    val windSpeed: String = "",
    val humidity: String = "",
    val airPressure: String = "",
    val visibility: String = "",
    val cloud: String = "",
    val updateTime: Long = -1
)

fun WeatherNowEntity.asModel(): WeatherNow = WeatherNow(
    successful = true,
    nowTemp = nowTemp,
    feelsLike = feelsLike,
    icon = icon,
    text = text,
    windDegree = windDegree,
    windDir = windDir,
    windScale = windScale,
    humidity = humidity,
    airPressure = airPressure,
    visibility = visibility,
    updateTime = updateTime
)