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
import net.toughcoder.aeolus.data.room.toDTO
import net.toughcoder.aeolus.data.weather.WeatherDataSource

class LocalDataSource(private val database: AeolusDatabase) : WeatherDataSource {
    override suspend fun loadWeatherNow(loc: String, lang: String, measure: String): QWeatherNowDTO? {
        val dao = database.weatherNowDao()
        return dao.getByCityId(loc)?.asDTO()
    }

    override suspend fun updateWeatherNow(loc: String, weatherNow: WeatherNowEntity) {
        val dao = database.weatherNowDao()
        val entity = dao.getByCityId(loc)
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

    override suspend fun load3DayWeathers(loc: String, lang: String, measure: String): List<QWeatherDayDTO> {
        val dao = database.dailyWeatherDao()
        val weathers = dao.getDailyWeathers(loc)
        return weathers.map(DailyWeatherEntity::toDTO)
    }

    override suspend fun load7DayWeathers(loc: String, lang: String, measure: String, types: List<Int>): List<QWeatherDayDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun updateDailyWeather(
        loc: String,
        dailyWeathers: List<DailyWeatherEntity>
    ) {
        val dao = database.dailyWeatherDao()
        dao.addDailyWeathers(dailyWeathers)
    }

    override suspend fun load24HourWeathers(loc: String, lang: String, measure: String): List<QWeatherHourDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun loadWeatherIndices(
        loc: String,
        type: List<Int>,
        lang: String
    ): List<QWeatherIndexDTO> {
        TODO("Not yet implemented")
    }

    override suspend fun loadDailyWeatherIndices(
        loc: String,
        type: List<Int>,
        lang: String
    ): Map<String, List<QWeatherIndexDTO>> {
        TODO("Not yet implemented")
    }

    override suspend fun loadAirQualityNow(loc: String, lang: String): QWeatherAirDTO? {
        TODO("Not yet implemented")
    }

    override suspend fun loadDailyAirQuality(
        loc: String,
        lang: String
    ): List<QWeatherAirDTO> {
        TODO("Not yet implemented")
    }
}