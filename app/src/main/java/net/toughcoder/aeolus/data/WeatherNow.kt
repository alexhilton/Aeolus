package net.toughcoder.aeolus.data

data class WeatherNow(
    val nowTemp: String,
    val feelsLike: String,
    val icon: String,
    val text: String,
    val wind360: String,
    val windDir: String,
    val windScale: String,
    val windSpeed: String,
    val humidity: String,
    val airPressure: String,
    val visibility: String,
    val cloud: String,
    val updateTime: Long
)