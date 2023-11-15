package net.toughcoder.aeolus.model

data class WeatherLocation(
    val id: String = "",
    val name: String = "",
    val admin: String = ""
) {
    fun successful() = id.isNotEmpty() && name.isNotEmpty()
}