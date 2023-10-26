package net.toughcoder.aeolus.data.weather.api

import com.google.gson.annotations.SerializedName

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