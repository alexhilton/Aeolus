package net.toughcoder.aeolus.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.ui.search.CityState
import net.toughcoder.aeolus.ui.search.asUiState

class FavoritesViewModel(
    private val locationRepo: LocationRepository
) : ViewModel() {

    fun getAllFavorites(): Flow<List<CityState>> = flow {
        emit(
            locationRepo.loadAllFavoriteCities()
                .map { it.asUiState() }
        )
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