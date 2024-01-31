package net.toughcoder.aeolus.data.weather

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.toughcoder.aeolus.data.qweather.QWeatherHourDTO
import net.toughcoder.aeolus.data.qweather.QWeatherIndexDTO
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.data.qweather.QWeatherService
import net.toughcoder.aeolus.logd
import net.toughcoder.aeolus.model.AirQuality
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.DailyWeatherIndex
import net.toughcoder.aeolus.model.HourlyWeather
import net.toughcoder.aeolus.model.MEASURE_IMPERIAL
import net.toughcoder.aeolus.model.WeatherIndex
import net.toughcoder.aeolus.model.WeatherNow
import net.toughcoder.aeolus.model.toModel
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
    override suspend fun loadWeatherNow(loc: WeatherLocation, lang: String, measure: String): WeatherNow {
        try {
            val weatherResponsne = api.fetchWeatherNow(loc.id, toParamLang(lang), toParamMeasure(measure))
            val airResponse = api.fetchAQINow(loc.id, toParamLang(lang))
            val aqi = if (airResponse.code == "200") airResponse.now.index else ""
            return if (weatherResponsne.code == "200") {
                with(weatherResponsne.now) {
                    WeatherNow(
                        successful = true,
                        nowTemp = temp,
                        feelsLike = feelsLike,
                        icon = icon,
                        text = text,
                        windDegree = windDegree,
                        windDir = windDir,
                        windScale = windScale,
                        windSpeed = windSpeed,
                        humidity = humidity,
                        airPressure = pressure,
                        visibility = visibility,
                        cloud = cloud,
                        updateTime = parseTime(weatherResponsne.updateTime),
                        measure = measure,
                        airQualityIndex = aqi
                    )
                }
            } else {
                logd(LOG_TAG, "loadWeatherNow: Error code: ${weatherResponsne.code}")
                WeatherNow(successful = false)
            }
        } catch (exception: Exception) {
            logd(LOG_TAG, "Failed to load now weather ${exception.message}")
            return WeatherNow(successful = false)
        }
    }

    private fun parseTime(t: String): Long {
        val d = LocalDateTime.parse(t, DateTimeFormatter.ISO_DATE_TIME)
        logd(LOG_TAG, "parseTime $t -> $d, long ${d.toEpochSecond(ZoneOffset.UTC)}")
        return d.toEpochSecond(ZoneOffset.UTC)
    }

    override suspend fun loadDailyWeather(loc: WeatherLocation, lang: String, measure: String): List<DailyWeather> {
        try {
            val weather = api.fetchWeather3D(loc.id, toParamLang(lang), toParamMeasure(measure))
            val airResponse = api.fetchAQIDaily(loc.id, toParamLang(lang))
            val airList = if (airResponse.code == "200") airResponse.dailyAirs else emptyList()
            return if (weather.code == "200") {
                weather.dayList.zip(airList) { w, air ->
                    w.toModel(measure, air.index)
                }
            } else {
                logd(LOG_TAG, "failed to loadDailyWeather: ${weather.code}")
                emptyList()
            }
        } catch(exception: Exception) {
            logd(LOG_TAG, "Failed to load daily weather ${exception.message}")
        }
        return emptyList()
    }

    override suspend fun load7DayWeathers(
        loc: WeatherLocation, lang: String, measure: String, types: List<Int>
    ): List<DailyWeather> {
        try {
            val weather = api.fetchWeather7D(loc.id, toParamLang(lang), toParamMeasure(measure))
            val airResponse = api.fetchAQIDaily(loc.id, toParamLang(lang))
            val indexResponse = api.fetch3DWeatherIndices(loc.id, types.joinToString(","), toParamLang(lang))
            val indexMap = if (indexResponse.code == "200") {
                indexResponse.indexList
                    .map { it.toModel() }
                    .groupBy { it.date }
            } else {
                emptyMap()
            }
            val airList = if (airResponse.code == "200") airResponse.dailyAirs else emptyList()
            return if (weather.code == "200") {
                weather.dayList.mapIndexed{ idx, dw ->
                    var clothIndex = ""
                    var coldIndex = ""
                    val indices = indexMap[dw.date]
                    if (indices != null) {
                        for (idx in indices) {
                            if (idx.type == 3) {
                                clothIndex = idx.category
                            }
                            if (idx.type == 9) {
                                coldIndex = idx.category
                            }
                        }
                    }
                    dw.toModel(
                        measure = measure,
                        aqi = if (idx < airList.size) airList[idx].index else "",
                        cloth = clothIndex,
                        cold = coldIndex
                    )
                }
            } else {
                logd(LOG_TAG, "failed to load7DayWeathers: ${weather.code}")
                emptyList()
            }
        } catch(exception: Exception) {
            logd(LOG_TAG, "Failed to load 7 days weather ${exception.message}")
        }
        return emptyList()
    }

    override suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNow) {
        // Not implemented
    }

    override suspend fun updateDailyWeather(
        loc: WeatherLocation,
        dailyWeathers: List<DailyWeather>
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
}