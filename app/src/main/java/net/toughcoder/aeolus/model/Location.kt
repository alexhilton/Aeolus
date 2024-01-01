package net.toughcoder.aeolus.model

import androidx.compose.ui.text.intl.Locale
import net.toughcoder.aeolus.data.qweather.QWeatherCityDTO
import net.toughcoder.aeolus.data.room.LocationEntity

data class WeatherLocation(
    val id: String = "",
    val name: String = "",
    val admin: String = "",
    val type: Int = TYPE_NORMAL
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

fun QWeatherCityDTO.toModel(type: Int = TYPE_NORMAL): WeatherLocation {
    val admin = if (name == admin2) admin1 else admin2
    return WeatherLocation(qweatherId, name, admin, type)
}

fun toParamLang(lang: String) =
    if (lang == LANGUAGE_AUTO) Locale.current.language else lang

const val TYPE_CURRENT = 1
const val TYPE_NORMAL = 2