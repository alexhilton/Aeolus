package net.toughcoder.aeolus.data.qweather

import com.google.gson.annotations.SerializedName
import net.toughcoder.aeolus.model.DailyWeather
import retrofit2.http.Field

data class QWeatherNowResponse(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("updateTime") val updateTime: String,
    @field:SerializedName("now") val now: QWeatherNowDTO
)

data class QWeatherNowDTO(
    @field:SerializedName("temp") val temp: String,
    @field:SerializedName("feelsLike") val feelsLike: String,
    @field:SerializedName("icon") val icon: String,
    @field:SerializedName("text") val text: String,
    @field:SerializedName("wind360") val windDegree: String,
    @field:SerializedName("windDir") val windDir: String,
    @field:SerializedName("windScale") val windScale: String,
    @field:SerializedName("windSpeed") val windSpeed: String,
    @field:SerializedName("humidity") val humidity: String,
    @field:SerializedName("vis") val visibility: String,
    @field:SerializedName("pressure") val pressure: String,
    @field:SerializedName("cloud") val cloud: String
)

data class QWeatherTopCitiesResponse(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("topCityList") val topCityList: List<QWeatherCityDTO>
)

data class QWeatherCityDTO(
    @field:SerializedName("name") val name: String,
    @field:SerializedName("id") val qweatherId: String,
    @field:SerializedName("lat") val latitude: String,
    @field:SerializedName("lon") val longitude: String,
    @field:SerializedName("adm2") val admin2: String,
    @field:SerializedName("adm1") val admin1: String,
    @field:SerializedName("country") val country: String,
    @field:SerializedName("type") val type: String,
    @field:SerializedName("rank") val rank: Int
)

data class QWeatherSearchResponse(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("location") val cityList: List<QWeatherCityDTO>
)

data class QWeatherDailyResponse(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("daily") val dayList: List<QWeatherDayDTO>
)

data class QWeatherDayDTO(
    @field:SerializedName("fxDate") val date: String,
    @field:SerializedName("tempMax") val tempHigh: String,
    @field:SerializedName("tempMin") val tempLow: String,
    @field:SerializedName("sunrise") val sunrise: String,
    @field:SerializedName("sunset") val sunset: String,

    @field:SerializedName("iconDay") val iconDay: String,
    @field:SerializedName("textDay") val textDay: String,
    @field:SerializedName("wind360Day") val windDegreeDay: String,
    @field:SerializedName("windDirDay") val windDirDay: String,
    @field:SerializedName("windScaleDay") val windScaleDay: String,
    @field:SerializedName("windSpeedDay") val windSpeedDay: String,

    @field:SerializedName("iconNight") val iconNight: String,
    @field:SerializedName("textNight") val textNight: String,
    @field:SerializedName("wind360Night") val windDegreeNight: String,
    @field:SerializedName("windDirNight") val windDirNight: String,
    @field:SerializedName("windScaleNight") val windScaleNight: String,
    @field:SerializedName("windSpeedNight") val windSpeedNight: String,

    @field:SerializedName("precip") val precip: String,
    @field:SerializedName("uvIndex") val uvIndex: String,
    @field:SerializedName("humidity") val humidity: String,
    @field:SerializedName("pressure") val pressure: String,
    @field:SerializedName("vis") val visibility: String
)

fun QWeatherDayDTO.toModel(): DailyWeather =
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
        iconNight = iconNight
    )

data class QWeatherHourlyResponse(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("hourly") val hourList: List<QWeatherHourDTO>
)

data class QWeatherHourDTO(
    @field:SerializedName("fxTime") val dateTime: String,
    @field:SerializedName("temp") val temp: String,
    @field:SerializedName("text") val text: String,
    @field:SerializedName("icon") val icon: String,
    @field:SerializedName("wind360") val windDegree: String,
    @field:SerializedName("windDir") val windDir: String,
    @field:SerializedName("windScale") val windScale: String
)