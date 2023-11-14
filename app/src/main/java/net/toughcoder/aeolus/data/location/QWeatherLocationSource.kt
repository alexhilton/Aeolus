package net.toughcoder.aeolus.data.location

import net.toughcoder.aeolus.data.WeatherLocation
import net.toughcoder.aeolus.data.qweather.GeoAPIService

class QWeatherLocationSource(
    private val api: GeoAPIService
) : LocationDataSource {
    override suspend fun searchHotCities(): List<WeatherLocation> {
        try {
            val response = api.fetchTopCities()
            if (response.code == "200") {
                return response.topCityList
                    .map { WeatherLocation(it.qweatherId, it.name) }
                    .toList()
            }
        } catch(exception: Exception) {
        }
        return listOf()
    }

    override suspend fun searchCity(query: String): List<WeatherLocation> {
        TODO("Not yet implemented")
    }
}