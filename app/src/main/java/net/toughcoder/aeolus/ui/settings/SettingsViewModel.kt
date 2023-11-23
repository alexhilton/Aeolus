package net.toughcoder.aeolus.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.toughcoder.aeolus.data.AeolusStore

class SettingsViewModel(
    store: AeolusStore
) : ViewModel() {
    companion object {
        fun providerFactory(prefStore: AeolusStore) : ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return SettingsViewModel(prefStore) as T
                }
            }
    }
}