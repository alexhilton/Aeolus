package net.toughcoder.aeolus.ui.search

import androidx.annotation.StringRes
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.data.AeolusStore
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.data.location.SearchRepository
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.ui.CityState
import net.toughcoder.aeolus.ui.NO_ERROR
import net.toughcoder.aeolus.ui.asUiState

class SearchViewModel(
    private val prefStore: AeolusStore,
    private val locationRepo: LocationRepository,
    private val searchRepo: SearchRepository
) : ViewModel() {

    private val _searchResultState = MutableStateFlow(SearchResultState(false))

    val searchResultState = _searchResultState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            _searchResultState.value
        )

    fun getSearchHistories(): Flow<List<String>> = prefStore.getSearchHistories()

    fun addSearchHistory(history: String) {
        viewModelScope.launch {
            prefStore.addSearchHistory(history)
        }
    }

    fun getTopCities(): Flow<List<CityState>> = flow {
        emit(
            searchRepo.getHotCities()
                .map { it.asUiState() }
                .toList()
        )
    }

    fun searchCity(query: String): Unit {
        _searchResultState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val result = searchRepo.searchCity(query)
            val error = if (result.isEmpty()) R.string.empty_search_results else NO_ERROR
            val cities = result.map { it.asUiState() }
            _searchResultState.update { SearchResultState(false, error, cities) }
        }
    }

    fun favoriteCity(city: CityState): Unit {
        viewModelScope.launch {
            locationRepo.favoriteCity(WeatherLocation(city.id, city.name, city.admin))
        }
    }

    companion object {
        fun providerFactory(
            prefStore: AeolusStore,
            locationRepository: LocationRepository,
            searchRepo: SearchRepository
        )
                : ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(prefStore, locationRepository, searchRepo) as T
                }
            }
    }
}

data class SearchResultState(
    val loading: Boolean = false,
    @StringRes val error: Int = 0,
    val cities: List<CityState> = listOf()
)