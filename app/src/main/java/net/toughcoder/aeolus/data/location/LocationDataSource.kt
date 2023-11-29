package net.toughcoder.aeolus.data.location

import net.toughcoder.aeolus.model.WeatherLocation

interface LocationDataSource {
    suspend fun searchHotCities(lang: String): List<WeatherLocation>

    suspend fun searchCity(query: String, lang: String): List<WeatherLocation>

    suspend fun loadCityInfo(cityId: String, lang: String): WeatherLocation
}