package net.toughcoder.aeolus.ui

import androidx.annotation.DrawableRes
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.data.unit
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.WeatherLocation
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

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
    val visibility: String = "",
    val weekday: String = ""
)

fun DailyWeather.asUiState(): DailyUiState =
    DailyUiState(
        date = date.substring(5),
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
        visibility = "$visibility ${unit().length}",
        weekday = date.weekday()
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

fun String.weekday(): String {
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA)
    val d = LocalDate.parse(this, fmt)
    val w = listOf("周一", "周二", "周三", "周四", "周五", "周六", "周日")
    return w[d.dayOfWeek.value - 1]
}