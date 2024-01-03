package net.toughcoder.aeolus.data.location

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.toughcoder.aeolus.data.local.AeolusStore
import net.toughcoder.aeolus.data.room.AeolusDatabase
import net.toughcoder.aeolus.data.room.asEntity
import net.toughcoder.aeolus.model.TYPE_CURRENT
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.asModel

class LocationRepository(
    private val prefStore: AeolusStore,
    private val database: AeolusDatabase,
    private val locationProvider: LocationProvider,
    private val datasource: LocationDataSource,
    private val dispatcher: CoroutineDispatcher
) {
    companion object {
        const val LIMIT = 20
        const val LOG_TAG = "LocationRepo"
    }

    fun getDefaultCityId(): Flow<String> =
        prefStore.getDefaultCity()
            .map { it.id }

    fun getDefaultCity(): Flow<WeatherLocation> =
        prefStore.getDefaultCity()
            .map {
                val lang = runBlocking { prefStore.getLanguage().first() }
                if (it.type == TYPE_CURRENT) {
                    val loc = runBlocking { locationProvider.getLocation().firstOrNull() }
                    if (loc == null || loc.isEmpty() || lang.isEmpty()) {
                        return@map WeatherLocation()
                    }
                    return@map datasource.searchByGeo(loc.longitude, loc.latitude, lang)
                }
                val city = datasource.loadCityInfo(it.id, lang)
                if (city.successful()) {
                    val dao = database.locationDao()
                    dao.update(city.asEntity())
                    return@map city
                }
                return@map it
            }.flowOn(dispatcher)

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
            val lang = runBlocking { prefStore.getLanguage().first() }
            val dao = database.locationDao()
            dao.getAllCities()
                .map {
                    val city = datasource.loadCityInfo(it.qid, lang)
                    if (city.successful()) {
                        dao.update(city.asEntity())
                    }
                    Log.d(LOG_TAG, "favorites: old city -> $it, new city -> $city")
                    return@map if (city.successful()) city else it.asModel()
                }
        }
    }

    fun getLocationInfo(cityId: String): Flow<WeatherLocation> = flow {
        val lang = runBlocking { prefStore.getLanguage().first() }
        val dao = database.locationDao()
        val city = datasource.loadCityInfo(cityId, lang)
        if (city.successful()) {
            dao.update(city.asEntity())
        }
        emit(
            if (city.successful()) {
                city
            } else {
                dao.getCity(cityId)?.asModel() ?: WeatherLocation()
            }
        )
    }.flowOn(dispatcher)

    suspend fun getHotCities(): List<WeatherLocation> =
        withContext(dispatcher) {
            val lang = runBlocking { prefStore.getLanguage().first() }
            datasource.searchHotCities(lang)
        }

    suspend fun searchCity(query: String): List<WeatherLocation> =
        withContext(dispatcher) {
            val lang = runBlocking { prefStore.getLanguage().first() }
            datasource.searchCity(query, lang)
        }

    fun getCurrentCity(): Flow<WeatherLocation> =
        combine(prefStore.getLanguage(), locationProvider.getLocation()) { lang, loc ->
            if (loc.isEmpty() || lang.isEmpty()) {
                return@combine WeatherLocation(type = TYPE_CURRENT)
            }
            return@combine datasource.searchByGeo(loc.longitude, loc.latitude, lang)
        }.flowOn(dispatcher)
}