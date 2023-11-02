package net.toughcoder.aeolus.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WeatherNowEntity::class], version = 1)
abstract class AeolusDatabase : RoomDatabase() {
    abstract fun weatherNowDao(): WeatherNowDao
}