package net.toughcoder.aeolus.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.data.weather.WeatherRepository
import net.toughcoder.aeolus.logd
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.ui.CityState
import net.toughcoder.aeolus.ui.DailyUiState
import net.toughcoder.aeolus.ui.asUiState

class FavoritesViewModel(
    private val locationRepo: LocationRepository,
    private val weatherRepo: WeatherRepository
) : ViewModel() {

    private val viewModelState = MutableStateFlow(FavoriteScreenUiState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    fun loadFavorites() {
        viewModelScope.launch {
            combine(
                locationRepo.getDefaultCityId(),
                locationRepo.getCurrentCity(),
            ) { defaultCityId, currentCity ->
                val favorites = locationRepo.loadFavoriteCitiesLocally()
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
                    favorites = allCities.map { city ->
                        val weather = weatherRepo.fetchDayWeather(city)
                        return@map FavoriteUiState(
                            city = city.asUiState(),
                            snapshot = weather.asUiState(),
                            selected = city.id == defaultCityId.ifEmpty { currentCity.id },
                        )
                    }
                )
            }.collect { state ->
                viewModelState.update { state }
            }
        }
    }


    fun setDefaultCity(city: CityState) {
        viewModelScope.launch {
            locationRepo.setDefaultCity(city.toModel())
        }
    }

    fun removeFavorite(item: FavoriteUiState) {
        viewModelScope.launch {
            if (item.city.current()) {
                return@launch
            }
            if (item.selected) {
                logd("Fav", "remove default city")
                locationRepo.removeDefaultCity()
            }

            logd("Fav", "remove city")
            locationRepo.removeCity(item.city.toModel())

            // TODO: We should not refresh, it should be refreshed automatically
            loadFavorites()
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
) {
    fun current() = city.current()
}