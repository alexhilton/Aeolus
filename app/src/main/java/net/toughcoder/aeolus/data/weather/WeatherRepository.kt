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
import net.toughcoder.aeolus.data.qweather.QWeatherIndexDTO
import net.toughcoder.aeolus.data.room.toEntity
import net.toughcoder.aeolus.model.DEFAULT_LANGUAGE
import net.toughcoder.aeolus.model.DEFAULT_MEASURE
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.HourlyWeather
import net.toughcoder.aeolus.model.WeatherIndex
import net.toughcoder.aeolus.model.WeatherNow
import net.toughcoder.aeolus.model.toModel

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
    private lateinit var weatherIndexStream: MutableStateFlow<List<WeatherIndex>>

    suspend fun getWeatherNow(location: WeatherLocation): WeatherNow =
        withContext(dispatcher) {
            val now = local.loadWeatherNow(location, DEFAULT_LANGUAGE, DEFAULT_MEASURE)
            return@withContext now?.toModel("") ?: WeatherNow(successful = false)
        }

    suspend fun fetchWeatherNow(location: WeatherLocation): WeatherNow =
        withContext(dispatcher) {
            val bundle = network.loadWeatherNow(location, DEFAULT_LANGUAGE, DEFAULT_MEASURE)
            bundle?.also {
                // Update database
                local.updateWeatherNow(location, it.toEntity(location.id, ""))
            }
            return@withContext bundle?.toModel("") ?: WeatherNow(successful = false)
        }

    suspend fun weatherNowStream(location: WeatherLocation): Flow<WeatherNow> =
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val localNow = local.loadWeatherNow(location, lang, measure)
            val data = localNow?.toModel(measure) ?: WeatherNow(successful = false)
            nowWeatherStream = MutableStateFlow(data)
            nowWeatherStream.asStateFlow()
        }

    suspend fun refreshWeatherNow(location: WeatherLocation) {
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val bundle = network.loadWeatherNow(location, lang, measure)
            bundle?.also {
                local.updateWeatherNow(location, it.toEntity(location.id, ""))
            }
            val now = bundle?.toModel(measure) ?: WeatherNow(successful = false)
            nowWeatherStream.update { now }
        }
    }

    suspend fun dailyWeatherStream(location: WeatherLocation): Flow<List<DailyWeather>> =
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val fromLocal = local.loadDailyWeather(location, lang, measure)
            dailyWeatherStream = MutableStateFlow(fromLocal)
            dailyWeatherStream.asStateFlow()
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
            val types = listOf(1, 2, 3, 5, 7, 9)
            val bundle = network.load7DayWeathers(location, lang, measure, types)
            if (bundle.isNotEmpty()) {
                local.updateDailyWeather(location, bundle)
                dailyWeatherStream.update { bundle }
            }
        }
    }

    suspend fun getWeatherSnapshotStream(location: WeatherLocation): Flow<DailyWeather> =
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val weatherList = local.loadDailyWeather(location, lang, measure)
            val fromLocal = if (weatherList.isEmpty()) DailyWeather() else weatherList[0]
            weatherSnapshotStream = MutableStateFlow(fromLocal)
            weatherSnapshotStream.asStateFlow()
        }

    suspend fun loadWeatherSnapshot(location: WeatherLocation) =
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val weatherList = network.loadDailyWeather(location, lang, measure)
            if (weatherList.isNotEmpty()) {
                local.updateDailyWeather(location, weatherList)
                weatherSnapshotStream.update { weatherList[0] }
            }
        }

    suspend fun fetchDayWeather(location: WeatherLocation): DailyWeather =
        withContext(dispatcher) {
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

    suspend fun hourlyWeatherStream(location: WeatherLocation): Flow<List<HourlyWeather>> =
        withContext(dispatcher) {
            hourlyWeatherStream = MutableStateFlow(emptyList())
            hourlyWeatherStream.asStateFlow()
        }

    suspend fun fetchHourlyWeathers(location: WeatherLocation) {
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val list = network.load24HourWeathers(location, lang, measure)
                .map { it.toModel(measure) }
            if (list.isNotEmpty()) {
                hourlyWeatherStream.update { list }
            }
        }
    }

    suspend fun weatherIndexStream(location: WeatherLocation): Flow<List<WeatherIndex>> =
        withContext(dispatcher) {
            weatherIndexStream = MutableStateFlow(emptyList())
            weatherIndexStream
        }

    suspend fun refreshWeatherIndices(location: WeatherLocation) {
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val types = listOf(1, 2, 3, 5, 7, 9)
            val list = network.loadWeatherIndices(location, types, lang)
                .map(QWeatherIndexDTO::toModel)
            if (list.isNotEmpty()) {
                weatherIndexStream.update { list }
            }
        }
    }
}