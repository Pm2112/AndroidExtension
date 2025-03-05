package com.pdm.androidextension.shazam

data class AudioChunk(
    val buffer: ByteArray,
    val meaningfulLengthInBytes: Int,
    val timestamp: Long
)