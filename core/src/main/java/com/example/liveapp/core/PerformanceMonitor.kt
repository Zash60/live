package com.example.liveapp.core

import android.os.Debug
import android.os.SystemClock
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PerformanceMonitor @Inject constructor() {

    private val _performanceMetrics = MutableStateFlow(PerformanceMetrics())
    val performanceMetrics: StateFlow<PerformanceMetrics> = _performanceMetrics.asStateFlow()

    private var startTime = 0L
    private var frameCount = 0
    private var lastFrameTime = 0L

    data class PerformanceMetrics(
        val fps: Double = 0.0,
        val memoryUsage: Long = 0L,
        val cpuUsage: Double = 0.0,
        val networkLatency: Long = 0L,
        val batteryDrainRate: Double = 0.0,
        val uptime: Long = 0L
    )

    fun startMonitoring() {
        startTime = SystemClock.elapsedRealtime()
        lastFrameTime = startTime
        updateMetrics()
    }

    fun stopMonitoring() {
        _performanceMetrics.value = PerformanceMetrics()
    }

    fun recordFrame() {
        frameCount++
        val currentTime = SystemClock.elapsedRealtime()
        val timeDiff = currentTime - lastFrameTime

        if (timeDiff >= 1000) { // Update FPS every second
            val fps = frameCount.toDouble() / (timeDiff / 1000.0)
            updateMetrics(fps = fps)
            frameCount = 0
            lastFrameTime = currentTime
        }
    }

    fun recordNetworkLatency(latencyMs: Long) {
        updateMetrics(networkLatency = latencyMs)
    }

    private fun updateMetrics(
        fps: Double? = null,
        networkLatency: Long? = null
    ) {
        val runtime = Runtime.getRuntime()
        val usedMemory = runtime.totalMemory() - runtime.freeMemory()
        val uptime = SystemClock.elapsedRealtime() - startTime

        // Simple CPU usage estimation (in a real app, you'd use more sophisticated methods)
        val cpuUsage = estimateCpuUsage()

        val currentMetrics = _performanceMetrics.value
        _performanceMetrics.value = currentMetrics.copy(
            fps = fps ?: currentMetrics.fps,
            memoryUsage = usedMemory,
            cpuUsage = cpuUsage,
            networkLatency = networkLatency ?: currentMetrics.networkLatency,
            uptime = uptime
        )

        // Log performance metrics for debugging
        Log.d(TAG, "Performance: FPS=${currentMetrics.fps}, Memory=${usedMemory / 1024}KB, CPU=${cpuUsage}%, Latency=${currentMetrics.networkLatency}ms")
    }

    private fun estimateCpuUsage(): Double {
        // This is a simplified estimation. In production, you'd use Android's ProcessStats or similar
        val threadCount = Thread.activeCount()
        return (threadCount * 2.0).coerceAtMost(100.0) // Rough estimation
    }

    fun logPerformanceEvent(event: String, duration: Long? = null) {
        val message = if (duration != null) {
            "$event took ${duration}ms"
        } else {
            event
        }
        Log.i(TAG, message)
    }

    fun startTiming(label: String): PerformanceTimer {
        return PerformanceTimer(label, this)
    }

    class PerformanceTimer(
        private val label: String,
        private val monitor: PerformanceMonitor
    ) {
        private val startTime = SystemClock.elapsedRealtimeNanos()

        fun end() {
            val durationMs = (SystemClock.elapsedRealtimeNanos() - startTime) / 1_000_000
            monitor.logPerformanceEvent("$label completed", durationMs)
        }
    }

    companion object {
        private const val TAG = "PerformanceMonitor"
    }
}