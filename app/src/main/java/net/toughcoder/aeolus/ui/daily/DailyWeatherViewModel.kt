package net.toughcoder.aeolus.ui.daily

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.data.weather.WeatherRepository

class DailyWeatherViewModel(
    private val locationRepo: LocationRepository,
    private val weatherRepo: WeatherRepository
) : ViewModel() {
    companion object {
        fun providerFactory(
            locationRepo: LocationRepository,
            weatherRepo: WeatherRepository
        ) : ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return DailyWeatherViewModel(locationRepo, weatherRepo) as T
                }
            }
    }
}