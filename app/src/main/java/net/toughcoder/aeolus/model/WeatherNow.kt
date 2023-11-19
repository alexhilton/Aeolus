package net.toughcoder.aeolus.model

import net.toughcoder.aeolus.data.room.DailyWeatherEntity
import net.toughcoder.aeolus.data.room.WeatherNowEntity

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

data class DailyWeather(
    val date: String = "",
    val tempHigh: String = "",
    val tempLow: String = "",
    val sunrise: String = "",
    val sunset: String = "",
    val iconDay: String = "",
    val textDay: String = "",
    val uvIndex: String = "",
    val humidity: String = "",
    val pressure: String = "",
    val visibility: String = "",
    val windDegree: String = "",
    val windDir: String = "",
    val windScale: String = "",
    val windSpeed: String = "",
    val iconNight: String = "",
    val textNight: String = ""
)

fun DailyWeatherEntity.toModel(): DailyWeather =
    DailyWeather(
        date,
        tempHigh,
        tempLow,
        sunrise,
        sunset,
        iconDay,
        textDay,
        uvIndex,
        humidity,
        pressure,
        visibility,
        windDegree,
        windDir,
        windScale,
        windSpeed,
        iconNight,
        textNight
    )