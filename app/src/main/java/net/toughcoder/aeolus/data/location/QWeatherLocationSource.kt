package net.toughcoder.aeolus.data.location

import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.data.qweather.GeoAPIService

class QWeatherLocationSource(
    private val api: GeoAPIService
) : LocationDataSource {
    override suspend fun searchHotCities(): List<WeatherLocation> {
        try {
            val response = api.fetchTopCities(number = 20)
            if (response.code == "200") {
                return response.topCityList
                    .filter { it.rank > 1 && (it.name == it.admin1 || it.name == it.admin2) }
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