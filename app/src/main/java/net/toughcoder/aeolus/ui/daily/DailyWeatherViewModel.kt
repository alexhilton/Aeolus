package net.toughcoder.aeolus.ui.daily

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.savedstate.SavedStateRegistryOwner
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.data.weather.WeatherRepository
import net.toughcoder.aeolus.ui.CityState
import net.toughcoder.aeolus.ui.favorites.DayWeatherUiState

class DailyWeatherViewModel(
    private val locationRepo: LocationRepository,
    private val weatherRepo: WeatherRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cityId: String = Uri.decode(savedStateHandle.get<String>("cityId"))

    init {
        Log.d(LOG_TAG, "cityId is $cityId")
    }

    companion object {
        const val LOG_TAG = "DailyViewModel"

        fun providerFactory(
            locationRepo: LocationRepository,
            weatherRepo: WeatherRepository,
            owner: SavedStateRegistryOwner,
            defaultArgs: Bundle? = null
        ) : AbstractSavedStateViewModelFactory =
            object : AbstractSavedStateViewModelFactory(owner, defaultArgs) {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(
                    key: String,
                    modelClass: Class<T>,
                    handle: SavedStateHandle
                ): T {
                    return DailyWeatherViewModel(locationRepo, weatherRepo, handle) as T
                }
            }
    }
}

data class DailyScreenUiState(
    val city: CityState,
    val dailyWeathers: List<DayWeatherUiState>
)