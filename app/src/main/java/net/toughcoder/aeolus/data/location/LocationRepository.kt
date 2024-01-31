package net.toughcoder.aeolus.data.location

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
import net.toughcoder.aeolus.data.location.current.LocationProvider
import net.toughcoder.aeolus.data.qweather.QWeatherCityDTO
import net.toughcoder.aeolus.data.room.AeolusDatabase
import net.toughcoder.aeolus.data.room.asEntity
import net.toughcoder.aeolus.logd
import net.toughcoder.aeolus.model.ERROR_NO_CITY
import net.toughcoder.aeolus.model.ERROR_NO_LOCATION
import net.toughcoder.aeolus.model.ERROR_NO_PERM
import net.toughcoder.aeolus.model.TYPE_CURRENT
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.asModel
import net.toughcoder.aeolus.model.toModel

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
            .map { defaultCity ->
                val lang = runBlocking { prefStore.getLanguage().first() }
                // When there is no default city, use current location.
                if (!defaultCity.successful() || defaultCity.type == TYPE_CURRENT) {
                    val loc = runBlocking { locationProvider.getLocation().firstOrNull() }
                    if (loc == null || loc.isEmpty() || lang.isEmpty()) {
                        val error = if (loc!!.latitude == LocationProvider.ERROR_NO_PERM) {
                            ERROR_NO_PERM
                        } else {
                            ERROR_NO_LOCATION
                        }
                        return@map WeatherLocation(type = TYPE_CURRENT, error = error)
                    }
                    val geoCity = datasource.searchByGeo(loc.longitude, loc.latitude, lang)
                    return@map geoCity
                        ?.run { toModel(TYPE_CURRENT) }
                        ?: WeatherLocation(type = TYPE_CURRENT, error = ERROR_NO_CITY)
                }
                val city = datasource.loadCityInfo(defaultCity.id, lang)
                city?.also {
                    val dao = database.locationDao()
                    dao.update(it.asEntity())
                }
                return@map city?.run { toModel() } ?: WeatherLocation(error = ERROR_NO_CITY)
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

    suspend fun loadFavoriteCitiesFromLocal(): List<WeatherLocation> =
        withContext(dispatcher) {
            val dao = database.locationDao()
            return@withContext dao.getAllCities().map { it.asModel() }
        }

    suspend fun loadFavoriteCities(): List<WeatherLocation> =
        withContext(dispatcher) {
            val lang = runBlocking { prefStore.getLanguage().first() }
            val dao = database.locationDao()
            dao.getAllCities()
                .map { localCity ->
                    val city = datasource.loadCityInfo(localCity.qid, lang)
                    city?.also { dao.update(it.asEntity()) }
                    logd(LOG_TAG, "favorites: old city -> $localCity, new city -> $city")
                    return@map city?.run { toModel() } ?: WeatherLocation(error = ERROR_NO_CITY)
                }
        }

    fun getLocationInfo(cityId: String): Flow<WeatherLocation> = flow {
        val lang = runBlocking { prefStore.getLanguage().first() }
        val dao = database.locationDao()
        val city = datasource.loadCityInfo(cityId, lang)
        city?.also { dao.update(it.asEntity()) }
        emit(
            city?.run { toModel() } ?: WeatherLocation(error = ERROR_NO_CITY)
        )
    }.flowOn(dispatcher)

    suspend fun getHotCities(): List<WeatherLocation> =
        withContext(dispatcher) {
            val lang = runBlocking { prefStore.getLanguage().first() }
            datasource.searchHotCities(lang)
                .filter { it.rank > 1 && (it.name == it.admin1 || it.name == it.admin2) }
                .map(QWeatherCityDTO::toModel)
        }

    suspend fun searchCity(query: String): List<WeatherLocation> =
        withContext(dispatcher) {
            val lang = runBlocking { prefStore.getLanguage().first() }
            datasource.searchCity(query, lang)
                .filter { it.rank > 5 }
                .map(QWeatherCityDTO::toModel)
        }

    fun getCurrentCity(): Flow<WeatherLocation> =
        combine(prefStore.getLanguage(), locationProvider.getLocation()) { lang, loc ->
            if (loc.isEmpty() || lang.isEmpty()) {
                return@combine WeatherLocation(type = TYPE_CURRENT)
            }
            val city = datasource.searchByGeo(loc.longitude, loc.latitude, lang)
            return@combine city
                ?.run { toModel(TYPE_CURRENT) }
                ?: WeatherLocation(type = TYPE_CURRENT, error = ERROR_NO_CITY)
        }.flowOn(dispatcher)
}