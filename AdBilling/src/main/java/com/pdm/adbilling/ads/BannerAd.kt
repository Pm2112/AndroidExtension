package com.pdm.adbilling.ads

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowMetrics
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.pdm.adbilling.R

class BannerAd(
    private val activity: Activity,
    private val adListener: AdmListener? = null
) {
    companion object {
        private const val TAG = "BannerAd"
        private val TYPE_AD = TypeAds.BANNER
        @SuppressLint("StaticFieldLeak")
        private var instance: BannerAd? = null

        fun getInstance(activity: Activity, adListener: AdmListener? = null): BannerAd {
            return instance ?: BannerAd(activity, adListener).also { instance = it }
        }
    }

    private var adView: AdView? = null
    private var contentLoader: View? = null
    private var currentIndex = 0

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

    fun loadBanner(id: Int = -1, adKeyPosition: String, adContainer: ViewGroup) {
        if (!AdManager.isInitialized()) {
            Log.w(TAG, "MobileAds chưa khởi tạo. Không tải quảng cáo.")
            return
        }

        // Kiểm tra nếu đã có banner đang hiển thị, không tạo lại
        if (adView != null && adView?.parent == adContainer) {
            Log.d(TAG, "Banner đã tồn tại, không tạo lại.")
            return
        }

        // Thêm ContentLoader để hiển thị loading
        contentLoader = LayoutInflater.from(activity)
            .inflate(R.layout.banner_loading, adContainer, false)
        adContainer.removeAllViews()
        adContainer.addView(contentLoader)

        val newAdView = AdView(activity)
        newAdView.adUnitId = if (id == -1) getNextBannerId() else AdManager.listBannerAdUnitId[id]
        newAdView.setAdSize(adSize)

        adView = newAdView

        val adRequest = AdRequest.Builder().build()
        newAdView.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adListener?.onAdLoaded(TYPE_AD, adKeyPosition)
                hideContentLoader()
                adContainer.removeAllViews()
                adContainer.addView(newAdView)
            }

            override fun onAdFailedToLoad(adError: LoadAdError) {
                super.onAdFailedToLoad(adError)
                adListener?.onAdFailedToLoad(TYPE_AD, adKeyPosition, adError.code)
                hideContentLoader()
            }
        }

        newAdView.loadAd(adRequest)
    }

    private fun hideContentLoader() {
        contentLoader?.visibility = View.GONE
    }

    private fun getNextBannerId(): String {
        val id = AdManager.listBannerAdUnitId[currentIndex]
        currentIndex = (currentIndex + 1) % AdManager.listBannerAdUnitId.size
        return id
    }

    fun resume() {
        adView?.resume()
    }

    fun pause() {
        adView?.pause()
    }

    fun destroy() {
        adView?.destroy()
        adView = null
    }
}