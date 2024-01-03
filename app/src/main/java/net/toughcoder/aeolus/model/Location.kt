package net.toughcoder.aeolus.model

import androidx.compose.ui.text.intl.Locale
import net.toughcoder.aeolus.data.qweather.QWeatherCityDTO
import net.toughcoder.aeolus.data.room.LocationEntity

data class WeatherLocation(
    val id: String = "",
    val name: String = "",
    val admin: String = "",
    val type: Int = TYPE_NORMAL,
    val error: Int = ERROR_NONE
) {
    fun successful() = id.isNotEmpty() && name.isNotEmpty()

    fun current() = type == TYPE_CURRENT
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

const val ERROR_NONE = 0
const val ERROR_NO_PERM = 1
const val ERROR_NO_LOCATION = 2
const val ERROR_NO_CITY = 3