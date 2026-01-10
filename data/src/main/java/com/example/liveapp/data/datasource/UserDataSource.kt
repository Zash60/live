package com.example.liveapp.data.datasource

import com.example.liveapp.domain.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    fun signInWithGoogle(): Flow<Result<GoogleSignInAccount>>
    fun getYouTubeChannelInfo(account: GoogleSignInAccount): Flow<Result<User>>
    fun logout()
}