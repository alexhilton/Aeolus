package net.toughcoder.aeolus.data.local

import net.toughcoder.aeolus.data.room.AeolusDatabase
import net.toughcoder.aeolus.data.room.asEntity
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.WeatherNow
import net.toughcoder.aeolus.data.weather.WeatherNowDataSource
import net.toughcoder.aeolus.model.asModel

class LocalDataSource(private val database: AeolusDatabase) : WeatherNowDataSource {
    override suspend fun loadWeatherNow(loc: WeatherLocation): WeatherNow {
        val dao = database.weatherNowDao()
        return dao.getByCityId(loc.name)?.asModel() ?: WeatherNow()
    }

    override suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNow) {
        val dao = database.weatherNowDao()
        val entity = dao.getByCityId(loc.id)
        if (entity == null) {
            dao.insert(weatherNow.asEntity(loc.id))
        } else {
            with(weatherNow) {
                entity.copy(
                    updateTime = updateTime,
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
}