package net.toughcoder.aeolus.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.data.AeolusStore
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.data.location.SearchRepository

class SearchViewModel(
    private val prefStore: AeolusStore,
    private val searchRepo: SearchRepository
) : ViewModel() {

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
    val name: String
)