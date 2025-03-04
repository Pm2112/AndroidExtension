package com.pdm.androidextension

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm.androidextension.shazam.ShazamManager
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    fun initShazam() {
        viewModelScope.launch {
            ShazamManager.initialize(
                onSuccess = {
                    Log.d("zzzxxx", "Khởi tạo Shazam thành công")
                },
                onError = {
                    Log.d("zzzxxx", "Khởi tạo Shazam thất bại: $it")
                }
            )
        }
    }
}