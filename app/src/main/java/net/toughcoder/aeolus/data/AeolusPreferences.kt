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

class AeolusPreferences(private val dataStore: DataStore<Preferences>) {

    private val searchHistoryKey = stringPreferencesKey("search_histories")

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
}

val Context.aeolusStore: DataStore<Preferences> by preferencesDataStore(name = "aeolus_pref")