package com.example.liveapp.data.datasource

import android.content.Context
import com.example.liveapp.domain.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.youtube.YouTube
import com.google.api.services.youtube.model.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val context: Context
) : UserDataSource {

    private val googleSignInClient: GoogleSignInClient by lazy {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestProfile()
            .requestScopes(com.google.api.services.youtube.YouTubeScopes.YOUTUBE_READONLY)
            .build()
        GoogleSignIn.getClient(context, gso)
    }

    private val youTube: YouTube by lazy {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        val credential = GoogleAccountCredential.usingOAuth2(
            context,
            listOf(com.google.api.services.youtube.YouTubeScopes.YOUTUBE_READONLY)
        ).apply {
            selectedAccount = account?.account
        }
        YouTube.Builder(NetHttpTransport(), GsonFactory(), credential)
            .setApplicationName("LiveApp")
            .build()
    }

    override fun signInWithGoogle(): Flow<Result<GoogleSignInAccount>> = flow {
        try {
            val account = GoogleSignIn.getLastSignedInAccount(context)
            if (account != null) {
                emit(Result.success(account))
            } else {
                // For actual sign-in, this would need to be handled in Activity/ViewModel
                // Here we assume it's already signed in or handle differently
                emit(Result.failure(Exception("Not signed in")))
            }
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun getYouTubeChannelInfo(account: GoogleSignInAccount): Flow<Result<User>> = flow {
        try {
            val channelsResponse = youTube.channels()
                .list(listOf("snippet", "statistics"))
                .setMine(true)
                .execute()

            val channel: Channel? = channelsResponse.items.firstOrNull()
            val user = User(
                id = account.id ?: "",
                name = account.displayName ?: "",
                email = account.email ?: "",
                profilePictureUrl = account.photoUrl?.toString(),
                channelId = channel?.id,
                channelTitle = channel?.snippet?.title,
                subscriberCount = channel?.statistics?.subscriberCount
            )
            emit(Result.success(user))
        } catch (e: Exception) {
            emit(Result.failure(e))
        }
    }

    override fun logout() {
        googleSignInClient.signOut()
    }

    fun getSignInIntent() = googleSignInClient.signInIntent
}