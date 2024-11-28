package net.toughcoder.aeolus.data.location

import net.toughcoder.aeolus.data.qweather.QWeatherCityDTO

interface LocationDataSource {
    suspend fun searchHotCities(lang: String): List<QWeatherCityDTO>

    suspend fun searchCity(query: String, lang: String): List<QWeatherCityDTO>

    suspend fun loadCityInfo(cityId: String, lang: String): QWeatherCityDTO?

    suspend fun searchByGeo(longitude: Double, latitude: Double, lang: String): QWeatherCityDTO?
}