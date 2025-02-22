package com.pdm.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.lifecycle.LiveData
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

class NetworkHelper(application: Application, private val testUrl: String = INTERNET_TEST_URL) : LiveData<Boolean>() {
    companion object {
        private const val INTERNET_TEST_URL = "http://clients3.google.com/generate_204"
        private const val HTTP_NO_CONTENT = 204
        private const val CONNECTION_TIMEOUT = 1500
        private const val TAG = "NetworkHelper"
    }

    private val connectivityManager: ConnectivityManager =
        application.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            postValue(hasInternetAccess())
        }

        override fun onLost(network: Network) {
            postValue(false)
        }
    }

    override fun onActive() {
        super.onActive()
        postValue(hasInternetAccess())

        val builder = NetworkRequest.Builder()
        connectivityManager.registerNetworkCallback(builder.build(), networkCallback)
    }

    override fun onInactive() {
        super.onInactive()
        if (!hasActiveObservers()) {
            connectivityManager.unregisterNetworkCallback(networkCallback)
        }
    }

    /**
     * Kiểm tra kết nối internet thực sự.
     */
    private fun hasInternetAccess(): Boolean {
        val network = connectivityManager.activeNetwork
        val capabilities = connectivityManager.getNetworkCapabilities(network)

        val isConnected =
            capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        if (!isConnected) return false

        return checkInternetConnection()
    }

    /**
     * Thử kết nối đến Google để kiểm tra internet.
     */
    private fun checkInternetConnection(): Boolean {
        return try {
            val url = URL(testUrl) // Sử dụng testUrl thay vì URL cố định
            (url.openConnection() as HttpURLConnection).apply {
                setRequestProperty("User-Agent", "Android")
                setRequestProperty("Connection", "close")
                connectTimeout = CONNECTION_TIMEOUT
                connect()
            }.responseCode == HTTP_NO_CONTENT
        } catch (e: IOException) {
//            Log.e(TAG, "Internet check failed", e)
            false
        }
    }

    fun isNetwork(): Boolean = hasInternetAccess()
}