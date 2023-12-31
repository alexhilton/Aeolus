package net.toughcoder.aeolus.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.data.weather.WeatherRepository
import net.toughcoder.aeolus.model.TYPE_CURRENT
import net.toughcoder.aeolus.model.TYPE_NORMAL
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.ui.CityState
import net.toughcoder.aeolus.ui.DailyUiState
import net.toughcoder.aeolus.ui.asUiState

class FavoritesViewModel(
    private val locationRepo: LocationRepository,
    private val weatherRepo: WeatherRepository
) : ViewModel() {

    fun getAllFavorites(): Flow<FavoriteScreenUiState> =
        combine(locationRepo.getDefaultCityId(), locationRepo.getCurrentCity()) { defaultCityId, currentCity ->
            val favorites = locationRepo.loadFavoriteCities()
            val allCities = mutableListOf<WeatherLocation>()
            if (currentCity.successful()) {
                allCities.add(currentCity)
            }
            for (fc in favorites) {
                if (fc.id != currentCity.id) {
                    allCities.add(fc)
                }
            }

            return@combine FavoriteScreenUiState(
                loading = false,
                favorites = allCities.mapIndexed { idx, city ->
                    val weather = weatherRepo.fetchDayWeather(city)
                    return@mapIndexed FavoriteUiState(
                        city = city.asUiState(),
                        snapshot = weather.asUiState(),
                        selected = city.id == defaultCityId,
                        current = currentCity.successful() && idx == 0
                    )
                }
            )
        }.flowOn(Dispatchers.IO)

    fun setDefaultCity(city: CityState, current: Boolean) {
        viewModelScope.launch {
            locationRepo.setDefaultCity(city.toModel(), if (current) TYPE_CURRENT else TYPE_NORMAL)
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

data class FavoriteScreenUiState(
    val loading: Boolean = true,
    val favorites: List<FavoriteUiState> = emptyList()
)

data class FavoriteUiState(
    val city: CityState,
    val snapshot: DailyUiState,
    val selected: Boolean = false,
    val current: Boolean = false
)