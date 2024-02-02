package net.toughcoder.aeolus.data.weather

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.async
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

    suspend fun weatherNowStream(location: WeatherLocation): Flow<WeatherNow> =
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val localNow = local.loadWeatherNow(location.id, lang, measure)
            val data = localNow?.toModel(measure) ?: WeatherNow(successful = false)
            nowWeatherStream = MutableStateFlow(data)
            nowWeatherStream.asStateFlow()
        }

    suspend fun refreshWeatherNow(location: WeatherLocation) {
        withContext(dispatcher) {
            val cityId = location.id
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val weatherJob = async {
                network.loadWeatherNow(cityId, lang, measure)
            }
            val aqiJob = async {
                network.loadAirQualityNow(cityId, lang)
            }
            val weather = weatherJob.await()
            val aqi = aqiJob.await()
            weather?.also {
                local.updateWeatherNow(cityId, it.toEntity(cityId, ""))
            }

            val now = weather?.toModel(measure, aqi?.index ?: "") ?: WeatherNow(successful = false)
            nowWeatherStream.update { now }
        }
    }

    suspend fun dailyWeatherStream(location: WeatherLocation): Flow<List<DailyWeather>> =
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val fromLocal = local.load3DayWeathers(location.id, lang, measure)
            dailyWeatherStream = MutableStateFlow(fromLocal.map { it.toModel(measure, "") })
            dailyWeatherStream.asStateFlow()
        }

    suspend fun refresh7DayWeathers(location: WeatherLocation) {
        withContext(dispatcher) {
            val cityId = location.id
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val types = listOf(1, 2, 3, 5, 7, 9)
            val weatherJob = async {
                network.load7DayWeathers(cityId, lang, measure, types)
            }
            val aqiJob = async {
                network.loadDailyAirQuality(cityId, lang)
            }
            val indexJob = async {
                network.loadDailyWeatherIndices(cityId, types, lang)
            }
            val weatherList = weatherJob.await()
            val aqiList = aqiJob.await()
            val indexMap = indexJob.await()

            if (weatherList.isEmpty()) {
                return@withContext
            }
            local.updateDailyWeather(
                cityId,
                weatherList.mapIndexed{ idx, item -> item.toEntity(cityId, idx, "") }
            )
            dailyWeatherStream.update {
                weatherList.mapIndexed { idx, dw ->
                    var clothIndex = ""
                    var coldIndex = ""
                    val indices = indexMap[dw.date]
                    if (indices != null) {
                        for (id in indices) {
                            if (id.type == "3") {
                                clothIndex = id.category
                            }
                            if (id.type == "9") {
                                coldIndex = id.category
                            }
                        }
                    }
                    dw.toModel(
                        measure = measure,
                        aqi = if (idx < aqiList.size) aqiList[idx].index else "",
                        cloth = clothIndex,
                        cold = coldIndex
                    )
                }
            }
        }
    }

    suspend fun refresh3DayWeathers(location: WeatherLocation) {
        withContext(dispatcher) {
            val cityId = location.id
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val weatherJob = async {
                network.load3DayWeathers(cityId, lang, measure)
            }
            val aqiJob = async {
                network.loadDailyAirQuality(cityId, lang)
            }
            val weatherList = weatherJob.await()
            val aqiList = aqiJob.await()
            if (weatherList.isNotEmpty()) {
                // update local cache
                local.updateDailyWeather(
                    cityId,
                    weatherList.mapIndexed{ idx, item -> item.toEntity(cityId, idx, aqiList[idx].index) }
                )
                dailyWeatherStream.update {
                    weatherList.zip(aqiList) { dw, aqi ->
                        dw.toModel(measure, aqi.index)
                    }
                }
            }
        }
    }

    suspend fun hourlyWeatherStream(location: WeatherLocation): Flow<List<HourlyWeather>> =
        withContext(dispatcher) {
            hourlyWeatherStream = MutableStateFlow(emptyList())
            hourlyWeatherStream.asStateFlow()
        }

    suspend fun refreshHourlyWeathers(location: WeatherLocation) {
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val list = network.load24HourWeathers(location.id, lang, measure)
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
            val list = network.loadWeatherIndices(location.id, types, lang)
                .map(QWeatherIndexDTO::toModel)
            if (list.isNotEmpty()) {
                weatherIndexStream.update { list }
            }
        }
    }

    suspend fun fetchDayWeather(location: WeatherLocation): DailyWeather =
        withContext(dispatcher) {
            val cityId = location.id
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val weatherList = network.load3DayWeathers(cityId, lang, measure)
            if (weatherList.isNotEmpty()) {
                local.updateDailyWeather(
                    cityId,
                    weatherList.mapIndexed{ idx, item -> item.toEntity(cityId, idx, "") }
                )
                weatherList[0].toModel(measure, "")
            } else {
                DailyWeather()
            }
        }

    suspend fun getWeatherSnapshotStream(location: WeatherLocation): Flow<DailyWeather> =
        withContext(dispatcher) {
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val weatherList = local.load3DayWeathers(location.id, lang, measure)
            val fromLocal = if (weatherList.isEmpty()) DailyWeather() else weatherList[0].toModel(measure, "")
            weatherSnapshotStream = MutableStateFlow(fromLocal)
            weatherSnapshotStream.asStateFlow()
        }

    suspend fun loadWeatherSnapshot(location: WeatherLocation) =
        withContext(dispatcher) {
            val cityId = location.id
            val lang = runBlocking { store.getLanguage().first() }
            val measure = runBlocking { store.getMeasure().first() }
            val weatherList = network.load3DayWeathers(cityId, lang, measure)
            if (weatherList.isNotEmpty()) {
                local.updateDailyWeather(
                    cityId,
                    weatherList.mapIndexed{ idx, item -> item.toEntity(cityId, idx, "") }
                )
                weatherSnapshotStream.update { weatherList[0].toModel(measure, "") }
            }
        }


    suspend fun getWeatherNow(location: WeatherLocation): WeatherNow =
        withContext(dispatcher) {
            val now = local.loadWeatherNow(location.id, DEFAULT_LANGUAGE, DEFAULT_MEASURE)
            return@withContext now?.toModel("") ?: WeatherNow(successful = false)
        }

    suspend fun fetchWeatherNow(location: WeatherLocation): WeatherNow =
        withContext(dispatcher) {
            val cityId = location.id
            val bundle = network.loadWeatherNow(cityId, DEFAULT_LANGUAGE, DEFAULT_MEASURE)
            bundle?.also {
                // Update database
                local.updateWeatherNow(cityId, it.toEntity(cityId, ""))
            }
            return@withContext bundle?.toModel("") ?: WeatherNow(successful = false)
        }
}