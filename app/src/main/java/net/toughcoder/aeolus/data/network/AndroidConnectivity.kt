package net.toughcoder.aeolus.data.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

interface Connectivity {
    fun isOnline(): Boolean
}

class AndroidConnectivity(private val context: Context) : Connectivity {
    override fun isOnline(): Boolean {
        val connManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connManager.getNetworkCapabilities(connManager.activeNetwork)
        return capabilities?.let { cap ->
            val names = listOf(
                NetworkCapabilities.TRANSPORT_CELLULAR,
                NetworkCapabilities.TRANSPORT_ETHERNET,
                NetworkCapabilities.TRANSPORT_WIFI
            )
            return@let names.any { cap.hasTransport(it) }
        } ?: false
    }
}