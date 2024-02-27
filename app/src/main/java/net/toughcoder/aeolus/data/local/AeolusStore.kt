package net.toughcoder.aeolus.data.local

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.toughcoder.aeolus.logd
import net.toughcoder.aeolus.model.DEFAULT_LANGUAGE
import net.toughcoder.aeolus.model.DEFAULT_MEASURE
import net.toughcoder.aeolus.model.KEY_LANGUAGE
import net.toughcoder.aeolus.model.KEY_MEASURE
import net.toughcoder.aeolus.model.LANGUAGE_AUTO
import net.toughcoder.aeolus.model.TYPE_NORMAL
import net.toughcoder.aeolus.model.WeatherLocation

/**
 * Store key-value data.
 */
class AeolusStore(private val dataStore: DataStore<Preferences>) {
    companion object {
        const val LOG_TAG = "AeolusStore"
    }

    private val searchHistoryKey = stringPreferencesKey("search_histories")
    private val defaultCityKey = stringPreferencesKey("default_city")
    private val languageKey = stringPreferencesKey(KEY_LANGUAGE)
    private val measureKey = stringPreferencesKey(KEY_MEASURE)

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
                prefs[defaultCityKey] = "${city.id};${city.name};${city.admin};${city.type}"
            }
        }
    }

    suspend fun removeCity() {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs.remove(defaultCityKey)
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
                val type = if (parts.size > 3) parts[3].toInt() else TYPE_NORMAL
                WeatherLocation(id = parts[0], name = parts[1], admin = parts[2], type = type)
            }
        }.distinctUntilChanged()
    }

    fun getLanguage(): Flow<String> {
        return dataStore.data.map { prefs ->
            prefs[languageKey] ?: DEFAULT_LANGUAGE
        }
    }

    suspend fun persistLanguage(lang: String) {
        val locale = if (lang == LANGUAGE_AUTO) {
            LocaleListCompat.getEmptyLocaleList()
        } else {
            LocaleListCompat.forLanguageTags(lang)
        }
        logd(LOG_TAG, "set lang $locale")
        AppCompatDelegate.setApplicationLocales(locale)

        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[languageKey] = lang
                logd(LOG_TAG, "persistLang $lang")
            }
        }
    }

    fun getMeasure(): Flow<String> {
        return dataStore.data.map { prefs ->
            prefs[measureKey] ?: DEFAULT_MEASURE
        }
    }

    suspend fun persistMeasure(measure: String) {
        withContext(Dispatchers.IO) {
            dataStore.edit { prefs ->
                prefs[measureKey] = measure
                logd(LOG_TAG, "persistMeasure $measure")
            }
        }
    }
}

val Context.aeolusStore: DataStore<Preferences> by preferencesDataStore(name = "aeolus_pref")