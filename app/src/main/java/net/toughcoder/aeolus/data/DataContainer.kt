package net.toughcoder.aeolus.data

import android.content.Context
import androidx.room.Room
import net.toughcoder.aeolus.data.local.AeolusDatabase
import net.toughcoder.aeolus.data.local.LocalDataSource
import net.toughcoder.aeolus.data.weather.FakeWeatherNowDataSource
import net.toughcoder.aeolus.data.weather.QWeatherNowDataSource
import net.toughcoder.aeolus.data.weather.WeatherNowRepository

interface DataContainer {
    val locationRepository: LocationRepository
    val weatherNowRepository: WeatherNowRepository
    val database: AeolusDatabase
}

class DataContainerImpl(private val context: Context) : DataContainer {
    override val locationRepository: LocationRepository by lazy {
        LocationRepository()
    }

    override val weatherNowRepository: WeatherNowRepository by lazy {
//        WeatherNowRepository(FakeWeatherNowDataSource())
        WeatherNowRepository(
            LocalDataSource(database),
            QWeatherNowDataSource()
        )
    }

    override val database: AeolusDatabase by lazy {
        Room.databaseBuilder(context, AeolusDatabase::class.java, "aeolus.db").build()
    }
}