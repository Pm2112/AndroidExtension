package com.pdm.adbilling.ads

import android.app.Activity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.VideoController
import com.google.android.gms.ads.VideoOptions
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.google.android.gms.ads.nativead.NativeAdView
import com.pdm.adbilling.R

class NativeAdManager(
    private val activity: Activity,
    private val lifecycle: Lifecycle
) : DefaultLifecycleObserver {
    companion object {
        private const val TAG = "NativeAd"
        private val TYPE_AD = TypeAds.NATIVE
    }

    init {
        lifecycle.addObserver(this)
    }

    private var nativeAd: NativeAd? = null
    private var nativeAdView: NativeAdView? = null
    private var adUnitIdIndex = 0
    private var adContainerView: ViewGroup? = null

    fun loadAd(
        adContainerView: ViewGroup,
        layoutNativeAdView: Int,
        isVideoOption: Boolean = true,
        isMutedVideo: Boolean = true,
        nativeAdOptions: Int = NativeAdOptions.ADCHOICES_TOP_RIGHT
    ) {
        val nativeAdView =
            LayoutInflater.from(activity).inflate(layoutNativeAdView, null) as NativeAdView

        val loadingView = LayoutInflater.from(activity)
            .inflate(R.layout.loading_native, adContainerView, false)
        this.adContainerView = adContainerView
        this.adContainerView!!.removeAllViews()
        this.adContainerView!!.addView(loadingView)

        val builder = AdLoader.Builder(activity, AdManager.listNativeAdUnitId[adUnitIdIndex])
        builder.forNativeAd { nativeAd ->
            // You must call destroy on old ads when you are done with them,
            // otherwise you will have a memory leak.
            this.nativeAd = nativeAd
            this.nativeAdView = nativeAdView

            populateNativeAdView(nativeAd, nativeAdView)
        }

        changeAdUnitId()

        val adOptions = NativeAdOptions.Builder()
            .setAdChoicesPlacement(nativeAdOptions)

        if (isVideoOption) {
            val videoOptions = VideoOptions.Builder().setStartMuted(isMutedVideo).build()
            adOptions.setVideoOptions(videoOptions)
        }
        val adOptionsBuild = adOptions.build()
        builder.withNativeAdOptions(adOptionsBuild)
        val adLoader = builder.withAdListener(object : AdListener() {
            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                this@NativeAdManager.adContainerView!!.removeAllViews()
            }

            override fun onAdLoaded() {
                super.onAdLoaded()
                this@NativeAdManager.adContainerView!!.removeAllViews()
                this@NativeAdManager.adContainerView!!.addView(nativeAdView)
            }
        }).build()
        adLoader.loadAd(AdRequest.Builder().build())
    }

    private fun populateNativeAdView(nativeAd: NativeAd, nativeAdView: NativeAdView) {
        // Set the media view.
        nativeAdView.mediaView = nativeAdView.findViewById(R.id.ad_media)

        // Set other ad assets.
        nativeAdView.headlineView = nativeAdView.findViewById(R.id.ad_headline)
        nativeAdView.bodyView = nativeAdView.findViewById(R.id.ad_body)
        nativeAdView.callToActionView = nativeAdView.findViewById(R.id.ad_call_to_action)
        nativeAdView.iconView = nativeAdView.findViewById(R.id.ad_app_icon)

        nativeAdView.headlineView?.let {
            (it as TextView).text = nativeAd.headline
        }
        nativeAd.mediaContent?.let { mc ->
            nativeAdView.mediaView?.let {
                it.mediaContent = mc
            }
        }

        nativeAdView.bodyView?.let {
            if (nativeAd.body == null) {
                (it as TextView).visibility = View.INVISIBLE
            } else {
                (it as TextView).visibility = View.VISIBLE
                it.text = nativeAd.body
            }
        }

        nativeAdView.callToActionView?.let {
            if (nativeAd.callToAction == null) {
                (it as TextView).visibility = View.INVISIBLE
            } else {
                (it as TextView).visibility = View.VISIBLE
                it.text = nativeAd.callToAction
            }
        }

        nativeAdView.iconView?.let {
            if (nativeAd.icon == null) {
                (it as ImageView).visibility = View.GONE
            } else {
                (it as ImageView).setImageDrawable(nativeAd.icon?.drawable)
                (it as ImageView).visibility = View.VISIBLE
            }
        }

        // This method tells the Google Mobile Ads SDK that you have finished populating your
        // native ad view with this native ad.
        nativeAdView.setNativeAd(nativeAd)

        // Get the video controller for the ad. One will always be provided, even if the ad doesn't
        // have a video asset.
        val mediaContent = nativeAd.mediaContent
        val vc = mediaContent?.videoController

        // Updates the UI to say whether or not this ad has a video asset.
        if (vc != null && mediaContent.hasVideoContent()) {
            // Create a new VideoLifecycleCallbacks object and pass it to the VideoController. The
            // VideoController will call methods on this object when events occur in the video
            // lifecycle.
            val mediaAspectRatio: Float = mediaContent.aspectRatio
            Log.d(TAG, "populateNativeAdView: mediaAspectRatio = $mediaAspectRatio")
            vc.videoLifecycleCallbacks =
                object : VideoController.VideoLifecycleCallbacks() {
                    override fun onVideoEnd() {
                        // Publishers should allow native ads to complete video playback before
                        // refreshing or replacing them with another ad in the same UI location.
                        super.onVideoEnd()
                    }
                }
        } else {
        }
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        adContainerView?.visibility = VISIBLE
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        adContainerView?.visibility = GONE
    }

    override fun onDestroy(owner: LifecycleOwner) {
        super.onDestroy(owner)
        nativeAd?.destroy()
        nativeAd = null
        nativeAdView = null
    }

    private fun changeAdUnitId() {
        adUnitIdIndex++
        if (adUnitIdIndex >= AdManager.listNativeAdUnitId.size) {
            adUnitIdIndex = 0
        }
    }
}
