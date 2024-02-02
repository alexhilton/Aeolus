package net.toughcoder.aeolus.ui.home

import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
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
import net.toughcoder.aeolus.logd
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.ERROR_NO_CITY
import net.toughcoder.aeolus.model.ERROR_NO_LOCATION
import net.toughcoder.aeolus.model.ERROR_NO_PERM
import net.toughcoder.aeolus.model.HourlyWeather
import net.toughcoder.aeolus.model.WeatherIndex
import net.toughcoder.aeolus.model.getMeasure
import net.toughcoder.aeolus.ui.CityState
import net.toughcoder.aeolus.ui.DailyUiState
import net.toughcoder.qweather.ICONS
import net.toughcoder.aeolus.ui.NO_ERROR
import net.toughcoder.aeolus.ui.asUiState
import net.toughcoder.aeolus.ui.formatTemp
import net.toughcoder.aeolus.ui.toWindDegree

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

    private val locationState = MutableStateFlow(WeatherLocation(error = ERROR_NO_CITY))

    private lateinit var weatherNowState: Flow<WeatherNow>
    private lateinit var dailyWeatherState: Flow<List<DailyWeather>>
    private lateinit var hourlyWeatherState: Flow<List<HourlyWeather>>
    private lateinit var weatherIndexState: Flow<List<WeatherIndex>>

    private val viewModelState = MutableStateFlow(
        ViewModelState(
            loading = true,
            error = R.string.error_loading
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
    }

    fun refresh() {
        viewModelScope.launch {
            doRefresh()
        }
    }

    private suspend fun doRefresh() {
        // Step #1: Mark as loading
        viewModelState.update { it.copy(loading = true) }

        // Step #2: Quit earlier if not need to update
        val now = SystemClock.uptimeMillis()
        logd(LOG_TAG, "doRefresh now $now, last ${viewModelState.value.updateTime}")
        val cityChanged = locationState.value != viewModelState.value.city
        if (!cityChanged && now - viewModelState.value.updateTime < 120 * 1000L) {
            viewModelState.update {
                it.copy(loading = false, error = R.string.error_up_to_date)
            }
            logd(LOG_TAG, "doRefresh, weather data is up-to-date, do nothing.")
            return
        }

        // Step #3: Get latest location, then load new weather data based on the location
        locationRepo.getDefaultCity()
            .collect { loc ->
                logd(LOG_TAG, "doRefresh new loc $loc")
                if (loc.successful()) {
                    locationState.update { loc }
                    weatherRepo.refreshWeatherNow(loc)
                    weatherRepo.refresh3DayWeathers(loc)
                    weatherRepo.refreshHourlyWeathers(loc)
                    weatherRepo.refreshWeatherIndices(loc)

                    viewModelScope.launch {
                        updateState()
                    }
                }
            }
    }

    private suspend fun updateState(forceLoading: Boolean = false) {
        combine(
            locationState, weatherNowState, dailyWeatherState, hourlyWeatherState, weatherIndexState
        ) { loc, now, dailyWeathers, hourlyWeathers, weatherIndices ->
            val error = if (!loc.successful()) {
                when (loc.error) {
                    ERROR_NO_PERM -> R.string.error_permission
                    ERROR_NO_LOCATION -> R.string.error_location
                    ERROR_NO_CITY -> R.string.error_city
                    else -> NO_ERROR
                }
            } else if (forceLoading) {
                R.string.error_loading
            } else if (!now.successful) {
                R.string.error_network
            } else {
                NO_ERROR
            }
            val weather = if (now.successful) now else viewModelState.value.weatherData
            val updateTime = if (now.successful) SystemClock.uptimeMillis() else viewModelState.value.updateTime
            val daily = dailyWeathers.ifEmpty { viewModelState.value.dailyData }
            val hourly = hourlyWeathers.ifEmpty { viewModelState.value.hourlyData }
            val indices = weatherIndices.ifEmpty { viewModelState.value.indexData }
            return@combine ViewModelState(
                loading = forceLoading,
                city = loc,
                weatherData = weather,
                dailyData = daily,
                hourlyData = hourly,
                indexData = indices,
                error = error,
                updateTime = updateTime
            )
        }.collect { state ->
            viewModelState.update { state }
        }
    }

    private fun loadLocalWeather() {
        viewModelScope.launch {
            locationRepo.getDefaultCity()
                .collect { loc ->
                    weatherNowState = weatherRepo.weatherNowStream(loc)
                    dailyWeatherState = weatherRepo.dailyWeatherStream(loc)
                    hourlyWeatherState = weatherRepo.hourlyWeatherStream(loc)
                    weatherIndexState = weatherRepo.weatherIndexStream(loc)
                    logd(LOG_TAG, "from locals: location $loc")
                    locationState.update { loc }

                    viewModelScope.launch {
                        updateState(true)
                    }

                    doRefresh()
                }
        }
    }
}

