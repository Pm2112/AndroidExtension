package com.pdm.network

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import java.net.HttpURLConnection
import java.net.URL

class NetworkHelperTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var application: Application

    @Mock
    private lateinit var connectivityManager: ConnectivityManager

    @Mock
    private lateinit var network: Network

    @Mock
    private lateinit var networkCapabilities: NetworkCapabilities

    @Mock
    private lateinit var urlConnection: HttpURLConnection

    private lateinit var networkHelper: NetworkHelper

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)

        // Mock Application -> return connectivityManager
        `when`(application.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(connectivityManager)

        // Khởi tạo NetworkHelper
        networkHelper = NetworkHelper(application)
    }

    @Test
    fun `hasInternetAccess should return true when internet is available`() {
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)

        // Mock URL connection để giả lập Google phản hồi HTTP 204
        val urlMock = mock(URL::class.java)
        `when`(urlMock.openConnection()).thenReturn(urlConnection)
        `when`(urlConnection.responseCode).thenReturn(204)

        assertEquals(true, networkHelper.isNetwork())
    }

    @Test
    fun `hasInternetAccess should return false when there is no internet capability`() {
        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(false)

        assertEquals(false, networkHelper.isNetwork())
    }

    @Test
    fun `hasInternetAccess should return false when Google request fails`() {
        val networkHelper = NetworkHelper(application, "http://fakeurl.com") // Inject URL giả

        `when`(connectivityManager.activeNetwork).thenReturn(network)
        `when`(connectivityManager.getNetworkCapabilities(network)).thenReturn(networkCapabilities)
        `when`(networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)

        assertEquals(false, networkHelper.isNetwork()) // Test thành công vì URL này không tồn tại
    }
}