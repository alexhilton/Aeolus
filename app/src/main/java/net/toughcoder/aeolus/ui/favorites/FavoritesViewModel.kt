package net.toughcoder.aeolus.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.data.weather.WeatherRepository
import net.toughcoder.aeolus.ui.CityState
import net.toughcoder.aeolus.ui.DailyUiState
import net.toughcoder.aeolus.ui.asUiState

class FavoritesViewModel(
    private val locationRepo: LocationRepository,
    private val weatherRepo: WeatherRepository
) : ViewModel() {

    fun getAllFavorites(): Flow<List<FavoriteUiState>> = flow {
        locationRepo.getDefaultCity()
            .collect { defaultCity ->
                emit(
                    locationRepo.loadAllFavoriteCities()
                        .map {
                            val weather = weatherRepo.fetchDayWeather(it)
                            FavoriteUiState(
                                city = it.asUiState(),
                                snapshot = weather.asUiState(),
                                selected = it.id == defaultCity.id
                            )
                        }
                )
            }
    }

    fun setDefaultCity(city: CityState) {
        viewModelScope.launch {
            locationRepo.setDefaultCity(city.toModel())
        }
    }

    companion object {
        fun providerFactory(
            locationRepo: LocationRepository,
            weatherRepo: WeatherRepository
        ) : ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FavoritesViewModel(locationRepo, weatherRepo) as T
                }
            }
    }
}

data class FavoriteUiState(
    val city: CityState,
    val snapshot: DailyUiState,
    val selected: Boolean = false
)