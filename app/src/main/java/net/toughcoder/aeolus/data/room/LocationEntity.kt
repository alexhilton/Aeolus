package net.toughcoder.aeolus.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import net.toughcoder.aeolus.model.WeatherLocation

@Entity(tableName = "favorites")
data class LocationEntity(
    @PrimaryKey val qid: String,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "admin") val admin: String,
    @ColumnInfo(name = "add_time") val addTime: Long
)

fun WeatherLocation.asEntity(): LocationEntity {
    return LocationEntity(
        qid = id,
        name = name,
        admin = admin,
        addTime = System.currentTimeMillis()
    )
}