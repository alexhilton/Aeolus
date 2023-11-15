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