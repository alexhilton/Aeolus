package net.toughcoder.aeolus.data.network

class NoConnection : Connectivity {
    override fun isOnline() = false
}