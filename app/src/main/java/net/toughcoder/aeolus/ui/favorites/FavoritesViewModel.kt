package net.toughcoder.aeolus.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.ui.CityState
import net.toughcoder.aeolus.ui.asUiState

class FavoritesViewModel(
    private val locationRepo: LocationRepository
) : ViewModel() {

    fun getAllFavorites(): Flow<List<FavoriteState>> = flow {
        locationRepo.getDefaultCity()
            .collect { defaultCity ->
                emit(
                    locationRepo.loadAllFavoriteCities()
                        .map {
                            FavoriteState(
                                city = it.asUiState(),
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
            locationRepository: LocationRepository,
        ) : ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FavoritesViewModel(locationRepository) as T
                }
            }
    }
}

data class FavoriteState(
    val city: CityState,
    val selected: Boolean = false
)