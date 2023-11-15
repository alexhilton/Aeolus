package net.toughcoder.aeolus.ui.search

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
import net.toughcoder.aeolus.data.AeolusStore
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.data.location.SearchRepository

class SearchViewModel(
    private val prefStore: AeolusStore,
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

    fun getTopCities(): Flow<List<TopCityState>> = flow {
        emit(
            searchRepo.getHotCities()
                .map { TopCityState(it.name) }
                .toList()
        )
    }

    fun searchCity(query: String): Unit {
        _searchResultState.update { it.copy(loading = true) }
        viewModelScope.launch {
            val result = searchRepo.searchCity(query)
            val error = if (result.isEmpty()) "No results found, please try again later!" else ""
            val cities = result.map { TopCityState(it.name, it.id, it.admin) }
            _searchResultState.update { SearchResultState(false, error, cities) }
        }
    }

    companion object {
        fun providerFactory(prefStore: AeolusStore, searchRepo: SearchRepository)
                : ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(prefStore, searchRepo) as T
                }
            }
    }
}

data class TopCityState(
    val name: String,
    val id: String = "",
    val admin: String = ""
)

data class SearchResultState(
    val loading: Boolean = false,
    val error: String = "",
    val cities: List<TopCityState> = listOf()
)