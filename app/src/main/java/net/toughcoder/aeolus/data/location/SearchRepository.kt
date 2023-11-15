package net.toughcoder.aeolus.data.location

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.toughcoder.aeolus.model.WeatherLocation

class SearchRepository(
    private val datasource: LocationDataSource,
    private val dispatcher: CoroutineDispatcher
) {
    suspend fun getHotCities(): List<WeatherLocation> {
        return withContext(dispatcher) {
            datasource.searchHotCities()
        }
    }

    suspend fun searchCity(query: String): List<WeatherLocation> =
        withContext(dispatcher) {
            datasource.searchCity(query)
        }
}