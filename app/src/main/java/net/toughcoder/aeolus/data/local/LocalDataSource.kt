package net.toughcoder.aeolus.data.local

import net.toughcoder.aeolus.data.qweather.QWeatherAirDTO
import net.toughcoder.aeolus.data.qweather.QWeatherDayDTO
import net.toughcoder.aeolus.data.qweather.QWeatherHourDTO
import net.toughcoder.aeolus.data.qweather.QWeatherIndexDTO
import net.toughcoder.aeolus.data.qweather.QWeatherNowDTO
import net.toughcoder.aeolus.data.room.AeolusDatabase
import net.toughcoder.aeolus.data.room.DailyWeatherEntity
import net.toughcoder.aeolus.data.room.WeatherNowEntity
import net.toughcoder.aeolus.data.room.asDTO
import net.toughcoder.aeolus.data.room.toEntity
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.data.weather.WeatherDataSource
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.toModel

class LocalDataSource(private val database: AeolusDatabase) : WeatherDataSource {
    override suspend fun loadWeatherNow(loc: WeatherLocation, lang: String, measure: String): QWeatherNowDTO? {
        val dao = database.weatherNowDao()
        return dao.getByCityId(loc.name)?.asDTO()
    }

    override suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNowEntity) {
        val dao = database.weatherNowDao()
        val entity = dao.getByCityId(loc.id)
        if (entity == null) {
            dao.insert(weatherNow)
        } else {
            with(weatherNow) {
                entity.copy(
                    nowTemp = nowTemp,
                    feelsLike = feelsLike,
                    icon = icon,
                    text = text,
                    windDegree = windDegree,
                    windDir = windDir,
                    windScale = windScale,
                    humidity = humidity,
                    airPressure = airPressure,
                    visibility = visibility
                )
            }.also {
                dao.update(it)
            }
        }
    }

    override suspend fun load3DayWeathers(loc: WeatherLocation, lang: String, measure: String): List<DailyWeather> {
        val dao = database.dailyWeatherDao()
        val weathers = dao.getDailyWeathers(loc.id)
        return weathers.map { it.toModel(measure) }
    }

    override suspend fun load7DayWeathers(loc: WeatherLocation, lang: String, measure: String, types: List<Int>): List<QWeatherDayDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun updateDailyWeather(
        loc: WeatherLocation,
        dailyWeathers: List<DailyWeatherEntity>
    ) {
        val dao = database.dailyWeatherDao()
        dao.addDailyWeathers(dailyWeathers)
    }

    override suspend fun load24HourWeathers(loc: WeatherLocation, lang: String, measure: String): List<QWeatherHourDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun loadWeatherIndices(
        loc: WeatherLocation,
        type: List<Int>,
        lang: String
    ): List<QWeatherIndexDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun loadDailyWeatherIndices(
        loc: WeatherLocation,
        type: List<Int>,
        lang: String
    ): Map<String, List<QWeatherIndexDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun loadAirQualityNow(loc: WeatherLocation, lang: String): QWeatherAirDTO? {
        TODO("Not yet implemented")
    }

    override suspend fun loadDailyAirQuality(
        loc: WeatherLocation,
        lang: String
    ): List<QWeatherAirDTO> {
        TODO("Not yet implemented")
    }
}