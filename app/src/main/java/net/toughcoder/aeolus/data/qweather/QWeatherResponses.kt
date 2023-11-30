package net.toughcoder.aeolus.data.qweather

import com.google.gson.annotations.SerializedName

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

data class QWeatherAirNowResponses(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("now") val now: QWeatherAirDTO
)

data class QWeatherAirDTO(
    @field:SerializedName("aqi") val index: String,
    @field:SerializedName("level") val level: String,
    @field:SerializedName("category") val category: String,
    @field:SerializedName("primary") val primary: String,
)

data class QWeatherAirDailyResponse(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("daily") val dailyAirs: List<QWeatherAirDTO>
)