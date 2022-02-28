@file:Suppress("WildcardImport")
package edu.codespring.ro.apiaapp.data.network

import edu.codespring.ro.apiaapp.auth.AuthManager
import edu.codespring.ro.apiaapp.data.model.AuthData
import edu.codespring.ro.apiaapp.data.model.UserProfile
import edu.codespring.ro.apiaapp.isJsonResultEmpty
import io.ktor.client.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import io.ktor.client.request.*
import io.ktor.http.*

class AuthApi {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:3001/"
        private const val AUTH0_ENDPOINT = BASE_URL + "auth/auth0/callback?access_token="

        private const val PROFILE_ENDPOINT = BASE_URL + "profiles/"
        private const val PROFILE_EXIST_ENDPOINT = "$PROFILE_ENDPOINT?users_permissions_user.email="
    }

    private val httpClient = HttpClient {
        install(Logging) {
            logger = Logger.DEFAULT
            level = LogLevel.INFO
        }
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
    }

    suspend fun authUserBackend(accessToken: String): AuthData {
        return httpClient.get(AUTH0_ENDPOINT + accessToken)
    }

    suspend fun checkUserProfileExists(email: String): Boolean {
        val byteArray = httpClient.request<ByteArray> {
            url(PROFILE_EXIST_ENDPOINT + email)
            method = HttpMethod.Get

            headers {
                append("Authorization", "Bearer ${AuthManager.getUserJwt()}")
            }
        }

        return !byteArray.isJsonResultEmpty()
    }

    suspend fun createUserProfile(name: String, userId: Int) {
        return httpClient.post(PROFILE_ENDPOINT) {
            headers {
                append("Authorization", "Bearer ${AuthManager.getUserJwt()}")
            }

            contentType(ContentType.Application.Json)
            body = UserProfile(name, listOf(userId))
        }
    }
}
