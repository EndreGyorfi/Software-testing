package edu.codespring.ro.apiaapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

// Response
@Serializable
data class BackendUserData(
    val id: Int?
)

@Serializable
data class AuthData(
    val jwt: String,
    val user: BackendUserData
)

@Serializable
data class UserData(
    val name: String?,
    val email: String?,
    val pictureURL: String?
)

// Request
@Serializable
data class UserProfile(
    val name: String,
    @SerialName("users_permissions_user")
    val userIdList: List<Int>
)
