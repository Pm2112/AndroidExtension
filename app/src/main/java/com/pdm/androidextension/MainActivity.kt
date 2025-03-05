package com.pdm.androidextension

import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.pdm.adbilling.ui.BaseActivity
import com.pdm.androidextension.databinding.ActivityMainBinding
import com.pdm.androidextension.shazam.AppleDeveloperTokenGenerator
import com.pdm.androidextension.shazam.AudioChunk
import com.pdm.androidextension.shazam.MicrophoneRecorder
import com.pdm.androidextension.shazam.ShazamManager
import com.pdm.androidextension.shazam.ShazamToken
import com.pdm.audio.record.AudioRecorder
import com.pdm.permission.PermissionHelper
import com.pdm.permission.PermissionKeys.AUDIO_RECORD
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class MainActivity : BaseActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mainViewModel: MainViewModel
    private val REQUEST_RECORD_AUDIO_PERMISSION = 200
    private lateinit var audioRecorder: AudioRecorder


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mainViewModel = ViewModelProvider(this)[MainViewModel::class.java]
        binding = ActivityMainBinding.inflate(layoutInflater)
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

        ShazamToken.init(this)
        val shazamManager = ShazamManager()

        audioRecorder = AudioRecorder(this)

        binding.btnStart.setOnClickListener {
            shazamManager.startShazamRecognition(recordingFlow())
        }

        binding.btnStop.setOnClickListener {
//            audioRecorder.stopRecording()
//            val dataPCM = audioRecorder.getPcmData()
//            if (dataPCM.isNotEmpty()) {
//                mainViewModel.recognizeShazam(dataPCM, developerToken)
//            } else {
//                Log.e("MainActivity", "Không có dữ liệu PCM để gửi đi.")
//            }
            shazamManager.stopShazamRecognition()
        }


        showBannerAd(-1, AdKeyPosition.BANNER_SC_MAIN.name, binding.bannerView)
    }

    override fun onDestroy() {
        super.onDestroy()
        MicrophoneRecorder.stopRecording()
    }

    private fun recordingFlow(): Flow<AudioChunk> = flow {
        Log.d("ShazamRecognizer", "🔥 Bắt đầu ghi âm luồng dữ liệu...")

        audioRecorder.startRecording() // Bắt đầu ghi âm
        while (audioRecorder.isRecording()) { // Kiểm tra nếu đang ghi
            val pcmData = audioRecorder.getPcmData()
            if (pcmData.isNotEmpty()) {
                emit(AudioChunk(pcmData, pcmData.size, System.currentTimeMillis()))
            } else {
                Log.e("ShazamRecognizer", "⚠️ Không có dữ liệu âm thanh từ microphone!")
            }
        }
        audioRecorder.stopRecording() // Dừng ghi âm khi thoát vòng lặp
    }
}
