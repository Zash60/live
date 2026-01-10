package com.example.liveapp.core

import android.content.Context
import coil.ImageLoader
import coil.decode.SvgDecoder
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.size.Precision
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CoilImageLoader @Inject constructor(
    @ApplicationContext private val context: Context
) {

    val imageLoader = ImageLoader.Builder(context)
        .memoryCache {
            MemoryCache.Builder(context)
                .maxSizePercent(0.25) // Use 25% of available memory
                .build()
        }
        .diskCache {
            DiskCache.Builder()
                .directory(context.cacheDir.resolve("image_cache"))
                .maxSizePercent(0.02) // Use 2% of available storage
                .build()
        }
        .networkCachePolicy(CachePolicy.ENABLED)
        .memoryCachePolicy(CachePolicy.ENABLED)
        .diskCachePolicy(CachePolicy.ENABLED)
        .allowRgb565(true) // Allow lower quality for better performance
        .crossfade(true)
        .components {
            add(SvgDecoder.Factory())
        }
        .build()

    fun createOptimizedRequest(
        data: Any,
        size: coil.size.Size? = null,
        allowHardware: Boolean = true
    ): ImageRequest {
        return ImageRequest.Builder(context)
            .data(data)
            .apply {
                size?.let { size(it) }
                if (size != null) {
                    precision(Precision.INEXACT)
                }
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
                networkCachePolicy(CachePolicy.ENABLED)
                allowHardware(allowHardware)
                // Disable crossfade for better performance in lists
                crossfade(false)
            }
            .build()
    }

    fun createStreamingOptimizedRequest(
        data: Any,
        targetWidth: Int? = null,
        targetHeight: Int? = null
    ): ImageRequest {
        return ImageRequest.Builder(context)
            .data(data)
            .apply {
                if (targetWidth != null && targetHeight != null) {
                    size(targetWidth, targetHeight)
                    precision(Precision.INEXACT)
                }
                // For streaming, prioritize memory cache and allow hardware bitmaps
                memoryCachePolicy(CachePolicy.ENABLED)
                diskCachePolicy(CachePolicy.ENABLED)
                networkCachePolicy(CachePolicy.ENABLED)
                allowHardware(true)
                // Use lower quality for faster loading
                allowRgb565(true)
            }
            .build()
    }

    fun preloadImage(data: Any) {
        val request = ImageRequest.Builder(context)
            .data(data)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.ENABLED)
            .networkCachePolicy(CachePolicy.ENABLED)
            .build()

        imageLoader.enqueue(request)
    }

    fun clearMemoryCache() {
        imageLoader.memoryCache?.clear()
    }

    fun clearDiskCache() {
        imageLoader.diskCache?.clear()
    }
}