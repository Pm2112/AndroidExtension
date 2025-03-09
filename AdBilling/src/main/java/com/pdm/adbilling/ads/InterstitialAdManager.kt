package com.pdm.adbilling.ads

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.pdm.adbilling.R
import com.pdm.adbilling.databinding.PopupLoadAdBinding

class InterstitialAdManager(
    private val activity: Activity
) {
    init {
        loadAd()
    }

    private var dialogLoadAds: Dialog? = null
    private var interstitialAd: InterstitialAd? = null
    private var adUnitIdIndex: Int = 0

    private fun loadAd() {
        val adRequest = AdRequest.Builder().build()

        InterstitialAd.load(
            activity,
            AdManager.listInterstitialAdUnitId[adUnitIdIndex],
            adRequest,
            object : InterstitialAdLoadCallback() {
                override fun onAdLoaded(ad: InterstitialAd) {
                    interstitialAd = ad
                    Log.d("InterstitialAd", "Ad Loaded")
                }

                override fun onAdFailedToLoad(error: com.google.android.gms.ads.LoadAdError) {
                    Log.e("InterstitialAd", "Ad Failed to Load: ${error.message}")
                    interstitialAd = null
                }
            }
        )

        changeAdUnitId()
    }

    fun showAd(activity: Activity, onAdClosed: () -> Unit) {
        dialogLoadAds = Dialog(activity)
        dialogLoadAds?.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogLoadAds?.setCancelable(false)
        dialogLoadAds?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val binding = PopupLoadAdBinding.inflate(dialogLoadAds!!.layoutInflater)
        dialogLoadAds?.setContentView(binding.getRoot())
        dialogLoadAds?.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (interstitialAd != null) {
                interstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        interstitialAd = null
                        loadAd() // Tải lại quảng cáo mới
                        dialogLoadAds?.dismiss()
                        onAdClosed()
                    }

                    override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                        interstitialAd = null
                        loadAd()
                        dialogLoadAds?.dismiss()
                        onAdClosed()
                    }
                }
                interstitialAd?.show(activity)
            } else {
                dialogLoadAds?.dismiss()
                onAdClosed()
            }
        }, 1000)
    }

    private fun changeAdUnitId() {
        adUnitIdIndex++
        if (adUnitIdIndex >= AdManager.listNativeAdUnitId.size) {
            adUnitIdIndex = 0
        }
    }
}
