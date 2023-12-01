package net.toughcoder.aeolus.model

import net.toughcoder.aeolus.data.qweather.QWeatherDayDTO
import net.toughcoder.aeolus.data.qweather.QWeatherHourDTO
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
    val updateTime: Long = -1,
    val measure: String = "",
    val airQualityIndex: String = ""

)

fun WeatherNowEntity.asModel(measure: String): WeatherNow = WeatherNow(
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
    updateTime = updateTime,
    measure = measure
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
    val textNight: String = "",
    val measure: String = "",
    val airQualityIndex: String = ""
)

fun DailyWeatherEntity.toModel(measure: String): DailyWeather =
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
        textNight,
        measure,
        airQualityIndex = aqi
    )

fun QWeatherDayDTO.toModel(measure: String, aqi: String): DailyWeather =
    DailyWeather(
        date = date,
        tempHigh = tempHigh,
        tempLow = tempLow,
        sunrise = sunrise,
        sunset = sunset,
        iconDay = iconDay,
        textDay = textDay,
        uvIndex = uvIndex,
        humidity = humidity,
        visibility = visibility,
        pressure = pressure,
        windScale = windScaleDay,
        windDir = windDirDay,
        windDegree = windDegreeDay,
        windSpeed = windSpeedDay,
        textNight = textNight,
        iconNight = iconNight,
        measure = measure,
        airQualityIndex = aqi
    )

data class HourlyWeather(
    val dateTime: String,
    val temp: String,
    val icon: String,
    val text: String,
    val windDegree: String,
    val windDir: String,
    val windScale: String,
    val measure: String
)

fun QWeatherHourDTO.toModel(measure: String): HourlyWeather =
    HourlyWeather(
        dateTime = dateTime,
        temp = temp,
        text = text,
        icon = icon,
        windDegree = windDegree,
        windDir = windDir,
        windScale = windScale,
        measure = measure
    )

data class AirQuality(
    val index: Int = -1,
    val level: Int = -1,
    val category: String = "",
    val primary: String = ""
) {
    fun valid() = index >= 0 && level >= 0
}