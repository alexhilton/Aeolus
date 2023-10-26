package net.toughcoder.aeolus.data.weather.api

import com.google.gson.annotations.SerializedName

data class QWeatherNowResponse(
    @field:SerializedName("code") val code: String,
    @field:SerializedName("updateTime") val updateTime: String,
    @field:SerializedName("now") val now: QWeatherNowDTO
)