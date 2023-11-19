package net.toughcoder.aeolus.ui.favorites

import androidx.annotation.DrawableRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.data.unit
import net.toughcoder.aeolus.data.weather.WeatherRepository
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.ui.CityState
import net.toughcoder.aeolus.ui.ICONS
import net.toughcoder.aeolus.ui.asUiState
import net.toughcoder.aeolus.ui.home.formatTemp
import net.toughcoder.aeolus.ui.home.toWindDegree

class FavoritesViewModel(
    private val locationRepo: LocationRepository,
    private val weatherRepo: WeatherRepository
) : ViewModel() {

    fun getAllFavorites(): Flow<List<FavoriteState>> = flow {
        locationRepo.getDefaultCity()
            .collect { defaultCity ->
                emit(
                    locationRepo.loadAllFavoriteCities()
                        .map {
                            val weather = weatherRepo.fetchDayWeather(it)
                            FavoriteState(
                                city = it.asUiState(),
                                snapshot = weather.asSnapshotUiState(),
                                selected = it.id == defaultCity.id
                            )
                        }
                )
            }
    }

    fun setDefaultCity(city: CityState) {
        viewModelScope.launch {
            locationRepo.setDefaultCity(city.toModel())
        }
    }

    companion object {
        fun providerFactory(
            locationRepo: LocationRepository,
            weatherRepo: WeatherRepository
        ) : ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return FavoritesViewModel(locationRepo, weatherRepo) as T
                }
            }
    }
}

data class FavoriteState(
    val city: CityState,
    val snapshot: DayWeatherUiState,
    val selected: Boolean = false
)

data class DayWeatherUiState(
    val high: String,
    val low: String,
    @DrawableRes val icon: Int,
    val text: String,
    val windDegree: Float,
    @DrawableRes val iconDir: Int,
    val windDir: String,
    val windScale: String
) {
    fun isEmpty() = text.isEmpty()
}

fun DailyWeather.asSnapshotUiState(): DayWeatherUiState =
    DayWeatherUiState(
        high = "${tempHigh.formatTemp()}${unit().temp}",
        low = "${tempLow.formatTemp()}${unit().temp}",
        icon = ICONS[iconDay]!!,
        text = textDay,
        windDegree = windDegree.toWindDegree(),
        iconDir = R.drawable.ic_nav,
        windDir = windDir,
        windScale = "$windScale ${unit().scale}"
    )