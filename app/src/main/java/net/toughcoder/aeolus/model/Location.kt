package net.toughcoder.aeolus.model

import androidx.compose.ui.text.intl.Locale
import net.toughcoder.aeolus.data.room.LocationEntity

data class WeatherLocation(
    val id: String = "",
    val name: String = "",
    val admin: String = ""
) {
    fun successful() = id.isNotEmpty() && name.isNotEmpty()
}

fun LocationEntity.asModel(): WeatherLocation {
    return WeatherLocation(
        id = qid,
        name = name,
        admin = admin
    )
}

fun toParamLang(lang: String) =
    if (lang == LANGUAGE_AUTO) Locale.current.language else lang