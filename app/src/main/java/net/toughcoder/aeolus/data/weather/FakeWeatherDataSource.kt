package net.toughcoder.aeolus.data.weather

import kotlinx.coroutines.delay
import net.toughcoder.aeolus.data.qweather.QWeatherAirDTO
import net.toughcoder.aeolus.data.qweather.QWeatherHourDTO
import net.toughcoder.aeolus.data.qweather.QWeatherIndexDTO
import net.toughcoder.aeolus.data.qweather.QWeatherNowDTO
import net.toughcoder.aeolus.data.room.WeatherNowEntity
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.WeatherLocation
import kotlin.random.Random

class FakeWeatherDataSource : WeatherDataSource {
    override suspend fun loadWeatherNow(loc: WeatherLocation, lang: String, measure: String): QWeatherNowDTO? {
        delay(((Random.nextFloat() + 0.1f) * 3000f).toLong())
        val hasError = Random.nextInt(15) % 4 == 0
        return fakeNow()
    }

    override suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNowEntity) {
        TODO("Not yet implemented")
    }

    private fun fakeNow() = QWeatherNowDTO(
        temp = (Random.nextFloat() * 40f).toString(),
        feelsLike = (Random.nextFloat() * 70f).toString(),
        icon = "101",
        text = "多云",
        windDegree = Random.nextInt(360).toString(),
        windDir = "东南风",
        windScale = Random.nextInt(12).toString(),
        windSpeed = Random.nextInt(100).toString(),
        visibility = Random.nextInt(100).toString(),
        pressure = Random.nextInt(10000).toString(),
        humidity = Random.nextInt(400).toString(),
        cloud = "10"
    )

    override suspend fun loadDailyWeather(loc: WeatherLocation, lang: String, measure: String): List<DailyWeather> {
        TODO("Not yet implemented")
    }


    override suspend fun load7DayWeathers(loc: WeatherLocation, lang: String, measure: String, types: List<Int>): List<DailyWeather> {
        TODO("Not yet implemented")
    }

    override suspend fun updateDailyWeather(
        loc: WeatherLocation,
        dailyWeathers: List<DailyWeather>
    ) {
        TODO("Not yet implemented")
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

    override suspend fun loadAirQualityNow(loc: WeatherLocation, lang: String): QWeatherAirDTO? {
        TODO("Not yet implemented")
    }
}