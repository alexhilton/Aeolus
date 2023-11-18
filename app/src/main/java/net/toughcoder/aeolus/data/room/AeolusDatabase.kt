package net.toughcoder.aeolus.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherNowEntity::class, LocationEntity::class, DailyWeatherEntity::class], version = 1)
abstract class AeolusDatabase : RoomDatabase() {
    abstract fun weatherNowDao(): WeatherNowDao

    abstract fun locationDao(): LocationDao

    abstract fun dailyWeatherDao(): DailyWeatherDao
}