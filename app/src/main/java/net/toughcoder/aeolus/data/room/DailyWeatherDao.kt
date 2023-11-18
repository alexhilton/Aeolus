package net.toughcoder.aeolus.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface DailyWeatherDao {
    @Query("SELECT * FROM daily_weather WHERE city_id LIKE :city ORDER BY wid ASC")
    fun getDailyWeathers(city: String): List<DailyWeatherEntity>

    @Query("SELECT COUNT(wid) FROM daily_weather WHERE city_id LIKE :city")
    fun getCount(city: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addDailyWeathers(weathers: List<DailyWeatherEntity>)

    @Update
    fun updateDailyWeathers(weathers: List<DailyWeatherEntity>)

    @Delete
    fun deleteDailyWeathers(weathers: List<DailyWeatherEntity>)
}