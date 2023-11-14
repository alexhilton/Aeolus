package net.toughcoder.aeolus.data.qweather

import com.google.gson.annotations.SerializedName

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