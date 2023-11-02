package net.toughcoder.aeolus.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.toughcoder.aeolus.data.weather.WeatherNow

@Entity(tableName = "weather_now")
data class WeatherNowEntity(
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "city") val city: String,
    @ColumnInfo(name = "update_time") val updateTime: Long,
    @ColumnInfo(name = "now_temp") val nowTemp: String,
    @ColumnInfo(name = "feels_like") val feelsLike: String,
    @ColumnInfo(name = "icon") val icon: String,
    @ColumnInfo(name = "text") val text: String,
    @ColumnInfo(name = "wind_degree") val windDegree: String,
    @ColumnInfo(name = "wind_dir") val windDir: String,
    @ColumnInfo(name = "wind_scale") val windScale: String,
    @ColumnInfo(name = "humidity") val humidity: String,
    @ColumnInfo(name = "pressure") val airPressure: String,
    @ColumnInfo(name = "visibility") val visibility: String
) {
    fun asModel(): WeatherNow = WeatherNow(
        successful = true,
        nowTemp = nowTemp,
        feelsLike = feelsLike,
        icon = icon,
        text = text,
        windDegree = windDegree,
        windDir = windDir,
        windScale = windScale,
        humidity = humidity,
        airPressure = airPressure,
        visibility = visibility
    )
}