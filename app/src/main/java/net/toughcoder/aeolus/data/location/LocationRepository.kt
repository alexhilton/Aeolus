package net.toughcoder.aeolus.data.location

import android.util.Log
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import net.toughcoder.aeolus.data.AeolusStore
import net.toughcoder.aeolus.data.room.AeolusDatabase
import net.toughcoder.aeolus.data.room.asEntity
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.asModel

class LocationRepository(
    private val prefStore: AeolusStore,
    private val database: AeolusDatabase,
    private val dispatcher: CoroutineDispatcher
) {
    companion object {
        const val LIMIT = 20
        const val LOG_TAG = "LocationRepo"
    }

    fun getDefaultCity() = prefStore.getDefaultCity()
//    : WeatherLocation {
//        delay(500)
//        return WeatherLocation(
//            "101190101",
//            "Nanjing"
//        )
//    }

    suspend fun setDefaultCity(city: WeatherLocation) {
        prefStore.persistCity(city)
    }

    suspend fun favoriteCity(city: WeatherLocation) {
        withContext(dispatcher) {
            val dao = database.locationDao()
            val qe = dao.getCity(city.id)
            if (qe == null) {
                if (dao.getCount() < LIMIT) {
                    dao.insert(city.asEntity())
                } else {
                    val list = dao.getAllCities()
                    for (idx in list.size - 1 downTo LIMIT - 1) {
                        dao.delete(list[idx])
                    }
                    dao.insert(city.asEntity())
                }
            }
        }
    }

    suspend fun loadAllFavoriteCities(): List<WeatherLocation> {
        return withContext(dispatcher) {
            val dao = database.locationDao()
            dao.getAllCities()
                .map {
                    Log.d(LOG_TAG, "favorites: $it")
                    it.asModel()
                }
        }
    }
}