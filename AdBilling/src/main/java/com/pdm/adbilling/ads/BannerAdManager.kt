package com.pdm.adbilling.ads

import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowMetrics
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.pdm.adbilling.R

class BannerAdManager(
    private val activity: Activity,
    private val lifecycle: Lifecycle,
) : DefaultLifecycleObserver {
    companion object {
        private const val TAG = "BannerAd"
        private val TYPE_AD = TypeAds.BANNER
    }

    init {
        lifecycle.addObserver(this)
    }

    private var adView: AdView? = null
    private var contentLoader: View? = null
    private var adUnitIdIndex = 0

    private val adSize: AdSize
        get() {
            val displayMetrics = activity.resources.displayMetrics
            val adWidthPixels =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                    val windowMetrics: WindowMetrics = activity.windowManager.currentWindowMetrics
                    windowMetrics.bounds.width()
                } else {
                    displayMetrics.widthPixels
                }
            val density = displayMetrics.density
            val adWidth = (adWidthPixels / density).toInt()
            return AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(activity, adWidth)
        }

    fun loadBanner(adContainer: ViewGroup) {
        if (!AdManager.isInitialized()) {
            Log.w(TAG, "MobileAds uninitialized. Not loading ad.")
            return
        }
        val adView = AdView(activity)
        adView.adUnitId = AdManager.listBannerAdUnitId[adUnitIdIndex]

        changeAdUnitId()

        adView.setAdSize(adSize)
        this.adView = adView

        contentLoader = LayoutInflater.from(activity)
            .inflate(R.layout.loading_banner, adContainer, false)

        adContainer.removeAllViews()
        adContainer.addView(contentLoader)

        adView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adContainer.removeAllViews()
                adContainer.addView(adView)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                adContainer.removeAllViews()
            }
        }

        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun changeAdUnitId() {
        adUnitIdIndex++
        if (adUnitIdIndex >= AdManager.listBannerAdUnitId.size) {
            adUnitIdIndex = 0
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        adView?.resume()
        Log.d(TAG, "BannerAd resumed.")
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        adView?.pause()
        Log.d(TAG, "BannerAd paused.")
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        adView?.destroy()
        Log.d(TAG, "BannerAd destroyed.")
    }
}
