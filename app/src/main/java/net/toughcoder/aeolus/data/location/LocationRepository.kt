package net.toughcoder.aeolus.data.location

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import net.toughcoder.aeolus.data.local.AeolusStore
import net.toughcoder.aeolus.data.room.AeolusDatabase
import net.toughcoder.aeolus.data.room.asEntity
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.asModel

class LocationRepository(
    private val prefStore: AeolusStore,
    private val database: AeolusDatabase,
    private val datasource: LocationDataSource,
    private val dispatcher: CoroutineDispatcher
) {
    companion object {
        const val LIMIT = 20
        const val LOG_TAG = "LocationRepo"
    }

    fun getDefaultCity() = prefStore.getDefaultCity()

    suspend fun setDefaultCity(city: WeatherLocation) {
        prefStore.persistCity(city)
    }

    suspend fun favoriteCity(city: WeatherLocation) {
        withContext(dispatcher) {
            val locationDao = database.locationDao()
            val qe = locationDao.getCity(city.id)
            if (qe == null) {
                if (locationDao.getCount() < LIMIT) {
                    locationDao.insert(city.asEntity())
                } else {
                    val dailyWeatherDao = database.dailyWeatherDao()
                    val nowWeatherDao = database.weatherNowDao()
                    val list = locationDao.getAllCities()
                    for (idx in list.size - 1 downTo LIMIT - 1) {
                        locationDao.delete(list[idx])
                        // Remove its belonging weather info as well
                        val now = nowWeatherDao.getByCityId(list[idx].qid)
                        if (now != null) {
                            nowWeatherDao.delete(now)
                        }
                        val weathers = dailyWeatherDao.getDailyWeathers(list[idx].qid)
                        if (weathers.isNotEmpty()) {
                            dailyWeatherDao.deleteDailyWeathers(weathers)
                        }
                    }
                    locationDao.insert(city.asEntity())
                }
            }
        }
    }

    suspend fun loadFavoriteCities(): List<WeatherLocation> {
        return withContext(dispatcher) {
            val dao = database.locationDao()
            dao.getAllCities()
                .map {
                    Log.d(LOG_TAG, "favorites: $it")
                    it.asModel()
                }
        }
    }

    fun getLocationInfo(cityId: String): Flow<WeatherLocation> = flow {
        val dao = database.locationDao()
        emit(dao.getCity(cityId)?.asModel() ?: WeatherLocation())
    }.flowOn(dispatcher)

    suspend fun getHotCities(): List<WeatherLocation> =
        withContext(dispatcher) {
            datasource.searchHotCities()
        }

    suspend fun searchCity(query: String): List<WeatherLocation> =
        withContext(dispatcher) {
            datasource.searchCity(query)
        }
}