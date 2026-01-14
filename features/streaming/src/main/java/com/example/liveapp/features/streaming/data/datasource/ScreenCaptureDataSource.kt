package com.example.liveapp.features.streaming.data.datasource

import android.content.Context
import android.graphics.Bitmap
import android.hardware.display.DisplayManager
import android.hardware.display.VirtualDisplay
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.os.Handler
import android.os.HandlerThread
import android.util.LruCache
import android.view.Surface
import com.example.liveapp.domain.model.Resolution
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.suspendCancellableCoroutine
import java.lang.ref.WeakReference
import javax.inject.Inject
import kotlin.coroutines.resume

class ScreenCaptureDataSource @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var mediaProjection: MediaProjection? = null
    private var virtualDisplay: VirtualDisplay? = null
    private var mediaRecorder: MediaRecorder? = null
    private var handlerThread: HandlerThread? = null
    private var handler: Handler? = null

    // Bitmap pool for memory management
    private val bitmapPool = object : LruCache<String, WeakReference<Bitmap>>(10) { // Max 10 bitmaps
        override fun sizeOf(key: String, value: WeakReference<Bitmap>): Int = 1
    }

    // Callback for MediaProjection cleanup
    private val mediaProjectionCallback = object : MediaProjection.Callback() {
        override fun onStop() {
            cleanupResources()
        }
    }

    fun initialize(mediaProjection: MediaProjection) {
        this.mediaProjection = mediaProjection.apply {
            registerCallback(mediaProjectionCallback, handler)
        }
        handlerThread = HandlerThread("ScreenCapture").apply {
            priority = Thread.NORM_PRIORITY
            start()
        }
        handler = Handler(handlerThread!!.looper)
    }

    suspend fun startScreenCapture(resolution: Resolution, outputPath: String): Surface? {
        return suspendCancellableCoroutine { continuation ->
            handler?.post {
                try {
                    mediaRecorder = MediaRecorder().apply {
                        setVideoSource(MediaRecorder.VideoSource.SURFACE)
                        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
                        setVideoSize(resolution.width, resolution.height)
                        setVideoFrameRate(30)
                        setVideoEncodingBitRate(2000000)
                        setOutputFile(outputPath)
                        prepare()
                    }

                    val surface = mediaRecorder?.surface
                    virtualDisplay = mediaProjection?.createVirtualDisplay(
                        "ScreenCapture",
                        resolution.width,
                        resolution.height,
                        context.resources.displayMetrics.densityDpi,
                        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                        surface,
                        null,
                        null
                    )

                    mediaRecorder?.start()
                    continuation.resume(surface)
                } catch (e: Exception) {
                    continuation.resume(null)
                }
            }
        }
    }

    fun stopScreenCapture() {
        handler?.post {
            cleanupResources()
        }
    }

    private fun cleanupResources() {
        try {
            mediaRecorder?.stop()
        } catch (e: Exception) {
            // MediaRecorder might not be started or already stopped
        }

        // Release resources in reverse order of creation
        mediaRecorder?.release()
        virtualDisplay?.release()
        mediaProjection?.unregisterCallback(mediaProjectionCallback)
        mediaProjection?.stop()

        // Clear bitmap pool
        bitmapPool.evictAll()

        // Clear references
        mediaRecorder = null
        virtualDisplay = null
        mediaProjection = null
    }

    fun release() {
        handler?.post {
            cleanupResources()
        }
        handlerThread?.quitSafely()
        handler = null
        bitmapPool.evictAll()
    }

    // Bitmap pool management methods
    fun getBitmapFromPool(key: String): Bitmap? {
        return bitmapPool.get(key)?.get()?.also {
            // Remove from pool as it's being used
            bitmapPool.remove(key)
        }
    }

    fun returnBitmapToPool(key: String, bitmap: Bitmap) {
        if (!bitmap.isRecycled) {
            bitmapPool.put(key, WeakReference(bitmap))
        }
    }

    fun clearBitmapPool() {
        bitmapPool.evictAll()
    }

    // Memory monitoring
    fun getMemoryUsage(): Long {
        val runtime = Runtime.getRuntime()
        return runtime.totalMemory() - runtime.freeMemory()
    }
}
