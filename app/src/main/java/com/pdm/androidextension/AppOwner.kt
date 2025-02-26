package com.pdm.androidextension

import android.app.Application
import com.pdm.adbilling.ads.AdManager

class AppOwner : Application() {
    override fun onCreate() {
        super.onCreate()

        AdManager.configAdUnitId(
            bannerAdUnitId = resources.getStringArray(R.array.banner_ad_unit_id).toList(),
        )
    }
}