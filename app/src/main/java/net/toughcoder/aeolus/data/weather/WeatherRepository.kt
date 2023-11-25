package net.toughcoder.aeolus.data.weather

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import net.toughcoder.aeolus.data.local.AeolusStore
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.data.local.LocalDataSource
import net.toughcoder.aeolus.model.DEFAULT_LANGUAGE
import net.toughcoder.aeolus.model.DEFAULT_MEASURE
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.HourlyWeather
import net.toughcoder.aeolus.model.WeatherNow

class WeatherRepository(
    private val store: AeolusStore,
    private val local: LocalDataSource,
    private val network: WeatherDataSource,
    private val dispatcher: CoroutineDispatcher,
) {
    private lateinit var nowWeatherStream: MutableStateFlow<WeatherNow>
    private lateinit var dailyWeatherStream: MutableStateFlow<List<DailyWeather>>
    private lateinit var weatherSnapshotStream: MutableStateFlow<DailyWeather>
    private lateinit var hourlyWeatherStream: MutableStateFlow<List<HourlyWeather>>

    suspend fun getWeatherNow(location: WeatherLocation): WeatherNow {
        return withContext(dispatcher) {
            local.loadWeatherNow(location, DEFAULT_LANGUAGE, DEFAULT_MEASURE)
        }
    }

    suspend fun fetchWeatherNow(location: WeatherLocation): WeatherNow {
        return withContext(dispatcher) {
            val bundle = network.loadWeatherNow(location, DEFAULT_LANGUAGE, DEFAULT_MEASURE)
            if (bundle.successful) {
                // Update database
                local.updateWeatherNow(location, bundle)
            }
            bundle
        }
    }

    suspend fun weatherNowStream(location: WeatherLocation): Flow<WeatherNow> {
        return withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val localNow = local.loadWeatherNow(location, lang, measure)
            nowWeatherStream = MutableStateFlow(localNow)
            nowWeatherStream.asStateFlow()
        }
    }

    suspend fun refreshWeatherNow(location: WeatherLocation) {
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val bundle = network.loadWeatherNow(location, lang, measure)
            if (bundle.successful) {
                local.updateWeatherNow(location, bundle)
            }
            nowWeatherStream.update { bundle }
        }
    }

    suspend fun dailyWeatherStream(location: WeatherLocation): Flow<List<DailyWeather>> {
        return withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val fromLocal = local.loadDailyWeather(location, lang, measure)
            dailyWeatherStream = MutableStateFlow(fromLocal)
            dailyWeatherStream.asStateFlow()
        }
    }

    suspend fun fetch3DayWeathers(location: WeatherLocation) {
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val bundle = network.loadDailyWeather(location, lang, measure)
            if (bundle.isNotEmpty()) {
                // update local cache
                local.updateDailyWeather(location, bundle)
                dailyWeatherStream.update { bundle }
            }
        }
    }

    suspend fun fetch7DayWeathers(location: WeatherLocation) {
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val bundle = network.load7DayWeathers(location, lang, measure)
            if (bundle.isNotEmpty()) {
                local.updateDailyWeather(location, bundle)
                dailyWeatherStream.update { bundle }
            }
        }
    }

    suspend fun getWeatherSnapshotStream(location: WeatherLocation): Flow<DailyWeather> {
        return withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val weatherList = local.loadDailyWeather(location, lang, measure)
            val fromLocal = if (weatherList.isEmpty()) DailyWeather() else weatherList[0]
            weatherSnapshotStream = MutableStateFlow(fromLocal)
            weatherSnapshotStream.asStateFlow()
        }
    }

    suspend fun loadWeatherSnapshot(location: WeatherLocation) {
        return withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val weatherList = network.loadDailyWeather(location, lang, measure)
            if (weatherList.isNotEmpty()) {
                local.updateDailyWeather(location, weatherList)
                weatherSnapshotStream.update { weatherList[0] }
            }
        }
    }

    suspend fun fetchDayWeather(location: WeatherLocation): DailyWeather {
        return withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val weatherList = network.loadDailyWeather(location, lang, measure)
            if (weatherList.isNotEmpty()) {
                local.updateDailyWeather(location, weatherList)
                weatherList[0]
            } else {
                DailyWeather()
            }
        }
    }

    suspend fun hourlyWeatherStream(location: WeatherLocation): Flow<List<HourlyWeather>> {
        return withContext(dispatcher) {
            hourlyWeatherStream = MutableStateFlow(emptyList())
            hourlyWeatherStream.asStateFlow()
        }
    }

    suspend fun fetchHourlyWeathers(location: WeatherLocation) {
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            hourlyWeatherStream.update { network.load24HourWeathers(location, lang, measure) }
        }
    }
}