package net.toughcoder.aeolus.data.location

import android.util.Log
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.data.qweather.GeoAPIService
import net.toughcoder.aeolus.model.toModel
import net.toughcoder.aeolus.model.toParamLang

class QWeatherLocationSource(
    private val api: GeoAPIService
) : LocationDataSource {
    companion object {
        const val LOG_TAG = "QWeatherGeo"
    }

    override suspend fun searchHotCities(lang: String): List<WeatherLocation> {
        try {
            val response = api.fetchTopCities(number = 20, lang = toParamLang(lang))
            if (response.code == "200") {
                return response.topCityList
                    .filter { it.rank > 1 && (it.name == it.admin1 || it.name == it.admin2) }
                    .map { it.toModel() }
            }
        } catch (exception: Exception) {
            Log.d(LOG_TAG, "searchHotCities: Error: ${exception.message}")
        }
        return listOf()
    }

    override suspend fun searchCity(query: String, lang: String): List<WeatherLocation> {
        try {
            val response = api.searchCity(query = query, number = 20, lang = toParamLang(lang))
            if (response.code == "200") {
                return response.cityList.filter { it.rank > 5 }
                    .map { it.toModel() }
            }
        } catch (exception: Exception) {
            Log.d(LOG_TAG, "searchCity: Error: ${exception.message}")
        }
        return listOf()
    }

    override suspend fun loadCityInfo(cityId: String, lang: String): WeatherLocation {
        try {
            val response = api.searchCity(query = cityId, number = 1, lang = toParamLang(lang))
            if (response.code == "200") {
                return response.cityList[0].let { it.toModel() }
            } else {
                Log.d(LOG_TAG, "loadCityInfo $cityId: ${response.code}")
            }
        } catch (exception: Exception) {
            Log.d(LOG_TAG, "loadCityInfo: $cityId: ${exception.message}")
        }
        return WeatherLocation()
    }

    override suspend fun searchByGeo(lat: Double, log: Double, lang: String): WeatherLocation {
        try {
            val response = api.searchCity(query = "$lat,$log", number = 1, lang = toParamLang(lang))
            if (response.code == "200") {
                return response.cityList[0].let { it.toModel() }
            } else {
                Log.d(LOG_TAG, "searchByGeo failed: ${response.code}")
            }
        } catch (e: Exception) {
            Log.d(LOG_TAG, "searchByGeo exception: ${e.message}")
        }
        return WeatherLocation()
    }
}