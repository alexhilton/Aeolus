package net.toughcoder.aeolus.data.location

import net.toughcoder.aeolus.data.WeatherLocation

interface LocationDataSource {
    suspend fun searchHotCities(): List<WeatherLocation>

    suspend fun searchCity(query: String): List<WeatherLocation>
}