data class ViewModelState(
    var loading: Boolean = false,
    var city: WeatherLocation? = null,
    var weatherData: WeatherNow? = null,
    val dailyData: List<DailyWeather> = emptyList(),
    val hourlyData: List<HourlyWeather> = emptyList(),
    val indexData: List<WeatherIndex> = emptyList(),
    @StringRes var error: Int = NO_ERROR,
    var updateTime: Long = -1
) {
    fun toUiState() =
        weatherData?.let { convert(it) } ?: HomeUiState.NoWeatherUiState(
            city?.asUiState(),
            false,
            error
        )

    private fun convert(data: WeatherNow): HomeUiState =
        with(data.getMeasure()) {
            return HomeUiState.WeatherUiState(
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
                aqi = data.airQualityIndex,
                dailyStates = dailyData.map { it.asUiState() },
                hourlyStates = hourlyData.map {it.asUiState() },
                weatherIndices = convertIndices(),
                errorMessage = error
            )
        }

    private fun convertIndices(): List<IndexUiState> {
        if (indexData.isEmpty()) {
            return emptyList()
        }
        val order = listOf(3, 9, 5, 7, 1, 2)
        val icons = listOf(
            R.drawable.ic_index_cloth,
            R.drawable.ic_index_cold,
            R.drawable.ic_index_uv,
            R.drawable.ic_index_allege,
            R.drawable.ic_index_sports,
            R.drawable.ic_index_car_wash
        )
        return order.mapIndexed { idx, t ->
            var item = indexData[0]
            for (x in indexData) {
                if (x.type == t) {
                    item = x
                }
            }
            return@mapIndexed item.asUiState(icons[idx])
        }
    }
}

sealed interface HomeUiState {
    val city: CityState?

    val isLoading: Boolean

    val errorMessage: Int

    fun isEmpty(): Boolean

    data class WeatherUiState(
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
        val aqi: String,
        val dailyStates: List<DailyUiState>,
        val hourlyStates: List<HourlyUiState>,
        val weatherIndices: List<IndexUiState>,
        override val city: CityState?,
        override val isLoading: Boolean,
        @StringRes override val errorMessage: Int
    ) : HomeUiState {
        override fun isEmpty(): Boolean {
            return (city?.isEmpty() ?: true) && temp.isEmpty() && text.isEmpty()
        }
    }

    data class NoWeatherUiState(
        override val city: CityState?,
        override val isLoading: Boolean,
        @StringRes override val errorMessage: Int
    ) : HomeUiState {
        override fun isEmpty() = true
    }
}

data class HourlyUiState(
    val time: String,
    val temp: String,
    val tempValue: Float,
    val text: String,
    @DrawableRes val icon: Int,
    @DrawableRes val iconDir: Int,
    val windScale: String,
    val windDegree: Float
)

fun HourlyWeather.asUiState(): HourlyUiState =
    getMeasure().let {
        HourlyUiState(
            time = dateTime.smartHour(),
            temp = "${temp.formatTemp()}${it.temp}",
            tempValue = temp.toFloat(),
            text = text,
            icon = ICONS[icon]!!,
            iconDir = R.drawable.ic_nav,
            windScale = "$windScale${it.scale}",
            windDegree = windDegree.toWindDegree()
        )
    }


fun String.smartHour(): String {
    val d = this.substring(5, 10)
    val t = this.substring(11, 16)
    return if (t == "00:00") d else t
}

data class IndexUiState(
    val type: Int,
    @DrawableRes val icon: Int,
    val name: String,
    val category: String,
    val text: String
)

fun WeatherIndex.asUiState(icon: Int): IndexUiState =
    IndexUiState(
        type = type,
        icon = icon,
        name = name,
        category = category,
        text = text
    )