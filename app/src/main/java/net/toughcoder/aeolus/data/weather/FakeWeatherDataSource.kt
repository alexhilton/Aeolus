package net.toughcoder.aeolus.data.weather

import android.os.SystemClock
import kotlinx.coroutines.delay
import net.toughcoder.aeolus.model.AirQuality
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.DailyWeatherIndex
import net.toughcoder.aeolus.model.HourlyWeather
import net.toughcoder.aeolus.model.WeatherIndex
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.WeatherNow
import kotlin.random.Random

class FakeWeatherDataSource : WeatherDataSource {
    override suspend fun loadWeatherNow(loc: WeatherLocation, lang: String, measure: String): WeatherNow {
        delay(((Random.nextFloat() + 0.1f) * 3000f).toLong())
        val hasError = Random.nextInt(15) % 4 == 0
        val data = fakeNow()
        return if (hasError) {
            data.copy(successful = false, updateTime = -1)
        } else {
            data
        }
    }

    override suspend fun updateWeatherNow(loc: WeatherLocation, weatherNow: WeatherNow) {
        TODO("Not yet implemented")
    }

    private fun fakeNow() = WeatherNow(
        successful = true,
        (Random.nextFloat() * 40f).toString(),
        (Random.nextFloat() * 70f).toString(),
        "101",
        "多云",
        Random.nextInt(360).toString(),
        "东南风",
        Random.nextInt(12).toString(),
        Random.nextInt(100).toString(),
        Random.nextInt(100).toString(),
        Random.nextInt(10000).toString(),
        Random.nextInt(400).toString(),
        "10",
        updateTime = SystemClock.uptimeMillis() / 1000L
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

    override suspend fun load24HourWeathers(loc: WeatherLocation, lang: String, measure: String): List<HourlyWeather> {
        TODO("Not yet implemented")
    }

    override suspend fun loadAirQualityNow(loc: WeatherLocation, lang: String): AirQuality {
        TODO("Not yet implemented")
    }

    override suspend fun loadWeatherIndices(
        loc: WeatherLocation,
        type: List<Int>,
        lang: String
    ): List<WeatherIndex> {
        TODO("Not yet implemented")
    }
}