package net.toughcoder.aeolus.ui.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.ui.search.CityState

class FavoritesViewModel(
    private val locationRepo: LocationRepository
) : ViewModel() {

    fun getAllFavorites(): Flow<List<CityState>> = flow {
        emit(
            locationRepo.loadAllFavoriteCities()
                .map { CityState(name = it.name, id = it.id, admin = it.admin) }
        )
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