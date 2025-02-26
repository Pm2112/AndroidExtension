package com.pdm.adbilling.ads

interface AdmListener {
    fun onAdLoaded(type: TypeAds, adKeyPosition: String)
    fun onAdFailedToLoad(type: TypeAds, adKeyPosition: String, errorCode: Int)
    fun onAdClicked(type: TypeAds, adKeyPosition: String)
    fun onAdClosed(type: TypeAds, adKeyPosition: String)
    fun onAdImpression(type: TypeAds, adKeyPosition: String)
    fun onAdOpened(type: TypeAds, adKeyPosition: String)
    fun onAdSwipeGestureClicked(type: TypeAds, adKeyPosition: String)
}
