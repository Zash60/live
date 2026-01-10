package com.example.liveapp.features.streaming.data.datasource

import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import kotlin.coroutines.resume

class AudioCaptureDataSource @Inject constructor() {

    private var audioRecord: AudioRecord? = null
    private var isRecording = false

    fun initialize() {
        val sampleRate = 44100
        val channelConfig = AudioFormat.CHANNEL_IN_MONO
        val audioFormat = AudioFormat.ENCODING_PCM_16BIT
        val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

        audioRecord = AudioRecord(
            MediaRecorder.AudioSource.MIC,
            sampleRate,
            channelConfig,
            audioFormat,
            bufferSize
        )
    }

    suspend fun startAudioCapture(): Boolean {
        return suspendCancellableCoroutine { continuation ->
            try {
                audioRecord?.startRecording()
                isRecording = true
                continuation.resume(true)
            } catch (e: Exception) {
                continuation.resume(false)
            }
        }
    }

    fun stopAudioCapture() {
        try {
            if (isRecording) {
                audioRecord?.stop()
                isRecording = false
            }
        } catch (e: Exception) {
            // Handle exception
        }
    }

    fun readAudioData(buffer: ByteArray, offset: Int, size: Int): Int {
        return audioRecord?.read(buffer, offset, size) ?: 0
    }

    fun release() {
        audioRecord?.release()
        audioRecord = null
    }

    // Note: System audio capture requires additional permissions and APIs
    // This would typically involve AudioPlaybackCapture for Android 10+
    // For now, only microphone is implemented
}