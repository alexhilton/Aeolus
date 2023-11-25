package net.toughcoder.aeolus.model

import androidx.annotation.StringRes
import net.toughcoder.aeolus.R

const val KEY_LANGUAGE = "language"
const val KEY_MEASURE = "measure"

const val LANGUAGE_ENGLISH = "en"
const val LANGUAGE_CHINESE = "zh"
const val LANGUAGE_AUTO = "auto"
const val DEFAULT_LANGUAGE = LANGUAGE_CHINESE

const val MEASURE_METRIC = "metric"
const val MEASURE_IMPERIAL = "imperial"

const val DEFAULT_MEASURE = MEASURE_METRIC

data class SettingsItem(
    @StringRes val title: Int,
    val key: String,
    val value: String,
    val defaultValue: String,
    val options: List<String>,
    val optionsTitle: List<Int>
)

val LANGUAGE_ITEM = SettingsItem(
    title = R.string.settings_entry_language_title,
    key = KEY_LANGUAGE,
    value = DEFAULT_LANGUAGE,
    defaultValue = DEFAULT_LANGUAGE,
    options = listOf("en", "zh", "auto"),
    optionsTitle = listOf(
            R.string.settings_entry_lang_en,
            R.string.settings_entry_lang_zh,
            R.string.settings_entry_lang_auto
    )
)

val MEASURE_ITEM = SettingsItem(
    title = R.string.settings_entry_measure_title,
    key = KEY_MEASURE,
    value = DEFAULT_MEASURE,
    defaultValue = DEFAULT_MEASURE,
    options = listOf(MEASURE_METRIC, MEASURE_IMPERIAL),
    optionsTitle = listOf(
        R.string.settings_entry_measure_metric,
        R.string.settings_entry_measure_imperial
    )
)