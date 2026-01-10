package com.example.liveapp.data.repository

import com.example.liveapp.domain.model.User
import com.example.liveapp.domain.repository.UserRepository
import com.example.liveapp.data.datasource.UserDataSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDataSource: UserDataSource
) : UserRepository {

    override fun login(): Flow<Result<User>> = userDataSource.signInWithGoogle()
        .flatMapConcat { accountResult ->
            accountResult.fold(
                onSuccess = { account ->
                    userDataSource.getYouTubeChannelInfo(account)
                },
                onFailure = { error ->
                    flow { emit(Result.failure(error)) }
                }
            )
        }

    override fun getProfile(): Flow<Result<User>> = userDataSource.signInWithGoogle()
        .flatMapConcat { accountResult ->
            accountResult.fold(
                onSuccess = { account ->
                    userDataSource.getYouTubeChannelInfo(account)
                },
                onFailure = { error ->
                    flow { emit(Result.failure(error)) }
                }
            )
        }

    override fun logout() {
        userDataSource.logout()
    }
}