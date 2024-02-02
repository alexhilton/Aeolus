package net.toughcoder.aeolus.ui.daily

import android.net.Uri
import android.os.Bundle
import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.savedstate.SavedStateRegistryOwner
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.data.weather.WeatherRepository
import net.toughcoder.aeolus.logd
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.ui.CityState
import net.toughcoder.aeolus.ui.DailyUiState
import net.toughcoder.aeolus.ui.asUiState

@OptIn(ExperimentalCoroutinesApi::class)
class DailyWeatherViewModel(
    private val locationRepo: LocationRepository,
    private val weatherRepo: WeatherRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val cityId: String = Uri.decode(savedStateHandle.get<String>("cityId"))

    private val viewModelState = MutableStateFlow(DailyScreenUiState())
    private lateinit var weatherStream: Flow<List<DailyWeather>>

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        logd(LOG_TAG, "cityId is $cityId")
        viewModelScope.launch {
            locationRepo.getLocationInfo(cityId)
                .flatMapMerge { loc ->
                    weatherStream = weatherRepo.dailyWeatherStream(loc)
                    weatherRepo.refreshDailyWeathers(loc)
                    viewModelState.update { it.copy(city = loc.asUiState()) }
                    weatherStream
                }.collect { weathers ->
                    viewModelState.update {
                        it.copy(loading = false, dailyWeathers = weathers.map { item -> item.asUiState() })
                    }
                }

        }
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
    val loading: Boolean = true,
    val city: CityState? = null,
    val dailyWeathers: List<DailyUiState> = emptyList()
)