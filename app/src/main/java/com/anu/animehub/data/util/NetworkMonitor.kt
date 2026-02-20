package com.anu.animehub.data.util

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

interface NetworkMonitor {
    val isOnline: Flow<Boolean>
}

class DefaultNetworkMonitor(private val context: Context) : NetworkMonitor {

    override val isOnline: Flow<Boolean> = callbackFlow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(isNetworkAvailable(cm))
            }
            override fun onLost(network: Network) {
                trySend(isNetworkAvailable(cm))
            }
            override fun onCapabilitiesChanged(network: Network, caps: NetworkCapabilities) {
                trySend(isNetworkAvailable(cm))
            }
        }
        cm.registerDefaultNetworkCallback(callback)
        trySend(isNetworkAvailable(cm))
        awaitClose { cm.unregisterNetworkCallback(callback) }
    }

    private fun isNetworkAvailable(cm: ConnectivityManager): Boolean {
        val network = cm.activeNetwork ?: return false
        val caps = cm.getNetworkCapabilities(network) ?: return false
        return caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            caps.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
    }
}
