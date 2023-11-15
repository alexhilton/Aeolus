package net.toughcoder.aeolus.data.location

import android.util.Log
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.data.qweather.GeoAPIService

class QWeatherLocationSource(
    private val api: GeoAPIService
) : LocationDataSource {
    companion object {
        const val LOG_TAG = "QWeatherGeo"
    }

    override suspend fun searchHotCities(): List<WeatherLocation> {
        try {
            val response = api.fetchTopCities(number = 20)
            if (response.code == "200") {
                return response.topCityList
                    .filter { it.rank > 1 && (it.name == it.admin1 || it.name == it.admin2) }
                    .map { WeatherLocation(it.qweatherId, it.name, it.admin1) }
            }
        } catch (exception: Exception) {
            Log.d(LOG_TAG, "searchHotCities: Error: ${exception.message}")
        }
        return listOf()
    }

    override suspend fun searchCity(query: String): List<WeatherLocation> {
        try {
            val response = api.searchCity(query = query, number = 20)
            if (response.code == "200") {
                return response.cityList.filter { it.rank > 5 }
                    .map { WeatherLocation(it.qweatherId, it.name, it.admin1) }
            }
        } catch (exception: Exception) {
            Log.d(LOG_TAG, "searchCity: Error: ${exception.message}")
        }
        return listOf()
    }
}