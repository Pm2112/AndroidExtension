package com.pdm.androidextension.shazam

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.util.Log
import androidx.core.content.ContextCompat
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.delay

object MicrophoneRecorder {
    private const val SAMPLE_RATE = 48000  // Đảm bảo sử dụng đúng tần số mẫu
    private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
    private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    private val BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT).coerceAtLeast(8192)

    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    @SuppressLint("MissingPermission")
    fun recordShortAudio(context: Context): Flow<AudioChunk> = flow {
        Log.d("MicrophoneRecorder", "Bắt đầu thu âm trong 10 giây...")

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            CHANNEL_CONFIG,
            AUDIO_FORMAT,
            BUFFER_SIZE
        )

        if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
            Log.e("MicrophoneRecorder", "AudioRecord chưa được khởi tạo!")
            throw IllegalStateException("AudioRecord chưa được khởi tạo đúng cách")
        }

        val buffer = ByteArray(BUFFER_SIZE)
        try {
            audioRecord?.startRecording()
            isRecording = true

            val startTime = System.currentTimeMillis()
            var totalBytesRead = 0

            while (isRecording) {
                val bytesRead = audioRecord?.read(buffer, 0, buffer.size) ?: -1
                if (bytesRead > 0) {
                    totalBytesRead += bytesRead
                    emit(AudioChunk(buffer.copyOf(bytesRead), bytesRead, System.currentTimeMillis()))
                }

                // Kiểm tra thời gian thu âm (10 giây)
                if (System.currentTimeMillis() - startTime >= 10000) { // 10 giây
                    Log.d("MicrophoneRecorder", "Đã thu âm đủ 10 giây, dừng ghi âm.")
                    break
                }
            }

            Log.d("MicrophoneRecorder", "Thu âm hoàn tất, tổng số bytes: $totalBytesRead")

        } catch (e: Exception) {
            Log.e("MicrophoneRecorder", "Lỗi thu âm: ${e.message}")
        } finally {
            stopRecording()
        }
    }

    fun stopRecording() {
        isRecording = false
        audioRecord?.apply {
            try {
                stop()
                release()
                Log.d("MicrophoneRecorder", "Đã dừng thu âm.")
            } catch (e: Exception) {
                Log.e("MicrophoneRecorder", "Lỗi khi dừng thu âm: ${e.message}")
            }
        }
        audioRecord = null
    }
}