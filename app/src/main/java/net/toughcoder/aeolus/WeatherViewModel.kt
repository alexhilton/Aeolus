package net.toughcoder.aeolus

import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.random.Random

class WeatherViewModel : ViewModel() {
    private val viewModelState = MutableStateFlow(
        ViewModelState(
            city = "Nanjing",
            loading = false,
            error = "Network error, please try again later!"
        )
    )

    val uiState = viewModelState
        .map(ViewModelState::toUiState)
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            viewModelState.value.toUiState()
        )

    fun refresh() {
        viewModelState.value.weatherData?.let { weatherDetail ->
            val now = SystemClock.uptimeMillis()
            if (now - weatherDetail.updateTime < 60 * 1000L) {
                viewModelState.update {
                    it.copy(error = "Weather data is already up-to-date!")
                }
                return
            }
        }

        viewModelState.update { it.copy(loading = true) }
        viewModelScope.launch {
            delay(3000)
            viewModelState.update {
                it.copy(loading = false, weatherData = fakeWeatherDetail(), error = "")
            }
        }
    }
}

fun fakeWeatherDetail() = WeatherDetail(
    (Random.nextFloat() * 40f).toString(),
    (Random.nextFloat() * 70f).toString(),
    "101",
    "多云",
    Random.nextInt(360).toString(),
    "东南风",
    Random.nextInt(12).toString(),
    Random.nextInt(100).toString(),
    Random.nextInt(100).toString(),
    Random.nextInt(10000).toString(),
    Random.nextInt(400).toString(),
    "10",
    updateTime = SystemClock.uptimeMillis()
)

data class ViewModelState(
    var loading: Boolean = false,
    var city: String,
    var weatherData: WeatherDetail? = null,
    var error: String = ""
) {
    fun toUiState() =
        weatherData?.toUiState(city, loading, error) ?: NowUiState.NoWeatherUiState(
            city,
            false,
            error
        )
}

data class WeatherDetail(
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
) {
    fun toUiState(location: String, loading: Boolean, error: String = ""): NowUiState =
        with(unit()) {
            return NowUiState.WeatherNowUiState(
                isLoading = loading,
                city = location,
                temp = "${formatTemp(nowTemp)}$temp",
                feelsLike = "${formatTemp(feelsLike)}$temp",
                icon = ICONS[icon]!!,
                text = text,
                windDegree = (wind360.toFloat() + 180f) % 360f,
                iconDir = R.drawable.ic_nav,
                windDir = windDir,
                windScale = "$windScale $scale",
                windSpeed = "$windSpeed $speed",
                humidity = "$humidity $percent",
                pressure = "$airPressure $pressure",
                visibility = "$visibility $length",
                errorMessage = error
            )
        }

    private fun formatTemp(temp: String): String {
        val t = temp.toFloat()
        return "%.1f".format(t)
    }
}

sealed interface NowUiState {
    val city: String

    val isLoading: Boolean

    val errorMessage: String

    fun isEmpty(): Boolean

    data class WeatherNowUiState(
        val temp: String,
        val feelsLike: String,
        @DrawableRes val icon: Int,
        val text: String,
        val windDegree: Float,
        @DrawableRes val iconDir: Int,
        val windDir: String,
        val windScale: String,
        val windSpeed: String,
        val humidity: String,
        val pressure: String,
        val visibility: String,
        override val city: String,
        override val isLoading: Boolean,
        override val errorMessage: String
    ) : NowUiState {
        override fun isEmpty(): Boolean {
            return city.isNotEmpty() && temp.isNotEmpty() && text.isNotEmpty()
        }
    }

    data class NoWeatherUiState(
        override val city: String,
        override val isLoading: Boolean,
        override val errorMessage: String
    ) : NowUiState {
        override fun isEmpty() = true
    }
}
