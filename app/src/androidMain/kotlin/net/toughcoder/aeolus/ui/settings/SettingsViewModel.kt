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
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.data.local.AeolusStore
import net.toughcoder.aeolus.logd
import net.toughcoder.aeolus.model.KEY_LANGUAGE
import net.toughcoder.aeolus.model.KEY_MEASURE
import net.toughcoder.aeolus.model.LANGUAGE_ITEM
import net.toughcoder.aeolus.model.MEASURE_ITEM
import net.toughcoder.aeolus.model.SettingsItem

class SettingsViewModel(
    private val store: AeolusStore
) : ViewModel() {
    private val viewModelState = MutableStateFlow(SettingsUiState())

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
            needUpdate = viewModelState.value.language?.needUpdate(value) ?: true
            if (needUpdate) {
                viewModelScope.launch {
                    store.persistLanguage(value)
                }
            }
        }
        if (key == KEY_MEASURE) {
            needUpdate = viewModelState.value.measure?.needUpdate(value) ?: true
            if (needUpdate) {
                viewModelScope.launch {
                    store.persistMeasure(value)
                }
            }
        }
        if (needUpdate) {
            refreshState()
        }
    }

    private fun refreshState() {
        combine(store.getLanguage(), store.getMeasure()) { lang, measure ->
            val entryLang = LANGUAGE_ITEM.toUiState(lang)
            val entryMeasure = MEASURE_ITEM.toUiState(measure)
            logd(LOG_TAG, "refresh language = $lang, measure = $measure")
            viewModelState.update { SettingsUiState(entryLang, entryMeasure) }
        }.launchIn(viewModelScope)
    }

    companion object {
        const val LOG_TAG = "SettingsViewModel"

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
    val measure: SettingsEntryUiState? = null
)

data class SettingsEntryUiState(
    val key: String,
    @StringRes val title: Int,
    private val index: Int,
    val options: List<String>,
    val optionsTitle: List<Int>
) {
    fun needUpdate(newValue: String) = value() != newValue

    private fun value() = options[index]

    fun selected(idx: Int) =  idx == index

    fun valueTitle() = optionsTitle[index]
}

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
        index = valueIndex,
        options = options,
        optionsTitle = optionsTitle
    )
}