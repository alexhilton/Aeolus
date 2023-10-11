package net.toughcoder.aeolus

import androidx.annotation.DrawableRes
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
    val wind360: String,
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
            temp = "$temp ${unit().temp}",
            feelsLike = "$feelsLike ${unit().temp}",
            icon = ICONS[icon]!!,
            text = text,
            windDegree = (wind360.toFloat() + 90f) % 360f,
            windDir = windDir,
            windScale = "$windScale ${unit().scale}",
            windSpeed = "$windSpeed ${unit().speed}",
            humidity = "$humidity ${unit().percent}",
            pressure = "$pressure ${unit().pressure}",
            visibility = "$visibility ${unit().length}"
        )
}

data class NowUiState(
    val city: String,
    val temp: String,
    val feelsLike: String,
    @DrawableRes val icon: Int,
    val text: String,
    val windDegree: Float,
    val windDir: String,
    val windScale: String,
    val windSpeed: String,
    val humidity: String,
    val pressure: String,
    val visibility: String,
)