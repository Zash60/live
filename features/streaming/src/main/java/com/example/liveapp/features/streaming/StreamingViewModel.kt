package com.example.liveapp.features.streaming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveapp.features.streaming.domain.model.NetworkStats
import com.example.liveapp.features.streaming.domain.model.QualityPreset
import com.example.liveapp.features.streaming.domain.model.StreamConfig
import com.example.liveapp.features.streaming.domain.model.StreamState
import com.example.liveapp.features.streaming.domain.usecase.GetNetworkStatsUseCase
import com.example.liveapp.features.streaming.domain.usecase.StartStreamUseCase
import com.example.liveapp.features.streaming.domain.usecase.StopStreamUseCase
import com.example.liveapp.features.streaming.domain.usecase.UpdateSettingsUseCase
import com.example.liveapp.features.streaming.domain.usecase.CreateLiveEventUseCase
import com.example.liveapp.features.streaming.domain.usecase.UpdateThumbnailUseCase
import com.example.liveapp.features.streaming.domain.model.YouTubePrivacyStatus
import com.example.liveapp.features.streaming.data.datasource.YouTubeAuthManager
import com.example.liveapp.features.streaming.utils.BatteryOptimizationManager
import com.example.liveapp.core.PerformanceMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StreamingViewModel @Inject constructor(
    private val startStreamUseCase: StartStreamUseCase,
    private val stopStreamUseCase: StopStreamUseCase,
    private val updateSettingsUseCase: UpdateSettingsUseCase,
    private val getNetworkStatsUseCase: GetNetworkStatsUseCase,
    private val createLiveEventUseCase: CreateLiveEventUseCase,
    private val updateThumbnailUseCase: UpdateThumbnailUseCase,
    private val youTubeAuthManager: YouTubeAuthManager,
    private val batteryOptimizationManager: BatteryOptimizationManager,
    private val performanceMonitor: PerformanceMonitor
) : ViewModel() {

    private val _streamState = MutableStateFlow<StreamState>(StreamState.Idle)
    val streamState: StateFlow<StreamState> = _streamState.asStateFlow()

    private val _networkStats = MutableStateFlow(NetworkStats())
    val networkStats: StateFlow<NetworkStats> = _networkStats.asStateFlow()

    private val _currentConfig = MutableStateFlow(StreamConfig())
    val currentConfig: StateFlow<StreamConfig> = _currentConfig.asStateFlow()

    private val _qualityPresets = MutableStateFlow(QualityPreset.ALL)
    val qualityPresets: StateFlow<List<QualityPreset>> = _qualityPresets.asStateFlow()

    val batteryLevel = batteryOptimizationManager.batteryLevel
    val isCharging = batteryOptimizationManager.isCharging
    val powerSaveMode = batteryOptimizationManager.powerSaveMode
    val performanceMetrics = performanceMonitor.performanceMetrics

    init {
        viewModelScope.launch {
            getNetworkStatsUseCase()
                .distinctUntilChanged() // Only emit when stats actually change
                .debounce(500) // Wait 500ms after last emission before processing
                .collectLatest { stats ->
                    _networkStats.value = stats
                }
        }
    }

    fun startStream() {
        performanceMonitor.startMonitoring()
        val timer = performanceMonitor.startTiming("Stream Start")
        viewModelScope.launch {
            startStreamUseCase(_currentConfig.value)
            timer.end()
        }
    }

    fun stopStream() {
        val timer = performanceMonitor.startTiming("Stream Stop")
        viewModelScope.launch {
            stopStreamUseCase()
            timer.end()
        }
        performanceMonitor.stopMonitoring()
    }

    fun updateConfig(config: StreamConfig) {
        _currentConfig.value = config
        viewModelScope.launch {
            updateSettingsUseCase(config)
        }
    }

    fun selectQualityPreset(preset: QualityPreset) {
        val currentConfig = _currentConfig.value

        // If performance mode is enabled, prioritize quality over battery savings
        val (finalPreset, finalFps) = if (currentConfig.performanceMode) {
            // In performance mode, use higher quality and FPS, but still respect some battery limits
            val performancePreset = when (preset) {
                QualityPreset.SD_480P -> QualityPreset.HD_720P
                else -> preset
            }
            val performanceFps = minOf(preset.fps, 60) // Cap at 60 FPS for gaming
            Pair(performancePreset, performanceFps)
        } else {
            // Apply battery optimization to the selected preset
            val optimizedPreset = batteryOptimizationManager.getAdaptiveQualityPreset(preset)
            val adaptiveFps = batteryOptimizationManager.getAdaptiveFrameRate(preset.fps)
            Pair(optimizedPreset, adaptiveFps)
        }

        val updatedConfig = currentConfig.copy(
            resolution = finalPreset.resolution,
            bitrate = finalPreset.bitrate,
            fps = finalFps
        )
        _currentConfig.value = updatedConfig
        viewModelScope.launch {
            updateSettingsUseCase(updatedConfig)
        }
    }

    fun updateStreamUrl(url: String) {
        val updatedConfig = _currentConfig.value.copy(streamUrl = url)
        _currentConfig.value = updatedConfig
    }

    fun updateStreamKey(key: String) {
        val updatedConfig = _currentConfig.value.copy(streamKey = key)
        _currentConfig.value = updatedConfig
    }

    fun toggleAudio(enabled: Boolean) {
        val updatedConfig = _currentConfig.value.copy(audioEnabled = enabled)
        updateConfig(updatedConfig)
    }

    fun toggleMicrophone(enabled: Boolean) {
        val updatedConfig = _currentConfig.value.copy(microphoneEnabled = enabled)
        updateConfig(updatedConfig)
    }

    fun toggleCameraOverlay(enabled: Boolean) {
        val updatedConfig = _currentConfig.value.copy(cameraOverlayEnabled = enabled)
        updateConfig(updatedConfig)
    }

    // YouTube Live methods
    fun updateUseYouTubeLive(enabled: Boolean) {
        val updatedConfig = _currentConfig.value.copy(useYouTubeLive = enabled)
        _currentConfig.value = updatedConfig
    }

    fun updateYouTubeTitle(title: String) {
        val updatedConfig = _currentConfig.value.copy(youTubeEventTitle = title)
        _currentConfig.value = updatedConfig
    }

    fun updateYouTubeDescription(description: String) {
        val updatedConfig = _currentConfig.value.copy(youTubeEventDescription = description)
        _currentConfig.value = updatedConfig
    }

    fun updateYouTubePrivacy(privacy: YouTubePrivacyStatus) {
        val updatedConfig = _currentConfig.value.copy(youTubePrivacyStatus = privacy)
        _currentConfig.value = updatedConfig
    }

    fun createYouTubeEvent() {
        val credential = youTubeAuthManager.getCredential()
        if (credential == null) {
            // TODO: Handle not signed in - trigger sign in flow
            println("User not signed in to YouTube")
            return
        }

        viewModelScope.launch {
            val config = _currentConfig.value
            val event = com.example.liveapp.features.streaming.domain.model.YouTubeLiveEvent(
                title = config.youTubeEventTitle,
                description = config.youTubeEventDescription,
                privacyStatus = when (config.youTubePrivacyStatus) {
                    YouTubePrivacyStatus.PUBLIC -> com.example.liveapp.features.streaming.domain.model.YouTubeLiveEvent.PrivacyStatus.PUBLIC
                    YouTubePrivacyStatus.PRIVATE -> com.example.liveapp.features.streaming.domain.model.YouTubeLiveEvent.PrivacyStatus.PRIVATE
                    YouTubePrivacyStatus.UNLISTED -> com.example.liveapp.features.streaming.domain.model.YouTubeLiveEvent.PrivacyStatus.UNLISTED
                }
            )

            createLiveEventUseCase(credential, event).fold(
                onSuccess = { (broadcast, stream) ->
                    // Update config with broadcast and stream IDs, and RTMP details
                    val updatedConfig = config.copy(
                        youTubeBroadcastId = broadcast.id,
                        youTubeStreamId = stream.streamId,
                        streamUrl = stream.rtmpUrl,
                        streamKey = stream.streamKey
                    )
                    _currentConfig.value = updatedConfig
                },
                onFailure = { error ->
                    // TODO: Handle error - show snackbar or dialog
                    // For now, just log
                    println("Error creating YouTube event: ${error.message}")
                }
            )
        }
    }

    fun updateYouTubeThumbnail(thumbnailUri: String) {
        val credential = youTubeAuthManager.getCredential()
        if (credential == null) {
            println("User not signed in to YouTube")
            return
        }

        viewModelScope.launch {
            val broadcastId = _currentConfig.value.youTubeBroadcastId
            if (broadcastId != null) {
                updateThumbnailUseCase(credential, broadcastId, thumbnailUri).fold(
                    onSuccess = {
                        // TODO: Show success message
                    },
                    onFailure = { error ->
                        // TODO: Handle error
                        println("Error updating thumbnail: ${error.message}")
                    }
                )
            }
        }
    }

    fun togglePerformanceMode() {
        val currentConfig = _currentConfig.value
        val newPerformanceMode = !currentConfig.performanceMode

        // When enabling performance mode, optimize for gaming (lower latency, higher FPS if possible)
        val updatedConfig = if (newPerformanceMode) {
            currentConfig.copy(
                performanceMode = true,
                fps = minOf(currentConfig.fps, 60), // Cap at 60 FPS for performance
                bitrate = maxOf(currentConfig.bitrate, 4000) // Higher bitrate for quality
            )
        } else {
            currentConfig.copy(performanceMode = false)
        }

        _currentConfig.value = updatedConfig
        viewModelScope.launch {
            updateSettingsUseCase(updatedConfig)
        }
    }
}