package com.example.liveapp.domain.model

/**
 * Represents a user in the LiveApp system.
 *
 * This data class encapsulates user information including basic profile details
 * and YouTube channel information for content creators.
 *
 * @property id Unique identifier for the user, typically from authentication provider
 * @property name Display name of the user
 * @property email User's email address, used for authentication and notifications
 * @property profilePictureUrl URL to the user's profile picture, nullable if not available
 * @property channelId YouTube channel ID associated with this user, null for non-creators
 * @property channelTitle YouTube channel title, null for non-creators
 * @property subscriberCount Number of subscribers to the user's YouTube channel, null for non-creators
 */
data class User(
    val id: String,
    val name: String,
    val email: String,
    val profilePictureUrl: String?,
    val channelId: String?,
    val channelTitle: String?,
    val subscriberCount: Long?
)