package net.toughcoder.aeolus.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.toughcoder.aeolus.model.DailyWeather

@Entity(tableName = "daily_weather")
data class DailyWeatherEntity(
    @PrimaryKey val wid: String,
    @ColumnInfo(name = "city_id") val cityId: String,
    @ColumnInfo(name = "date") val date: String,
    @ColumnInfo(name = "temp_high") val tempHigh: String,
    @ColumnInfo(name = "temp_low") val tempLow: String,
    @ColumnInfo(name = "sunrise") val sunrise: String,
    @ColumnInfo(name = "sunset") val sunset: String,
    @ColumnInfo(name = "icon_day") val iconDay: String,
    @ColumnInfo(name = "text_day") val textDay: String,
    @ColumnInfo(name = "uv_index") val uvIndex: String,
    @ColumnInfo(name = "humidity") val humidity: String,
    @ColumnInfo(name = "pressure") val pressure: String,
    @ColumnInfo(name = "visibility") val visibility: String,
    @ColumnInfo(name = "wind_scale") val windScale: String,
    @ColumnInfo(name = "wind_degree") val windDegree: String,
    @ColumnInfo(name = "wind_dir") val windDir: String,
    @ColumnInfo(name = "wind_speed") val windSpeed: String,
    @ColumnInfo(name = "icon_night") val iconNight: String,
    @ColumnInfo(name = "text_night") val textNight: String
)

fun DailyWeather.toEntity(cityId: String, index: Int): DailyWeatherEntity =
    DailyWeatherEntity(
        "%s-%02d".format(cityId, index),
        cityId,
        date,
        tempHigh,
        tempLow,
        sunrise,
        sunset,
        iconDay,
        textDay,
        uvIndex,
        humidity,
        pressure,
        visibility,
        windScale,
        windDegree,
        windDir,
        windSpeed,
        iconNight,
        textNight
    )