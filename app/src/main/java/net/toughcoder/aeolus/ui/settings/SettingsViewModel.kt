package net.toughcoder.aeolus.ui.settings

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.data.AeolusStore

class SettingsViewModel(
    store: AeolusStore
) : ViewModel() {
    private val viewModelState = MutableStateFlow(SettingsUiState())

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        val lang = SettingsEntryUiState(
            key = "language",
            title = R.string.settings_entry_language_title,
            value = "English",
            options = listOf("English", "Chinese", "Auto")
        )
        val unit = SettingsEntryUiState(
            key = "unit",
            title = R.string.settings_entry_unit_title,
            value = "Metric",
            options = listOf("Metric", "Imperial")
        )
        viewModelState.update { SettingsUiState(lang, unit) }
    }

    fun updateSettingsEntry(key: String, value: String) {
        var needUpdate = false
        val langEntry = viewModelState.value.language
        val lang = if (key == "language") {
            langEntry?.let {
                needUpdate = value != it.value
            }
            langEntry?.copy(value = value)
        } else {
            langEntry
        }
        val unitEntry = viewModelState.value.unit
        val unit = if (key == "unit") {
            unitEntry?.let {
                needUpdate = value != it.value
            }
            unitEntry?.copy(value = value)
        } else {
            unitEntry
        }
        if (needUpdate) {
            viewModelState.update { SettingsUiState(lang, unit) }
        }
    }

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

data class SettingsUiState(
    val language: SettingsEntryUiState? = null,
    val unit: SettingsEntryUiState? = null
)

data class SettingsEntryUiState(
    val key: String,
    @StringRes val title: Int,
    val value: String,
    val options: List<String>
)