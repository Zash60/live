package com.example.liveapp.features.streaming.data.datasource

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential
import com.google.api.services.youtube.YouTubeScopes
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class YouTubeAuthManager @Inject constructor(
    private val context: Context
) {

    private val scopes = listOf(YouTubeScopes.YOUTUBE_FORCE_SSL)

    fun getGoogleSignInOptions(): GoogleSignInOptions {
        return GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestScopes(com.google.android.gms.common.api.Scope(YouTubeScopes.YOUTUBE_FORCE_SSL))
            .build()
    }

    fun getCredential(): GoogleAccountCredential? {
        val account = GoogleSignIn.getLastSignedInAccount(context)
        return account?.let {
            GoogleAccountCredential.usingOAuth2(context, scopes).apply {
                selectedAccount = it.account
            }
        }
    }

    fun isSignedIn(): Boolean {
        return GoogleSignIn.getLastSignedInAccount(context) != null
    }
}