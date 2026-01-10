package com.example.liveapp.features.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.liveapp.features.streaming.domain.model.StreamPreset
import com.example.liveapp.features.streaming.domain.usecase.DeleteStreamPresetUseCase
import com.example.liveapp.features.streaming.domain.usecase.GetStreamPresetsUseCase
import com.example.liveapp.features.streaming.domain.usecase.SaveStreamPresetUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppSettings(
    val darkMode: Boolean = true,
    val notificationsEnabled: Boolean = true,
    val chatAlerts: Boolean = true,
    val followerAlerts: Boolean = true,
    val defaultQuality: String = "1080p",
    val bitrate: Int = 3000,
    val frameRate: Int = 30,
    val resolution: String = "1920x1080",
    val performanceMode: Boolean = false, // Gaming optimization mode
    val privacyConsentGiven: Boolean = false,
    val dataCollectionConsent: Boolean = false,
    val analyticsEnabled: Boolean = false,
    val highContrastMode: Boolean = false,
    val largerTouchTargets: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val getStreamPresetsUseCase: GetStreamPresetsUseCase,
    private val saveStreamPresetUseCase: SaveStreamPresetUseCase,
    private val deleteStreamPresetUseCase: DeleteStreamPresetUseCase
) : ViewModel() {
    private val _settings = MutableStateFlow(AppSettings())
    val settings: StateFlow<AppSettings> = _settings.asStateFlow()

    private val _streamPresets = MutableStateFlow<List<StreamPreset>>(emptyList())
    val streamPresets: StateFlow<List<StreamPreset>> = _streamPresets.asStateFlow()

    init {
        loadPresets()
    }

    private fun loadPresets() {
        viewModelScope.launch {
            getStreamPresetsUseCase()
                .distinctUntilChanged() // Only emit when presets actually change
                .collect { presets ->
                    _streamPresets.value = presets
                }
        }
    }

    fun toggleDarkMode() {
        _settings.value = _settings.value.copy(darkMode = !_settings.value.darkMode)
    }

    fun toggleNotifications() {
        _settings.value = _settings.value.copy(notificationsEnabled = !_settings.value.notificationsEnabled)
    }

    fun toggleChatAlerts() {
        _settings.value = _settings.value.copy(chatAlerts = !_settings.value.chatAlerts)
    }

    fun toggleFollowerAlerts() {
        _settings.value = _settings.value.copy(followerAlerts = !_settings.value.followerAlerts)
    }

    fun updateQuality(quality: String) {
        _settings.value = _settings.value.copy(defaultQuality = quality)
    }

    fun updateBitrate(bitrate: Int) {
        _settings.value = _settings.value.copy(bitrate = bitrate)
    }

    fun updateFrameRate(frameRate: Int) {
        _settings.value = _settings.value.copy(frameRate = frameRate)
    }

    fun updateResolution(resolution: String) {
        _settings.value = _settings.value.copy(resolution = resolution)
    }

    fun togglePerformanceMode() {
        _settings.value = _settings.value.copy(performanceMode = !_settings.value.performanceMode)
    }

    fun saveStreamPreset(preset: StreamPreset) {
        viewModelScope.launch {
            saveStreamPresetUseCase(preset)
        }
    }

    fun deleteStreamPreset(id: Long) {
        viewModelScope.launch {
            deleteStreamPresetUseCase(id)
        }
    }

    fun givePrivacyConsent() {
        _settings.value = _settings.value.copy(privacyConsentGiven = true)
    }

    fun toggleDataCollectionConsent() {
        _settings.value = _settings.value.copy(dataCollectionConsent = !_settings.value.dataCollectionConsent)
    }

    fun toggleAnalytics() {
        _settings.value = _settings.value.copy(analyticsEnabled = !_settings.value.analyticsEnabled)
    }

    fun toggleHighContrastMode() {
        _settings.value = _settings.value.copy(highContrastMode = !_settings.value.highContrastMode)
    }

    fun toggleLargerTouchTargets() {
        _settings.value = _settings.value.copy(largerTouchTargets = !_settings.value.largerTouchTargets)
    }

    fun requestDataDeletion() {
        // Implement data deletion logic here
        // This would typically involve calling a use case to delete user data from server and local storage
        viewModelScope.launch {
            // TODO: Implement data deletion
        }
    }
}