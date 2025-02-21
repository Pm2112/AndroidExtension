package com.pdm.androidextension

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.pdm.androidextension.databinding.ActivityMainBinding
import com.pdm.permission.PermissionHelper
import com.pdm.permission.PermissionKeys

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var permissionHelper: PermissionHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        permissionHelper = PermissionHelper(activity = this)

        binding.btnPermission.setOnClickListener {
            binding.btnPermission.setOnClickListener {
                permissionHelper.request(
                    PermissionKeys.CAMERA, PermissionKeys.STORAGE
                ) { granted, denied ->
                    if (granted.isNotEmpty()) Toast.makeText(this, "Đã cấp: $granted", Toast.LENGTH_SHORT).show()
                    if (denied.isNotEmpty()) Toast.makeText(this, "Bị từ chối: $denied", Toast.LENGTH_SHORT).show()
                }
            }
        }

    }
}