package net.toughcoder.aeolus.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import net.toughcoder.aeolus.data.local.AeolusStore
import net.toughcoder.aeolus.data.qweather.GeoAPIService
import net.toughcoder.aeolus.data.qweather.QWeatherService
import net.toughcoder.aeolus.data.room.AeolusDatabase
import net.toughcoder.aeolus.data.local.LocalDataSource
import net.toughcoder.aeolus.data.local.aeolusStore
import net.toughcoder.aeolus.data.location.FakeLocationClient
import net.toughcoder.aeolus.data.location.FusedLocationClient
import net.toughcoder.aeolus.data.location.LocationProvider
import net.toughcoder.aeolus.data.location.LocationRepository
import net.toughcoder.aeolus.data.location.QWeatherLocationSource
import net.toughcoder.aeolus.data.weather.QWeatherDataSource
import net.toughcoder.aeolus.data.weather.WeatherRepository

interface DataContainer {
    val locationRepository: LocationRepository
    val weatherRepository: WeatherRepository
    val database: AeolusDatabase
    val datastore: AeolusStore
    val locationClient: LocationProvider
}

class DataContainerImpl(private val context: Context) : DataContainer {
    override val locationRepository: LocationRepository by lazy {
        LocationRepository(
            datastore,
            database,
            locationClient,
            QWeatherLocationSource(QWeatherService.create(GeoAPIService.BASE_URL)),
            Dispatchers.IO
        )
    }

    override val weatherRepository: WeatherRepository by lazy {
//        WeatherNowRepository(FakeWeatherNowDataSource())
        WeatherRepository(
            datastore,
            LocalDataSource(database),
            QWeatherDataSource(QWeatherService.create(QWeatherService.BASE_URL)),
            Dispatchers.IO
        )
    }

    override val database: AeolusDatabase by lazy {
        Room.databaseBuilder(context, AeolusDatabase::class.java, "aeolus.db").build()
    }

    override val datastore: AeolusStore by lazy {
        AeolusStore(context.aeolusStore)
    }

    override val locationClient: LocationProvider by lazy {
//        FusedLocationClient(context)
        FakeLocationClient(context)
    }
}