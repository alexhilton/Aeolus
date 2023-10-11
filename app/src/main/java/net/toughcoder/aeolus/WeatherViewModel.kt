package net.toughcoder.aeolus

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class WeatherViewModel : ViewModel() {
    private val location: String = "Beijing"

    private val viewModelState = MutableStateFlow(fakeWeatherDetail())

    val uiState = viewModelState
        .map { it.toUiState(location) }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState(location)
        )
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
) {
    fun toUiState(location: String) =
        NowUiState(
            city = location,
            temp = temp,
            feelsLike = feelsLike,
            icon = icon,
            text = text,
            windDegree = windDegree,
            windDir = windDir,
            windScale = windScale,
            windSpeed = windSpeed,
            humidity = humidity,
            pressure = pressure,
            visibility = visibility
        )
}

data class NowUiState(
    val city: String,
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
)