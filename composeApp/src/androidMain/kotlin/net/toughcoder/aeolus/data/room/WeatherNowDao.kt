package net.toughcoder.aeolus.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface WeatherNowDao {
    @Query("SELECT * FROM weather_now")
    fun getAll(): List<WeatherNowEntity>

    @Query("SELECT * FROM weather_now WHERE cityId LIKE :cityId LIMIT 1")
    fun getByCityId(cityId: String): WeatherNowEntity?

    @Insert
    fun insertAll(vararg entities: WeatherNowEntity)

    @Insert
    fun insert(entity: WeatherNowEntity)

    @Update
    fun update(entity: WeatherNowEntity)

    @Delete
    fun delete(entity: WeatherNowEntity)
}