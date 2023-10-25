package net.toughcoder.aeolus.data

data class WeatherLocation(
    val id: String = "",
    val name: String = ""
) {
    fun successful() = id.isNotEmpty() && name.isNotEmpty()
}