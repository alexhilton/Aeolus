package net.toughcoder.aeolus.data.location

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.toughcoder.aeolus.data.qweather.GeoAPIService
import net.toughcoder.aeolus.data.qweather.QWeatherCityDTO
import net.toughcoder.aeolus.logd
import net.toughcoder.aeolus.model.toParamLang

class QWeatherLocationSource(
    private val api: GeoAPIService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : LocationDataSource {
    companion object {
        const val LOG_TAG = "QWeatherGeo"
    }

    override suspend fun searchHotCities(lang: String): List<QWeatherCityDTO> =
        withContext(dispatcher) {
            try {
                val response = api.fetchTopCities(number = 20, lang = toParamLang(lang))
                if (response.code == "200") {
                    return@withContext response.topCityList
                }
            } catch (exception: Exception) {
                logd(LOG_TAG, "searchHotCities: Error: ${exception.message}")
            }
            return@withContext emptyList<QWeatherCityDTO>()
        }

    override suspend fun searchCity(query: String, lang: String): List<QWeatherCityDTO> =
        withContext(dispatcher) {
            try {
                val response = api.searchCity(query = query, number = 20, lang = toParamLang(lang))
                if (response.code == "200") {
                    return@withContext response.cityList
                }
            } catch (exception: Exception) {
                logd(LOG_TAG, "searchCity: Error: ${exception.message}")
            }
            return@withContext emptyList<QWeatherCityDTO>()
        }

    override suspend fun loadCityInfo(cityId: String, lang: String): QWeatherCityDTO? =
        withContext(dispatcher) {
            try {
                val response = api.searchCity(query = cityId, number = 1, lang = toParamLang(lang))
                if (response.code == "200") {
                    return@withContext response.cityList[0] //.toModel()
                } else {
                    logd(LOG_TAG, "loadCityInfo $cityId: ${response.code}")
                }
            } catch (exception: Exception) {
                logd(LOG_TAG, "loadCityInfo: $cityId: ${exception.message}")
            }
            return@withContext null //WeatherLocation(error = ERROR_NO_CITY)
        }

    override suspend fun searchByGeo(longitude: Double, latitude: Double, lang: String): QWeatherCityDTO? =
        withContext(dispatcher) {
            try {
                val response = api.searchCity(query = "$longitude,$latitude", number = 1, lang = toParamLang(lang))
                if (response.code == "200" && response.cityList.isNotEmpty()) {
                    return@withContext response.cityList[0]
                } else {
                    logd(LOG_TAG, "searchByGeo failed: ${response.code}")
                }
            } catch (e: Exception) {
                logd(LOG_TAG, "searchByGeo exception: ${e.message}")
            }
            return@withContext null
        }
}