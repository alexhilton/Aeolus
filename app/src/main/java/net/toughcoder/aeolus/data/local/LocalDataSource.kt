package net.toughcoder.aeolus.data.local

import net.toughcoder.aeolus.data.WeatherLocation
import net.toughcoder.aeolus.data.weather.WeatherNow
import net.toughcoder.aeolus.data.weather.WeatherNowDataSource
import net.toughcoder.aeolus.data.weather.asModel

class LocalDataSource(val database: AeolusDatabase) : WeatherNowDataSource {
    override suspend fun loadWeatherNow(loc: WeatherLocation): WeatherNow {
        val dao = database.weatherNowDao()
        return dao.getByCity(loc.name)?.asModel() ?: WeatherNow()
    }

    override suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNow) {
        val dao = database.weatherNowDao()
        val entity = dao.getByCity(loc.name)
        if (entity == null) {
            dao.insert(weatherNow.asEntity(loc.name))
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