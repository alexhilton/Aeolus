package net.toughcoder.aeolus.data

import android.content.Context

interface DataContainer {
    val locationRepository: LocationRepository
    val weatherNowRepository: WeatherNowRepository
}

class DataContainerImpl(private val context: Context) : DataContainer {
    override val locationRepository: LocationRepository by lazy {
        LocationRepository()
    }

    override val weatherNowRepository: WeatherNowRepository by lazy {
        WeatherNowRepository()
    }
}