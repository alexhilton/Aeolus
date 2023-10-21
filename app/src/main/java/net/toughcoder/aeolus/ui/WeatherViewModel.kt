package net.toughcoder.aeolus.ui

import android.os.SystemClock
import android.util.Log
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
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.data.LocationRepository
import net.toughcoder.aeolus.data.WeatherLocation
import net.toughcoder.aeolus.data.unit
import kotlin.random.Random

class WeatherViewModel : ViewModel() {
    companion object {
        const val LOG_TAG = "WeatherViewModel"
    }

    private val locationRepo: LocationRepository = LocationRepository()

    private val viewModelState = MutableStateFlow(
        ViewModelState(
            city = locationRepo.getLocation(),
            loading = false,
            error = "Loading weather data, please wait!"
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
        viewModelState.update { it.copy(loading = true) }
        Log.d(LOG_TAG, "refresh loading loading you should see loading")
        viewModelState.value.weatherData?.let { weatherDetail ->
            val now = SystemClock.uptimeMillis()
            if (now - weatherDetail.updateTime < 20 * 1000L) {
                viewModelState.update {
                    it.copy(loading = false, error = "Weather data is already up-to-date!")
                }
                return
            }
        }

        viewModelScope.launch {
            delay(3000)
            val hasError = Random.nextInt(15) % 4 == 0
            viewModelState.update {
                if (hasError) {
                    it.copy(loading = false, error = "Something is wrong, please try again later!")
                } else {
                    it.copy(loading = false, weatherData = fakeWeatherDetail(), error = "")
                }
            }
        }
        Log.d(LOG_TAG, "refreshing is done.")
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
    var city: WeatherLocation,
    var weatherData: WeatherDetail? = null,
    var error: String = ""
) {
    fun toUiState() =
        weatherData?.toUiState(city.name, loading, error) ?: NowUiState.NoWeatherUiState(
            city.name,
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
            return city.isEmpty() && temp.isEmpty() && text.isEmpty()
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
