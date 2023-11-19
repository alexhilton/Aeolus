package net.toughcoder.aeolus.ui.home

import android.os.SystemClock
import android.util.Log
import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.WeatherNow
import net.toughcoder.aeolus.data.weather.WeatherRepository
import net.toughcoder.aeolus.data.unit
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.ui.CityState
import net.toughcoder.aeolus.ui.ICONS
import net.toughcoder.aeolus.ui.asUiState

class HomeViewModel(
    private val locationRepo: LocationRepository,
    private val weatherRepo: WeatherRepository
) : ViewModel() {
    companion object {
        const val LOG_TAG = "WeatherViewModel"
        fun provideFactory(
            locationRepo: LocationRepository,
            weatherNowRepo: WeatherRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return HomeViewModel(locationRepo, weatherNowRepo) as T
            }
        }
    }

    private val locationState = MutableStateFlow(WeatherLocation())
    private lateinit var weatherNowState: Flow<WeatherNow>
    private lateinit var dailyWeatherState: Flow<List<DailyWeather>>

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

    init {
        loadLocalWeather()
        refresh()
    }

    fun refresh() {
        // Step #1: Mark as loading
        viewModelState.update { it.copy(loading = true) }

        // Step #2: Quit earlier if not need to update
        val now = SystemClock.uptimeMillis()
        Log.d(LOG_TAG, "now $now, last ${viewModelState.value.updateTime}")
        if (now - viewModelState.value.updateTime < 30 * 1000L) {
            viewModelState.update {
                it.copy(loading = false, error = "Weather data is already up-to-date!")
            }
            return
        }

        // Step #3: Get latest location, then load new weather data based on the location
        viewModelScope.launch {
            locationRepo.getDefaultCity()
                .collect { loc ->
                    if (loc.successful()) {
                        locationState.update { loc }
                        weatherRepo.refreshWeatherNow(loc)
                        weatherRepo.refreshDailyWeather(loc)
                        updateState()
                    }
                }
        }
    }

    private fun updateState() {
        viewModelScope.launch {
            combine(locationState, weatherNowState, dailyWeatherState) { loc, now, dailyWeathers ->
                val error = if (!loc.successful()) {
                    "Failed to get location, please try again later!"
                } else if (!now.successful) {
                    "Something is wrong, please try again later!"
                } else {
                    ""
                }
                val weather = if (now.successful) now else viewModelState.value.weatherData
                val updateTime = if (now.successful) SystemClock.uptimeMillis() else viewModelState.value.updateTime
                val daily = if (dailyWeathers.isNotEmpty()) dailyWeathers else viewModelState.value.dailyData
                return@combine ViewModelState(
                    loading = false,
                    city = loc,
                    weatherData = weather,
                    dailyData = daily,
                    error = error,
                    updateTime = updateTime
                )
            }.collect { state ->
                viewModelState.update { state }
            }
        }
    }

    private fun loadLocalWeather() {
        viewModelScope.launch {
            locationRepo.getDefaultCity()
                .collect { loc ->
                    weatherNowState = weatherRepo.weatherNowStream(loc)
                    dailyWeatherState = weatherRepo.dailyWeatherStream(loc)
                    Log.d(LOG_TAG, "from locals: location $loc")
                    locationState.update { loc }
                }

            updateState()
        }
    }
}

data class ViewModelState(
    var loading: Boolean = false,
    var city: WeatherLocation? = null,
    var weatherData: WeatherNow? = null,
    val dailyData: List<DailyWeather> = listOf(),
    var error: String = "",
    var updateTime: Long = -1
) {
    fun toUiState() =
        weatherData?.let { convert(it) } ?: NowUiState.NoWeatherUiState(
            city?.asUiState(),
            false,
            error
        )

    private fun convert(data: WeatherNow): NowUiState =
        with(unit()) {
            return NowUiState.WeatherNowUiState(
                isLoading = loading,
                city = city?.asUiState(),
                temp = "${data.nowTemp.formatTemp()}$temp",
                feelsLike = "${data.feelsLike.formatTemp()}$temp",
                icon = ICONS[data.icon]!!,
                text = data.text,
                windDegree = (data.windDegree.toFloat() + 180f) % 360f,
                iconDir = R.drawable.ic_nav,
                windDir = data.windDir,
                windScale = "${data.windScale} $scale",
                windSpeed = "${data.windSpeed} $speed",
                humidity = "${data.humidity} $percent",
                pressure = "${data.airPressure} $pressure",
                visibility = "${data.visibility} $length",
                dailyStates = dailyData.map { it.asUiState() },
                errorMessage = error
            )
        }
}

sealed interface NowUiState {
    val city: CityState?

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
        val dailyStates: List<DailyUiState>,
        override val city: CityState?,
        override val isLoading: Boolean,
        override val errorMessage: String
    ) : NowUiState {
        override fun isEmpty(): Boolean {
            return (city?.isEmpty() ?: true) && temp.isEmpty() && text.isEmpty()
        }
    }

    data class NoWeatherUiState(
        override val city: CityState?,
        override val isLoading: Boolean,
        override val errorMessage: String
    ) : NowUiState {
        override fun isEmpty() = true
    }
}

data class DailyUiState(
    val date: String,
    val tempHigh: String,
    val tempLow: String,
    val sunrise: String = "",
    val sunset: String = "",
    val iconDay: String,
    val textDay: String,
    val uvIndex: String,
)

fun DailyWeather.asUiState(): DailyUiState =
    DailyUiState(
        date = date,
        tempHigh = "${tempHigh.formatTemp()}${unit().temp}",
        tempLow = "${tempLow.formatTemp()}${unit().temp}",
        sunrise = sunrise,
        sunset = sunset,
        iconDay = iconDay,
        textDay = textDay,
        uvIndex = uvIndex
    )

fun String.formatTemp(): String {
    val temp = this
    return if ("." in temp) {
        val t = temp.toFloat()
        "%.1f".format(t)
    } else {
        temp
    }
}

fun String.toWindDegree(): Float {
    return (this.toFloat() + 180f) % 360f
}