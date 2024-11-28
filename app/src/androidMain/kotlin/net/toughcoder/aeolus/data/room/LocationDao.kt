package net.toughcoder.aeolus.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface LocationDao {
    @Query("SELECT * FROM favorites ORDER BY add_time DESC")
    fun getAllCities(): List<LocationEntity>

    @Query("SELECT * FROM favorites ORDER BY add_time DESC")
    fun queryAllCities(): Flow<List<LocationEntity>>

    @Query("SELECT * FROM favorites WHERE qid LIKE :qid LIMIT 1")
    fun getCity(qid: String): LocationEntity?

    @Query("SELECT COUNT(qid) FROM favorites")
    fun getCount(): Int

    @Insert
    fun insert(city: LocationEntity)

    @Delete
    fun delete(city: LocationEntity)

    @Update
    fun update(city: LocationEntity)
}