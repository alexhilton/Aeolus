package net.toughcoder.aeolus.data.location

import net.toughcoder.aeolus.model.WeatherLocation

interface LocationDataSource {
    suspend fun searchHotCities(): List<WeatherLocation>

    suspend fun searchCity(query: String): List<WeatherLocation>
}