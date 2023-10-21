package net.toughcoder.aeolus.data

import android.os.SystemClock
import kotlin.random.Random

class WeatherNowRepository {
    fun getWeatherNow(location: WeatherLocation): WeatherNow {
        return fakeNow()
    }
}

fun fakeNow() = WeatherNow(
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
    updateTime = SystemClock.uptimeMillis()
)