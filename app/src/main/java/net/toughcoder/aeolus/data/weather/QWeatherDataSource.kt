package net.toughcoder.aeolus.data.weather

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.toughcoder.aeolus.data.qweather.QWeatherAirDTO
import net.toughcoder.aeolus.data.qweather.QWeatherDayDTO
import net.toughcoder.aeolus.data.qweather.QWeatherHourDTO
import net.toughcoder.aeolus.data.qweather.QWeatherIndexDTO
import net.toughcoder.aeolus.data.qweather.QWeatherNowDTO
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.data.qweather.QWeatherService
import net.toughcoder.aeolus.data.room.DailyWeatherEntity
import net.toughcoder.aeolus.data.room.WeatherNowEntity
import net.toughcoder.aeolus.logd
import net.toughcoder.aeolus.model.MEASURE_IMPERIAL
import net.toughcoder.aeolus.model.toParamLang
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class QWeatherDataSource(
    private val api: QWeatherService,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : WeatherDataSource {
    companion object {
        const val LOG_TAG = "QWeatherNowDataSource"
    }
    override suspend fun loadWeatherNow(loc: String, lang: String, measure: String): QWeatherNowDTO? =
        withContext(dispatcher) {
            try {
                val response = api.fetchWeatherNow(loc, toParamLang(lang), toParamMeasure(measure))
                if (response.code == "200") {
                    return@withContext response.now
                } else {
                    logd(LOG_TAG, "loadWeatherNow: Error code: ${response.code}")
                }
            } catch (exception: Exception) {
                logd(LOG_TAG, "Failed to load now weather ${exception.message}")
            }
            return@withContext null
        }

    private fun parseTime(t: String): Long {
        val d = LocalDateTime.parse(t, DateTimeFormatter.ISO_DATE_TIME)
        logd(LOG_TAG, "parseTime $t -> $d, long ${d.toEpochSecond(ZoneOffset.UTC)}")
        return d.toEpochSecond(ZoneOffset.UTC)
    }

    override suspend fun load3DayWeathers(loc: WeatherLocation, lang: String, measure: String): List<QWeatherDayDTO> =
        withContext(dispatcher) {
            try {
                val weather = api.fetchWeather3D(loc.id, toParamLang(lang), toParamMeasure(measure))
                if (weather.code == "200") {
                    return@withContext weather.dayList
                } else {
                    logd(LOG_TAG, "Bad response ${weather.code}")
                }
            } catch(exception: Exception) {
                logd(LOG_TAG, "Failed to load daily weather ${exception.message}")
            }
            return@withContext emptyList()
        }

    override suspend fun load7DayWeathers(
        loc: WeatherLocation, lang: String, measure: String, types: List<Int>
    ): List<QWeatherDayDTO> =
        withContext(dispatcher) {
            try {
                val weather = api.fetchWeather7D(loc.id, toParamLang(lang), toParamMeasure(measure))
                return@withContext weather.dayList
            } catch(exception: Exception) {
                logd(LOG_TAG, "Failed to load 7 days weather ${exception.message}")
            }
            return@withContext emptyList()
        }

    override suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNowEntity) {
        // Not implemented
    }

    override suspend fun updateDailyWeather(
        loc: WeatherLocation,
        dailyWeathers: List<DailyWeatherEntity>
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun load24HourWeathers(loc: WeatherLocation, lang: String, measure: String): List<QWeatherHourDTO> =
        withContext(dispatcher) {
            try {
                val response = api.fetchWeather24H(loc.id, toParamLang(lang), toParamMeasure(measure))
                return@withContext if (response.code == "200") {
                    response.hourList //.map { it.toModel(measure) }
                } else {
                    logd(LOG_TAG, "failed to load24HourWeathers: ${response.code}")
                    emptyList()
                }
            } catch (exception: Exception) {
                logd(LOG_TAG, "Failed to load 24 hour weather ${exception.message}")
            }
            return@withContext emptyList()
        }

    private fun toParamMeasure(measure: String) =
        if (measure == MEASURE_IMPERIAL) "i" else "m"

    override suspend fun loadWeatherIndices(
        loc: WeatherLocation,
        type: List<Int>,
        lang: String
    ): List<QWeatherIndexDTO> =
        withContext(dispatcher) {
        try {
            val response =
                api.fetchWeatherIndices(loc.id, type.joinToString(","), toParamLang(lang))
            logd(LOG_TAG, "WeatherIndex: res code: ${response.code}")
            if (response.code == "200") {
                return@withContext response.indexList //.map { it.toModel() }
            }
        } catch (excep: Exception) {
            logd(LOG_TAG, "WeatherIndex: excep: ${excep.message}")
        }
        return@withContext emptyList()
    }

    override suspend fun loadDailyWeatherIndices(
        loc: WeatherLocation,
        types: List<Int>,
        lang: String
    ): Map<String, List<QWeatherIndexDTO>> =
        withContext(dispatcher) {
            try {
                val response =
                    api.fetch3DWeatherIndices(loc.id, types.joinToString(","), toParamLang(lang))
                if (response.code == "200") {
                    return@withContext response.indexList
                        .groupBy { it.date }
                } else {
                    logd(LOG_TAG, "Bad response: ${response.code}")
                }
            } catch (e: Exception) {
                logd(LOG_TAG, "Failed to load daily weather indices: ${e.message}")
            }
            return@withContext emptyMap()
        }

    override suspend fun loadAirQualityNow(loc: WeatherLocation, lang: String): QWeatherAirDTO? =
        withContext(dispatcher) {
            try {
                val response = api.fetchAQINow(loc.id, toParamLang(lang))
                if (response.code == "200") {
                    return@withContext response.now
                }
            } catch (excp: Exception) {
                logd(LOG_TAG, "loadAirQualityNow: Exception: ${excp.message}")
            }
            return@withContext null
        }

    override suspend fun loadDailyAirQuality(
        loc: WeatherLocation,
        lang: String
    ): List<QWeatherAirDTO> =
        withContext(dispatcher) {
            try {
                val response = api.fetchAQIDaily(loc.id, toParamLang(lang))
                if (response.code == "200") {
                    return@withContext response.dailyAirs
                } else {
                    logd(LOG_TAG, "Error response: ${response.code}")
                }
            } catch (e: Exception) {
                logd(LOG_TAG,"Failed to load daily AQI: ${e.message}")
            }
            return@withContext emptyList()
        }
}