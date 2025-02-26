package com.pdm.androidextension

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pdm.adbilling.ui.BaseActivity
import com.pdm.androidextension.databinding.ActivityDemoBinding

class DemoActivity : BaseActivity() {
    private lateinit var binding: ActivityDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityDemoBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                systemBarsInsets.top,
                view.paddingRight,
                systemBarsInsets.bottom
            )
            insets
        }

        binding.btnStart.setOnClickListener {
            startActivity(Intent(this, DemoActivity::class.java))
        }

        showBannerAd(-1, AdKeyPosition.BANNER_SC_MAIN.name, binding.bannerView)
    }
}