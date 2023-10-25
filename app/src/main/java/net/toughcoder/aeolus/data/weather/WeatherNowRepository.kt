package net.toughcoder.aeolus.data.weather

import net.toughcoder.aeolus.data.WeatherLocation

class WeatherNowRepository(
    private val datasource: WeatherNowDatasource
) {
    suspend fun getWeatherNow(location: WeatherLocation): WeatherNow {
        return datasource.loadWeatherNow(location)
    }
}