package com.pdm.androidextension

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.shazam.shazamkit.AudioSampleRateInHz
import com.shazam.shazamkit.DeveloperToken
import com.shazam.shazamkit.DeveloperTokenProvider
import com.shazam.shazamkit.ShazamKit
import com.shazam.shazamkit.ShazamKitResult
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
//    fun recognizeShazam(pcmData: ByteArray, developerToken: String) {
//        viewModelScope.launch {
//            try {
//                val tokenProvider = DeveloperTokenProvider { DeveloperToken(developerToken) }
//                Log.d("Shazam", "tokenProvider: $tokenProvider")
//                // Tạo bộ tạo chữ ký (SignatureGenerator)
//                val signatureGeneratorResult =
//                    ShazamKit.createSignatureGenerator(AudioSampleRateInHz.SAMPLE_RATE_48000)
//                if (signatureGeneratorResult is ShazamKitResult.Success) {
//                    val signatureGenerator = signatureGeneratorResult.data
//                    Log.d("Shazam", "signatureGenerator: $signatureGenerator")
//                    val meaningfulLength = getMeaningfulPCMSize(pcmData)
//                    signatureGenerator.append(pcmData, meaningfulLength, System.currentTimeMillis())
//
//                    // Tạo chữ ký
//                    val signature = signatureGenerator.generateSignature()
//                    Log.d("Shazam", "signature: $signature")
//                    // Tạo Catalog và Session
//                    val catalog = ShazamKit.createShazamCatalog(tokenProvider)
//                    Log.d("Shazam", "catalog: $catalog")
//                    val sessionResult = ShazamKit.createSession(catalog)
//                    Log.d("Shazam", "sessionResult: $sessionResult")
//
//                    if (sessionResult is ShazamKitResult.Success) {
//                        val session = sessionResult.data
//
//                        // Gửi chữ ký đi để nhận diện bài hát
//                        val matchResult = session.match(signature)
//                        Log.d("Shazam", "Match Result: $matchResult")
//                    } else {
//                        Log.e("Shazam", "Không thể tạo Shazam Session")
//                    }
//                } else {
//                    Log.e("Shazam", "Không thể tạo Signature Generator")
//                }
//            } catch (e: Exception) {
//                Log.e("Shazam", "Lỗi nhận diện bài hát: ${e.message}")
//            }
//        }
//    }
//
//    private fun getMeaningfulPCMSize(pcmData: ByteArray): Int {
//        val meaningfulThreshold = 100
//        var lastMeaningfulIndex = 0
//        for (i in pcmData.indices step 2) {
//            val amplitude = ((pcmData[i + 1].toInt() shl 8) or (pcmData[i].toInt() and 0xFF)).toShort()
//            if (Math.abs(amplitude.toInt()) > meaningfulThreshold) {
//                lastMeaningfulIndex = i
//            }
//        }
//        return lastMeaningfulIndex + 2
//    }

    fun startShazam() {

    }
}
