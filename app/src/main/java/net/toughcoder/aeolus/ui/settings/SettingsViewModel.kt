package net.toughcoder.aeolus.ui.settings

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import net.toughcoder.aeolus.data.AeolusStore
import net.toughcoder.aeolus.model.DEFAULT_LANGUAGE
import net.toughcoder.aeolus.model.DEFAULT_MEASURE
import net.toughcoder.aeolus.model.KEY_LANGUAGE
import net.toughcoder.aeolus.model.KEY_MEASURE
import net.toughcoder.aeolus.model.LANGUAGE_ITEM
import net.toughcoder.aeolus.model.MEASURE_ITEM
import net.toughcoder.aeolus.model.SettingsItem

class SettingsViewModel(
    store: AeolusStore
) : ViewModel() {
    private val viewModelState = MutableStateFlow(SettingsUiState())
    private val languageStream = MutableStateFlow(DEFAULT_LANGUAGE)
    private val measureStream = MutableStateFlow(DEFAULT_MEASURE)

    val uiState = viewModelState
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value
        )

    init {
        refreshState()
    }

    fun updateSettingsEntry(key: String, value: String) {
        var needUpdate = false
        if (key == KEY_LANGUAGE) {
            needUpdate = value != languageStream.value
            if (needUpdate) {
                languageStream.update { value }
            }
        }
        if (key == KEY_MEASURE) {
            needUpdate = value != measureStream.value
            if (needUpdate) {
                measureStream.update { value }
            }
        }
        if (needUpdate) {
            refreshState()
        }
    }

    private fun refreshState() {
        combine(languageStream, measureStream) { lang, measure ->
            val entryLang = LANGUAGE_ITEM.toUiState(lang)
            val entryMeasure = MEASURE_ITEM.toUiState(measure)
            viewModelState.update { SettingsUiState(entryLang, entryMeasure) }
        }.launchIn(viewModelScope)
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
    val value: Int,
    val options: List<String>,
    val optionsTitle: List<Int>
)

fun SettingsItem.toUiState(newValue: String): SettingsEntryUiState {
    var valueIndex = 0
    for (i in options.indices) {
        if (newValue == options[i]) {
            valueIndex = i
            break
        }
    }
    return SettingsEntryUiState(
        key = key,
        title = title,
        value = valueIndex,
        options = options,
        optionsTitle = optionsTitle
    )
}