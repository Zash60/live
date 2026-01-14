package com.example.liveapp.features.streaming

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveapp.core.PerformanceMonitor
import com.example.liveapp.domain.model.StreamConfig
import com.example.liveapp.domain.model.YouTubePrivacyStatus
import com.example.liveapp.features.streaming.data.datasource.YouTubeAuthManager
import com.example.liveapp.features.streaming.domain.model.NetworkStats
import com.example.liveapp.features.streaming.domain.model.QualityPreset
import com.example.liveapp.features.streaming.domain.model.StreamState
import com.example.liveapp.features.streaming.domain.model.YouTubeLiveEvent
import com.example.liveapp.features.streaming.domain.usecase.CreateLiveEventUseCase
import com.example.liveapp.features.streaming.domain.usecase.GetNetworkStatsUseCase
import com.example.liveapp.features.streaming.domain.usecase.StartStreamUseCase
import com.example.liveapp.features.streaming.domain.usecase.StopStreamUseCase
import com.example.liveapp.features.streaming.domain.usecase.UpdateSettingsUseCase
import com.example.liveapp.features.streaming.domain.usecase.UpdateThumbnailUseCase
import com.example.liveapp.features.streaming.utils.BatteryOptimizationManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.min
import kotlin.math.max

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
                .distinctUntilChanged()
                .debounce(500)
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

        val (finalPreset, finalFps) = if (currentConfig.performanceMode) {
            val performancePreset = when (preset) {
                QualityPreset.LOW -> QualityPreset.MEDIUM // Use Medium instead of Low for mapping
                else -> preset
            }
            val performanceFps = min(preset.fps, 60)
            Pair(performancePreset, performanceFps)
        } else {
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
            println("User not signed in to YouTube")
            return
        }

        viewModelScope.launch {
            val config = _currentConfig.value
            val event = YouTubeLiveEvent(
                title = config.youTubeEventTitle,
                description = config.youTubeEventDescription,
                privacyStatus = when (config.youTubePrivacyStatus) {
                    YouTubePrivacyStatus.PUBLIC -> YouTubeLiveEvent.PrivacyStatus.PUBLIC
                    YouTubePrivacyStatus.PRIVATE -> YouTubeLiveEvent.PrivacyStatus.PRIVATE
                    YouTubePrivacyStatus.UNLISTED -> YouTubeLiveEvent.PrivacyStatus.UNLISTED
                }
            )

            createLiveEventUseCase(credential, event).fold(
                onSuccess = { (broadcast, stream) ->
                    val updatedConfig = config.copy(
                        youTubeBroadcastId = broadcast.id,
                        youTubeStreamId = stream.streamId,
                        streamUrl = stream.rtmpUrl,
                        streamKey = stream.streamKey
                    )
                    _currentConfig.value = updatedConfig
                },
                onFailure = { error ->
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
                    onSuccess = { },
                    onFailure = { error ->
                        println("Error updating thumbnail: ${error.message}")
                    }
                )
            }
        }
    }

    fun togglePerformanceMode() {
        val currentConfig = _currentConfig.value
        val newPerformanceMode = !currentConfig.performanceMode

        val updatedConfig = if (newPerformanceMode) {
            currentConfig.copy(
                performanceMode = true,
                fps = min(currentConfig.fps, 60),
                bitrate = max(currentConfig.bitrate, 4000)
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
