package com.pdm.adbilling.ui

import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.pdm.adbilling.ads.BannerAd

abstract class BaseActivity : AppCompatActivity() {
    companion object {
        private const val TAG = "BaseActivity"
    }

    private val bannerAd: BannerAd by lazy {
        BannerAd.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        bannerAd.resume()
    }

    override fun onPause() {
        super.onPause()
        bannerAd.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerAd.destroy()
    }

    open fun showBannerAd(id: Int = -1, adKeyPosition: String, bannerView: ViewGroup) {
        bannerAd.loadBanner(id, adKeyPosition, bannerView)
    }
}