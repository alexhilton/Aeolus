package net.toughcoder.aeolus.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import net.toughcoder.aeolus.model.WeatherLocation

/**
 * Store key-value data.
 */
class AeolusStore(private val dataStore: DataStore<Preferences>) {

    private val searchHistoryKey = stringPreferencesKey("search_histories")
    private val defaultCityKey = stringPreferencesKey("default_city")

    fun getSearchHistories(): Flow<List<String>> {
        return dataStore.data.map { prefs ->
            val bundle = prefs[searchHistoryKey]
            bundle?.split(";")?.toList() ?: listOf()
        }
    }

    suspend fun addSearchHistory(history: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                val bundle = prefs[searchHistoryKey]
                if (bundle == null) {
                    prefs[searchHistoryKey] = history
                } else {
                    val list = bundle.split(";").toMutableList()
                    if (list.size > 5) {
                        list.removeLast()
                    }
                    val sb = StringBuilder()
                    sb.append(history)
                    for (x in list) {
                        sb.append(";")
                        sb.append(x)
                    }
                    prefs[searchHistoryKey] = sb.toString()
                }
            }
        }
    }

    suspend fun persistCity(city: WeatherLocation) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[defaultCityKey] = "${city.id};${city.name};${city.admin}"
            }
        }
    }

    fun getDefaultCity(): Flow<WeatherLocation> {
        return dataStore.data.map { prefs ->
            val bundle = prefs[defaultCityKey]
            if (bundle.isNullOrEmpty()) {
                WeatherLocation()
            } else {
                val parts = bundle.split(";")
                WeatherLocation(id = parts[0], name = parts[1], admin = parts[2])
            }
        }
    }
}

val Context.aeolusStore: DataStore<Preferences> by preferencesDataStore(name = "aeolus_pref")