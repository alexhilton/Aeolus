package net.toughcoder.aeolus.model

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