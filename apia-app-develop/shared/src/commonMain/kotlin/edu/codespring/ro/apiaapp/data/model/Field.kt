package edu.codespring.ro.apiaapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Field(
    val id: Int,
    val name: String,
    val size: Float,
    var checked: Boolean? = false)
