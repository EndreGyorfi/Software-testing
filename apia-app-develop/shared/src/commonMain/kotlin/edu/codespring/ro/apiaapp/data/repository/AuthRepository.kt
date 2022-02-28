package edu.codespring.ro.apiaapp.data.repository

import edu.codespring.ro.apiaapp.data.model.AuthData
import edu.codespring.ro.apiaapp.data.network.AuthApi

class AuthRepository {

    private val authApi = AuthApi()

    suspend fun authUserBackend(accessToken: String): AuthData {
        return authApi.authUserBackend(accessToken)
    }

    suspend fun checkUserProfileExists(email: String): Boolean {
        return authApi.checkUserProfileExists(email)
    }

    suspend fun createUserProfile(name: String, userId: Int) {
        return authApi.createUserProfile(name, userId)
    }
}
