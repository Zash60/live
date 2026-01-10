package com.example.liveapp.core

import kotlinx.coroutines.delay
import kotlinx.coroutines.withTimeout
import okhttp3.ConnectionPool
import okhttp3.OkHttpClient
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.pow

@Singleton
class NetworkOptimizer @Inject constructor() {

    // Optimized OkHttpClient with connection pooling
    val optimizedHttpClient = OkHttpClient.Builder()
        .connectionPool(ConnectionPool(
            maxIdleConnections = 10,
            keepAliveDuration = 30,
            timeUnit = TimeUnit.SECONDS
        ))
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .retryOnConnectionFailure(true)
        .build()

    // Retry logic with exponential backoff
    suspend fun <T> executeWithRetry(
        maxRetries: Int = 3,
        initialDelayMs: Long = 1000,
        maxDelayMs: Long = 30000,
        timeoutMs: Long = 30000,
        block: suspend () -> T
    ): Result<T> {
        var currentDelay = initialDelayMs
        var lastException: Exception? = null

        for (attempt in 0..maxRetries) {
            try {
                val result = withTimeout(timeoutMs) {
                    block()
                }
                return Result.success(result)
            } catch (e: Exception) {
                lastException = e

                if (attempt == maxRetries) {
                    break // Don't delay after last attempt
                }

                // Exponential backoff with jitter
                val jitter = (Math.random() * 0.1 * currentDelay).toLong()
                val delayWithJitter = currentDelay + jitter

                delay(delayWithJitter.coerceAtMost(maxDelayMs))

                // Increase delay for next attempt (exponential backoff)
                currentDelay = (currentDelay * 2.0.pow(attempt.toDouble())).toLong()
                    .coerceAtMost(maxDelayMs)
            }
        }

        return Result.failure(lastException ?: Exception("Unknown error"))
    }

    // Streaming-specific retry logic with shorter timeouts
    suspend fun <T> executeStreamingWithRetry(
        maxRetries: Int = 2,
        block: suspend () -> T
    ): Result<T> {
        return executeWithRetry(
            maxRetries = maxRetries,
            initialDelayMs = 500, // Faster initial retry for streaming
            maxDelayMs = 5000,    // Shorter max delay
            timeoutMs = 10000,    // Shorter timeout for streaming
            block = block
        )
    }

    // Connection health check
    suspend fun checkConnectionHealth(url: String): Boolean {
        return executeWithRetry(
            maxRetries = 1,
            initialDelayMs = 100,
            timeoutMs = 5000
        ) {
            // Simple connectivity check - in real implementation, you'd make a lightweight request
            // For now, just simulate
            true
        }.isSuccess
    }

    // Adaptive timeout based on network conditions
    fun getAdaptiveTimeout(isSlowNetwork: Boolean): Long {
        return if (isSlowNetwork) 45000L else 30000L // 45s for slow, 30s for normal
    }

    // Connection pooling stats
    fun getConnectionPoolStats(): String {
        val pool = optimizedHttpClient.connectionPool
        return "Idle: ${pool.idleConnectionCount()}, Total: ${pool.connectionCount()}"
    }
}