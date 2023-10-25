package net.toughcoder.aeolus.ui

import android.os.SystemClock
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.data.LocationRepository
import net.toughcoder.aeolus.data.WeatherLocation
import net.toughcoder.aeolus.data.WeatherNow
import net.toughcoder.aeolus.data.WeatherNowRepository
import net.toughcoder.aeolus.data.unit

class WeatherViewModel : ViewModel() {
    companion object {
        const val LOG_TAG = "WeatherViewModel"
    }

    private val locationRepo: LocationRepository = LocationRepository()

    private val weatherNowRepo: WeatherNowRepository = WeatherNowRepository()

    private val viewModelState = MutableStateFlow(
        ViewModelState(
            loading = true,
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
            viewModelState.update {
                val loc = locationRepo.getLocation()
                if (!loc.successful()) {
                    it.copy(
                        loading = false,
                        error = "Failed to get location, please try again later"
                    )
                } else {
                    val weatherData = weatherNowRepo.getWeatherNow(loc)
                    if (!weatherData.successful) {
                        it.copy(
                            loading = false,
                            city = loc,
                            error = "Something is wrong, please try again later!"
                        )
                    } else {
                        it.copy(
                            loading = false,
                            city = loc,
                            weatherData = weatherNowRepo.getWeatherNow(loc),
                            error = ""
                        )
                    }
                }
            }
        }
        Log.d(LOG_TAG, "refreshing is done.")
    }
}

data class ViewModelState(
    var loading: Boolean = false,
    var city: WeatherLocation? = null,
    var weatherData: WeatherNow? = null,
    var error: String = ""
) {
    fun toUiState() =
        weatherData?.let { convert(it) } ?: NowUiState.NoWeatherUiState(
            city?.name ?: "",
            false,
            error
        )

    private fun convert(data: WeatherNow): NowUiState =
        with(unit()) {
            return NowUiState.WeatherNowUiState(
                isLoading = loading,
                city = city?.name ?: "",
                temp = "${formatTemp(data.nowTemp)}$temp",
                feelsLike = "${formatTemp(data.feelsLike)}$temp",
                icon = ICONS[data.icon]!!,
                text = data.text,
                windDegree = (data.wind360.toFloat() + 180f) % 360f,
                iconDir = R.drawable.ic_nav,
                windDir = data.windDir,
                windScale = "${data.windScale} $scale",
                windSpeed = "${data.windSpeed} $speed",
                humidity = "${data.humidity} $percent",
                pressure = "${data.airPressure} $pressure",
                visibility = "${data.visibility} $length",
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
