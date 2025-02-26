package com.pdm.adbilling.ads

import android.content.Context
import android.util.Log
import com.google.android.gms.ads.MobileAds

object AdManager {
    private const val TAG = "AdManager"
    private var isInitialized = false
    val listBannerAdUnitId = mutableListOf<String>()
    val listInterstitialAdUnitId = mutableListOf<String>()
    val listRewardAdUnitId = mutableListOf<String>()
    val listNativeAdUnitId = mutableListOf<String>()
    val listOpenAdUnitId = mutableListOf<String>()

    fun initialize(context: Context) {
        MobileAds.initialize(context)
        isInitialized = true
        Log.d(TAG, "AdManager initialized.")
    }

    fun isInitialized(): Boolean = isInitialized

    fun configAdUnitId(
        bannerAdUnitId: List<String> = emptyList(),
        interstitialAdUnitId: List<String> = emptyList(),
        rewardAdUnitId: List<String> = emptyList(),
        nativeAdUnitId: List<String> = emptyList(),
        openAdUnitId: List<String> = emptyList()
    ) {
        listBannerAdUnitId.addAll(bannerAdUnitId)
        listInterstitialAdUnitId.addAll(interstitialAdUnitId)
        listRewardAdUnitId.addAll(rewardAdUnitId)
        listNativeAdUnitId.addAll(nativeAdUnitId)
        listOpenAdUnitId.addAll(openAdUnitId)
    }
}
