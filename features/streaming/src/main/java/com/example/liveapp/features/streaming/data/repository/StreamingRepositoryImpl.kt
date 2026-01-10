package com.example.liveapp.features.streaming.data.repository

import com.example.liveapp.features.streaming.data.datasource.AudioCaptureDataSource
import com.example.liveapp.features.streaming.data.datasource.RTMPDataSource
import com.example.liveapp.features.streaming.data.datasource.ScreenCaptureDataSource
import com.example.liveapp.features.streaming.data.datasource.WebRTCDataSource
import com.example.liveapp.features.streaming.domain.model.NetworkStats
import com.example.liveapp.features.streaming.domain.model.StreamConfig
import com.example.liveapp.features.streaming.domain.model.StreamState
import com.example.liveapp.features.streaming.domain.repository.StreamingRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StreamingRepositoryImpl @Inject constructor(
    private val screenCaptureDataSource: ScreenCaptureDataSource,
    private val audioCaptureDataSource: AudioCaptureDataSource,
    private val rtmpDataSource: RTMPDataSource,
    private val webRTCDataSource: WebRTCDataSource
) : StreamingRepository {

    private val _streamState = MutableStateFlow<StreamState>(StreamState.Idle)
    override fun getStreamState(): Flow<StreamState> = _streamState

    override suspend fun startStream(config: StreamConfig): Result<Unit> {
        return try {
            _streamState.value = StreamState.Preparing

            // Initialize audio capture
            audioCaptureDataSource.initialize()
            val audioStarted = audioCaptureDataSource.startAudioCapture()
            if (!audioStarted) {
                throw Exception("Failed to start audio capture")
            }

            // TODO: Initialize screen capture with MediaProjection
            // For now, assume it's initialized externally

            // Connect to streaming service
            val connectResult = if (config.streamUrl.contains("rtmp")) {
                rtmpDataSource.connect(config.streamUrl, config.streamKey)
            } else {
                webRTCDataSource.initializePeerConnection()
            }

            connectResult.onSuccess {
                val streamResult = if (config.streamUrl.contains("rtmp")) {
                    rtmpDataSource.startStreaming(config)
                } else {
                    webRTCDataSource.startWebRTCStreaming(config)
                }

                streamResult.onSuccess {
                    _streamState.value = StreamState.Streaming(config, System.currentTimeMillis())
                }.onFailure { error ->
                    _streamState.value = StreamState.Error(error.message ?: "Streaming failed")
                }
            }.onFailure { error ->
                _streamState.value = StreamState.Error(error.message ?: "Connection failed")
            }

            Result.success(Unit)
        } catch (e: Exception) {
            _streamState.value = StreamState.Error(e.message ?: "Unknown error")
            Result.failure(e)
        }
    }

    override suspend fun stopStream(): Result<Unit> {
        return try {
            rtmpDataSource.stopStreaming()
            webRTCDataSource.stopWebRTCStreaming()
            audioCaptureDataSource.stopAudioCapture()
            screenCaptureDataSource.stopScreenCapture()

            _streamState.value = StreamState.Stopped
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateConfig(config: StreamConfig): Result<Unit> {
        // TODO: Update streaming configuration dynamically
        return Result.success(Unit)
    }

    override fun getNetworkStats(): Flow<NetworkStats> = flow {
        var consecutiveErrors = 0
        val maxConsecutiveErrors = 5

        while (true) {
            try {
                val currentBitrate = when {
                    rtmpDataSource.getCurrentBitrate() > 0 -> rtmpDataSource.getCurrentBitrate()
                    webRTCDataSource.getCurrentBitrate() > 0 -> webRTCDataSource.getCurrentBitrate()
                    else -> 0
                }

                val droppedFrames = rtmpDataSource.getDroppedFrames() + webRTCDataSource.getDroppedFrames()

                // Check for streaming stability issues
                if (droppedFrames > 100 || currentBitrate < 100) { // Thresholds for stability
                    consecutiveErrors++
                    if (consecutiveErrors >= maxConsecutiveErrors) {
                        // Trigger reconnection or quality adjustment
                        _streamState.value = StreamState.Error("Connection unstable - attempting recovery")
                        // In a real implementation, you'd trigger reconnection logic here
                        consecutiveErrors = 0 // Reset after triggering recovery
                    }
                } else {
                    consecutiveErrors = 0
                }

                emit(NetworkStats(
                    ping = 50, // TODO: Implement actual ping measurement
                    currentBitrate = currentBitrate,
                    droppedFrames = droppedFrames,
                    uploadSpeed = currentBitrate
                ))

                kotlinx.coroutines.delay(1000) // Update every second
            } catch (e: Exception) {
                consecutiveErrors++
                if (consecutiveErrors >= maxConsecutiveErrors) {
                    _streamState.value = StreamState.Error("Streaming session unstable: ${e.message}")
                    break
                }
                kotlinx.coroutines.delay(2000) // Wait longer on errors
            }
        }
    }
}