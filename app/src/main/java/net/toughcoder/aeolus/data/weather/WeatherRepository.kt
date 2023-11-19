package net.toughcoder.aeolus.data.weather

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.data.local.LocalDataSource
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.WeatherNow

class WeatherRepository(
    private val local: LocalDataSource,
    private val network: WeatherDataSource,
    private val dispatcher: CoroutineDispatcher,
) {
    private lateinit var nowWeatherStream: MutableStateFlow<WeatherNow>
    private lateinit var dailyWeatherStream: MutableStateFlow<List<DailyWeather>>
    private lateinit var weatherSnapshotStream: MutableStateFlow<DailyWeather>

    suspend fun getWeatherNow(location: WeatherLocation): WeatherNow {
        return withContext(dispatcher) {
            local.loadWeatherNow(location)
        }
    }

    suspend fun fetchWeatherNow(location: WeatherLocation): WeatherNow {
        return withContext(dispatcher) {
            val bundle = network.loadWeatherNow(location)
            if (bundle.successful) {
                // Update database
                local.updateWeatherNow(location, bundle)
            }
            bundle
        }
    }

    suspend fun weatherNowStream(location: WeatherLocation): Flow<WeatherNow> {
        return withContext(dispatcher) {
            val localNow = local.loadWeatherNow(location)
            nowWeatherStream = MutableStateFlow(localNow)
            nowWeatherStream.asStateFlow()
        }
    }

    suspend fun refreshWeatherNow(location: WeatherLocation) {
        withContext(dispatcher) {
            val bundle = network.loadWeatherNow(location)
            if (bundle.successful) {
                local.updateWeatherNow(location, bundle)
            }
            nowWeatherStream.update { bundle }
        }
    }

    suspend fun dailyWeatherStream(location: WeatherLocation): Flow<List<DailyWeather>> {
        return withContext(dispatcher) {
            val fromLocal = local.loadDailyWeather(location)
            dailyWeatherStream = MutableStateFlow(fromLocal)
            dailyWeatherStream.asStateFlow()
        }
    }

    suspend fun refreshDailyWeather(location: WeatherLocation) {
        withContext(dispatcher) {
            val bundle = network.loadDailyWeather(location)
            if (bundle.isNotEmpty()) {
                // update local cache
                local.updateDailyWeather(location, bundle)
                dailyWeatherStream.update { bundle }
            }
        }
    }

    suspend fun getWeatherSnapshotStream(location: WeatherLocation): Flow<DailyWeather> {
        return withContext(dispatcher) {
            val weatherList = local.loadDailyWeather(location)
            val fromLocal = if (weatherList.isEmpty()) DailyWeather() else weatherList[0]
            weatherSnapshotStream = MutableStateFlow(fromLocal)
            weatherSnapshotStream.asStateFlow()
        }
    }

    suspend fun loadWeatherSnapshot(location: WeatherLocation) {
        return withContext(dispatcher) {
            val weatherList = network.loadDailyWeather(location)
            if (weatherList.isNotEmpty()) {
                local.updateDailyWeather(location, weatherList)
                weatherSnapshotStream.update { weatherList[0] }
            }
        }
    }

    suspend fun fetchDayWeather(location: WeatherLocation): DailyWeather {
        return withContext(dispatcher) {
            val weatherList = network.loadDailyWeather(location)
            if (weatherList.isNotEmpty()) {
                local.updateDailyWeather(location, weatherList)
                weatherList[0]
            } else {
                DailyWeather()
            }
        }
    }
}