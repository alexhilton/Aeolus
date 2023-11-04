package net.toughcoder.aeolus.data.weather

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.update
import net.toughcoder.aeolus.data.WeatherLocation
import net.toughcoder.aeolus.data.local.LocalDataSource

class WeatherNowRepository(
    private val local: LocalDataSource,
    private val network: WeatherNowDataSource
) {
    private lateinit var stream: MutableStateFlow<WeatherNow>

    suspend fun getWeatherNow(location: WeatherLocation): WeatherNow {
        return local.loadWeatherNow(location)
    }

    suspend fun fetchWeatherNow(location: WeatherLocation): WeatherNow {
        val bundle = network.loadWeatherNow(location)
        if (bundle.successful) {
            // Update database
            local.updateWeatherNow(location, bundle)
        }
        return bundle
    }

    suspend fun weatherNowStream(location: WeatherLocation): Flow<WeatherNow> {
        val localNow = local.loadWeatherNow(location)
        stream = MutableStateFlow(localNow)
        return stream.asStateFlow()
    }

    suspend fun refreshWeatherNow(location: WeatherLocation, after: () -> Unit) {
        val bundle = network.loadWeatherNow(location)
        if (bundle.successful) {
            local.updateWeatherNow(location, bundle)
        }
        stream.update { bundle }
        after()
    }
}