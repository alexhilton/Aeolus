package net.toughcoder.aeolus.ui

import androidx.annotation.DrawableRes
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.data.unit
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.WeatherLocation

data class CityState(
    val name: String,
    val id: String = "",
    val admin: String = ""
) {
    fun isEmpty() = name.isEmpty() || id.isEmpty()

    fun toModel(): WeatherLocation =
        WeatherLocation(
            id = id,
            name = name,
            admin = admin
        )
}

fun WeatherLocation.asUiState(): CityState =
    CityState(
        name = name,
        id = id,
        admin = admin
    )


data class DailyUiState(
    val date: String,
    val tempHigh: String,
    val tempLow: String,
    val sunrise: String = "",
    val sunset: String = "",
    @DrawableRes val iconDay: Int,
    val textDay: String,
    val uvIndex: String,
    val textNight: String = "",
    @DrawableRes val iconNight: Int = 0,
    val windDegree: Float = 0f,
    val windDir: String = "",
    val windScale: String = "",
    @DrawableRes val iconDir: Int = 0,
    val humidity: String = "",
    val pressure: String = "",
    val visibility: String = ""
)

fun DailyWeather.asUiState(): DailyUiState =
    DailyUiState(
        date = date,
        tempHigh = "${tempHigh.formatTemp()}${unit().temp}",
        tempLow = "${tempLow.formatTemp()}${unit().temp}",
        sunrise = sunrise,
        sunset = sunset,
        iconDay = ICONS[iconDay]!!,
        textDay = textDay,
        uvIndex = uvIndex,
        textNight = textNight,
        iconNight = ICONS[iconNight]!!,
        windDegree = windDegree.toWindDegree(),
        windDir = windDir,
        windScale = "$windScale ${unit().scale}",
        iconDir = R.drawable.ic_nav,
        humidity = "$humidity %",
        pressure = "$pressure ${unit().pressure}",
        visibility = "$visibility ${unit().length}"
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