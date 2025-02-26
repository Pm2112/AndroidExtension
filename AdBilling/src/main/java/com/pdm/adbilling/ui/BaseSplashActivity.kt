package com.pdm.adbilling.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.pdm.adbilling.ads.AdManager
import com.pdm.adbilling.consent.ConsentManager

abstract class BaseSplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkConsentAndProceed()
    }

    private fun checkConsentAndProceed() {
        ConsentManager.getInstance(this).initialize(this) { canRequestAds ->
            if (canRequestAds) {
                AdManager.initialize(this)
                startNextActivity()
            }
        }
    }

    abstract fun startNextActivity()
}
