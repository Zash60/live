package com.example.liveapp.features.streaming.domain.model

sealed class YouTubeApiException(message: String, cause: Throwable? = null) : Exception(message, cause) {

    class AuthenticationException(message: String = "YouTube authentication failed", cause: Throwable? = null)
        : YouTubeApiException(message, cause)

    class ApiLimitExceededException(message: String = "YouTube API quota exceeded", cause: Throwable? = null)
        : YouTubeApiException(message, cause)

    class NetworkException(message: String = "Network error occurred", cause: Throwable? = null)
        : YouTubeApiException(message, cause)

    class InvalidRequestException(message: String = "Invalid request to YouTube API", cause: Throwable? = null)
        : YouTubeApiException(message, cause)

    class UnknownException(message: String = "Unknown YouTube API error", cause: Throwable? = null)
        : YouTubeApiException(message, cause)
}