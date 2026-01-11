package com.example.liveapp.features.streaming.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.PowerManager
import com.example.liveapp.features.streaming.domain.model.QualityPreset
import com.example.liveapp.domain.model.StreamConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BatteryOptimizationManager @Inject constructor(
    private val context: Context
) {

    private val _batteryLevel = MutableStateFlow(100)
    val batteryLevel: StateFlow<Int> = _batteryLevel.asStateFlow().distinctUntilChanged()

    private val _isCharging = MutableStateFlow(false)
    val isCharging: StateFlow<Boolean> = _isCharging.asStateFlow().distinctUntilChanged()

    private val _powerSaveMode = MutableStateFlow(false)
    val powerSaveMode: StateFlow<Boolean> = _powerSaveMode.asStateFlow().distinctUntilChanged()

    private val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager

    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                Intent.ACTION_BATTERY_CHANGED -> {
                    val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 100)
                    val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
                    _batteryLevel.value = (level * 100) / scale

                    val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
                    _isCharging.value = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                                       status == BatteryManager.BATTERY_STATUS_FULL

                    _powerSaveMode.value = powerManager.isPowerSaveMode
                }
                Intent.ACTION_POWER_SAVE_MODE_CHANGED -> {
                    _powerSaveMode.value = powerManager.isPowerSaveMode
                }
            }
        }
    }

    init {
        registerBatteryReceiver()
        updateInitialBatteryState()
    }

    private fun registerBatteryReceiver() {
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_BATTERY_CHANGED)
            addAction(Intent.ACTION_POWER_SAVE_MODE_CHANGED)
        }
        context.registerReceiver(batteryReceiver, filter)
    }

    private fun updateInitialBatteryState() {
        val batteryIntent = context.registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        batteryIntent?.let { intent ->
            val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 100)
            val scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100)
            _batteryLevel.value = (level * 100) / scale

            val status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1)
            _isCharging.value = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                               status == BatteryManager.BATTERY_STATUS_FULL
        }
        _powerSaveMode.value = powerManager.isPowerSaveMode
    }

    fun getAdaptiveQualityPreset(originalPreset: QualityPreset): QualityPreset {
        val batteryLevel = _batteryLevel.value
        val isCharging = _isCharging.value
        val powerSaveMode = _powerSaveMode.value

        // If charging, use original quality
        if (isCharging) return originalPreset

        // If power save mode is on, reduce quality significantly
        if (powerSaveMode) {
            return when (originalPreset) {
                QualityPreset.ULTRA -> QualityPreset.HIGH
                QualityPreset.HIGH -> QualityPreset.MEDIUM
                QualityPreset.MEDIUM -> QualityPreset.LOW
                else -> QualityPreset.LOW
            }
        }

        // Adaptive quality based on battery level
        return when {
            batteryLevel > 50 -> originalPreset
            batteryLevel > 20 -> {
                // Reduce quality when battery is between 20-50%
                when (originalPreset) {
                    QualityPreset.ULTRA -> QualityPreset.HIGH
                    QualityPreset.HIGH -> QualityPreset.MEDIUM
                    else -> originalPreset
                }
            }
            else -> {
                // Critical battery - use lowest quality
                QualityPreset.SD_480P
            }
        }
    }

    fun shouldReduceFrameRate(): Boolean {
        val batteryLevel = _batteryLevel.value
        val isCharging = _isCharging.value
        val powerSaveMode = _powerSaveMode.value

        return !isCharging && (powerSaveMode || batteryLevel < 30)
    }

    fun getAdaptiveFrameRate(originalFps: Int): Int {
        if (!shouldReduceFrameRate()) return originalFps

        return when {
            _powerSaveMode.value -> 15 // Very low frame rate in power save mode
            _batteryLevel.value < 15 -> 10 // Critical battery
            else -> 20 // Reduced frame rate
        }.coerceAtMost(originalFps)
    }

    fun shouldEnableIdleOptimization(): Boolean {
        return !_isCharging.value && (_powerSaveMode.value || _batteryLevel.value < 40)
    }

    fun cleanup() {
        try {
            context.unregisterReceiver(batteryReceiver)
        } catch (e: Exception) {
            // Receiver might not be registered
        }
    }
}