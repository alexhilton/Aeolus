package net.toughcoder.aeolus.ui

import net.toughcoder.aeolus.model.WeatherLocation

data class CityState(
    val name: String,
    val id: String = "",
    val admin: String = ""
) {
    fun isEmpty() = name.isEmpty() || id.isEmpty()

    fun toModel(): WeatherLocation =
        WeatherLocation(
            id = id,
            name = name,
            admin = admin
        )
}

fun WeatherLocation.asUiState(): CityState =
    CityState(
        name = name,
        id = id,
        admin = admin
    )