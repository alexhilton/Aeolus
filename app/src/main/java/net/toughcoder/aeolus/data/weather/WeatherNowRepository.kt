package net.toughcoder.aeolus.data.weather

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext
import net.toughcoder.aeolus.data.WeatherLocation
import net.toughcoder.aeolus.data.local.LocalDataSource

class WeatherNowRepository(
    private val local: LocalDataSource,
    private val network: WeatherNowDataSource,
    private val dispatcher: CoroutineDispatcher,
) {
    private lateinit var stream: MutableStateFlow<WeatherNow>

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
            stream = MutableStateFlow(localNow)
            stream.asStateFlow()
        }
    }

    suspend fun refreshWeatherNow(location: WeatherLocation) {
        withContext(dispatcher) {
            val bundle = network.loadWeatherNow(location)
            if (bundle.successful) {
                local.updateWeatherNow(location, bundle)
            }
            stream.update { bundle }
        }
    }
}