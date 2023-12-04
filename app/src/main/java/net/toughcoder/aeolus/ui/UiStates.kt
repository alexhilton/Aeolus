package net.toughcoder.aeolus.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import net.toughcoder.aeolus.R
import net.toughcoder.aeolus.model.DailyWeather
import net.toughcoder.aeolus.model.WeatherLocation
import net.toughcoder.aeolus.model.getMeasure
import net.toughcoder.qweather.ICONS
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

const val NO_ERROR = 0

data class CityState(
    val name: String,
    val id: String = "",
    val admin: String = ""
) {
    fun isEmpty() = name.isEmpty() || id.isEmpty()

    fun fullname() = "$name, $admin"

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
    @StringRes val weekday: Int = 0,
    val aqi: String = "",
    @DrawableRes val clothIcon: Int = 0,
    val clothIndex: String = "",
    @DrawableRes val coldIcon: Int = 0,
    val coldIndex: String = "",
) {
    fun isEmpty() = textDay.isEmpty()
}

fun DailyWeather.asUiState(): DailyUiState =
    getMeasure().let { it ->
        DailyUiState(
            date = date.substring(5),
            tempHigh = "${tempHigh.formatTemp()}${it.temp}",
            tempLow = "${tempLow.formatTemp()}${it.temp}",
            sunrise = sunrise,
            sunset = sunset,
            iconDay = ICONS[iconDay]!!,
            textDay = textDay,
            uvIndex = uvIndex,
            textNight = textNight,
            iconNight = ICONS[iconNight]!!,
            windDegree = windDegree.toWindDegree(),
            windDir = windDir,
            windScale = "$windScale${it.scale}",
            iconDir = R.drawable.ic_nav,
            humidity = "$humidity${it.percent}",
            pressure = "$pressure${it.pressure}",
            visibility = "$visibility${it.length}",
            weekday = date.weekday(),
            aqi = airQualityIndex,
            clothIcon = R.drawable.ic_index_cloth,
            clothIndex = clothIndex,
            coldIcon = R.drawable.ic_index_cold,
            coldIndex = coldIndex
        )
    }

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

@StringRes
fun String.weekday(): Int {
    val fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.CHINA)
    val d = LocalDate.parse(this, fmt)
    val w = listOf(
        R.string.weekday_0,
        R.string.weekday_1,
        R.string.weekday_2,
        R.string.weekday_3,
        R.string.weekday_4,
        R.string.weekday_5,
        R.string.weekday_6,
    )
    return w[d.dayOfWeek.value - 1]
}