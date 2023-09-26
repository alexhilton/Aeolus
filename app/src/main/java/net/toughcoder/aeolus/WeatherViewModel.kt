package net.toughcoder.aeolus

import androidx.lifecycle.ViewModel

class WeatherViewModel : ViewModel() {
    val location: String = "Beijing"

    val weatherDetail: WeatherDetail = fakeWeatherDetail()
}

fun fakeWeatherDetail() = WeatherDetail(
    "24",
    "26",
    "101",
    "多云",
    "123",
    "东南风",
    "1",
    "3",
    "72",
    "1003",
    "16",
    "10",
)

data class WeatherDetail(
    val temp: String,
    val feelsLike: String,
    val icon: String,
    val text: String,
    val windDegree: String,
    val windDir: String,
    val windScale: String,
    val windSpeed: String,
    val humidity: String,
    val pressure: String,
    val visibility: String,
    val cloud: String,
)