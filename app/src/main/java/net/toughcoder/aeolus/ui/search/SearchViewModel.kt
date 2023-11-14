package net.toughcoder.aeolus.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.data.AeolusPreferences

class SearchViewModel(
    private val prefStore: AeolusPreferences
) : ViewModel() {

    fun getSearchHistories(): Flow<List<String>> = prefStore.getSearchHistories()

    fun addSearchHistory(history: String) {
        viewModelScope.launch {
            prefStore.addSearchHistory(history)
        }
    }

    companion object {
        fun providerFactory(prefStore: AeolusPreferences): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SearchViewModel(prefStore) as T
                }
            }
    }
}