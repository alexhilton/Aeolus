package net.toughcoder.aeolus.data

import android.os.SystemClock
import kotlinx.coroutines.delay
import kotlin.random.Random

class WeatherNowRepository {
    suspend fun getWeatherNow(location: WeatherLocation): WeatherNow {
        delay(((Random.nextFloat() + 0.1f) * 3000f).toLong())
        val hasError = Random.nextInt(15) % 4 == 0
        val data = fakeNow()
        return if (hasError) {
            data.copy(successful = false, updateTime = -1)
        } else {
            data
        }
    }
}

fun fakeNow() = WeatherNow(
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
    updateTime = SystemClock.uptimeMillis()
)