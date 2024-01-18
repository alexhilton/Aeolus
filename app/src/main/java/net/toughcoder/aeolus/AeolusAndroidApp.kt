package net.toughcoder.aeolus

import android.app.Application
import net.toughcoder.aeolus.data.DataContainer
import net.toughcoder.aeolus.data.DataContainerImpl

class AeolusAndroidApp : Application() {
    lateinit var dataContainer: DataContainer

    override fun onCreate() {
        super.onCreate()

        System.setProperty("kotlinx.coroutines.debug", if (BuildConfig.DEBUG) "on" else "off")

        dataContainer = DataContainerImpl(this)
    }
}