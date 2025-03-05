package com.pdm.androidextension.shazam

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.shazam.shazamkit.AudioSampleRateInHz
import com.shazam.shazamkit.DeveloperToken
import com.shazam.shazamkit.DeveloperTokenProvider
import com.shazam.shazamkit.ShazamKit
import com.shazam.shazamkit.ShazamKitResult.*
import com.shazam.shazamkit.StreamingSession
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch


class ShazamManager() {
    private var currentSession: StreamingSession? = null
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @RequiresApi(Build.VERSION_CODES.O)
    fun startShazamRecognition(recordingFlow: Flow<AudioChunk>) {
        coroutineScope.launch {
            try {
                Log.d("ShazamRecognizer", "developerToken: ${ShazamToken.developerToken}")
                val developerTokenProvider = DeveloperTokenProvider { DeveloperToken(ShazamToken.developerToken) }
                // Tạo catalog từ ShazamKit
                val catalogResult = ShazamKit.createShazamCatalog(developerTokenProvider)

                // Tạo Streaming Session
                val sessionResult = ShazamKit.createStreamingSession(
                    catalogResult,
                    AudioSampleRateInHz.SAMPLE_RATE_48000,
                    4096
                )
                if (sessionResult !is Success) {
                    Log.e("ShazamRecognizer", "Không thể tạo Streaming Session")
                    return@launch
                }
                currentSession = sessionResult.data

                Log.d("ShazamRecognizer", "Bắt đầu ghi âm và gửi đến Shazam")

                // Gửi dữ liệu ghi âm vào Streaming Session
                launch {
                    recordingFlow.collect { audioChunk ->
                        Log.d("ShazamRecognizer", "📤 Đang gửi dữ liệu vào matchStream: size=${audioChunk.meaningfulLengthInBytes}")

                        if (audioChunk.meaningfulLengthInBytes > 0) {
                            var offset = 0
                            while (offset < audioChunk.meaningfulLengthInBytes) {
                                val chunkSize = minOf(4096, audioChunk.meaningfulLengthInBytes - offset)
                                val chunkBuffer = audioChunk.buffer.copyOfRange(offset, offset + chunkSize)

                                Log.d("ShazamRecognizer", "➡ Gửi chunk: offset=$offset, size=$chunkSize")

                                currentSession?.matchStream(
                                    chunkBuffer,
                                    chunkSize,
                                    audioChunk.timestamp
                                )
                                offset += chunkSize
                            }
                        } else {
                            Log.e("ShazamRecognizer", "⚠ Dữ liệu PCM rỗng, không thể gửi!")
                        }
                    }
                }

                // Nhận kết quả nhận diện từ Shazam
                launch {
                    currentSession?.recognitionResults()?.collect { matchResult ->
                        Log.d("ShazamRecognizer", "Kết quả nhận diện: $matchResult")
                    }
                }
            } catch (e: Exception) {
                Log.e("ShazamRecognizer", "Lỗi khi nhận diện bài hát: ${e.message}", e)
            }
        }
    }

    fun stopShazamRecognition() {
        coroutineScope.cancel()
        currentSession = null
        Log.d("ShazamRecognizer", "Dừng nhận diện bài hát")
    }
}