package net.toughcoder.aeolus.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.toughcoder.aeolus.data.qweather.QWeatherNowDTO
import net.toughcoder.aeolus.model.WeatherNow

@Entity(tableName = "weather_now")
data class WeatherNowEntity(
    @PrimaryKey val cityId: String,
    @ColumnInfo(name = "now_temp") val nowTemp: String,
    @ColumnInfo(name = "feels_like") val feelsLike: String,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "wind_degree") val windDegree: String,
    @ColumnInfo(name = "wind_dir") val windDir: String,
    @ColumnInfo(name = "wind_scale") val windScale: String,
    @ColumnInfo(name = "wind_speed") val windSpeed: String,
    @ColumnInfo(name = "humidity") val humidity: String,
    @ColumnInfo(name = "pressure") val airPressure: String,
    @ColumnInfo(name = "visibility") val visibility: String,
    @ColumnInfo(name = "aqi") val airQualityIndex: String
)

fun WeatherNow.asEntity(cityId: String): WeatherNowEntity = WeatherNowEntity(
    cityId = cityId,
    nowTemp = nowTemp,
    feelsLike = feelsLike,
    icon = icon,
    text = text,
    windDegree = windDegree,
    windDir = windDir,
    windSpeed = windSpeed,
    windScale = windScale,
    humidity = humidity,
    airPressure = airPressure,
    visibility = visibility,
    airQualityIndex = airQualityIndex
)

fun QWeatherNowDTO.toEntity(cityId: String, aqi: String): WeatherNowEntity =
    WeatherNowEntity(
        cityId = cityId,
        nowTemp = temp,
        feelsLike = feelsLike,
        icon = icon,
        text = text,
        windDegree = windDegree,
        windDir = windDir,
        windSpeed = windSpeed,
        windScale = windScale,
        humidity = humidity,
        airPressure = pressure,
        visibility = visibility,
        airQualityIndex = aqi,
    )

fun WeatherNowEntity.asDTO(): QWeatherNowDTO =
    QWeatherNowDTO(
        temp = nowTemp,
        feelsLike = feelsLike,
        icon = icon,
        text = text,
        windDir = windDir,
        windDegree = windDegree,
        windScale = windScale,
        windSpeed = windSpeed,
        humidity = humidity,
        pressure = airPressure,
        visibility = visibility,
        cloud = ""
    )