package edu.codespring.ro.apiaapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Suppress("UnnecessaryAbstractClass")
abstract class SelectionData {
    abstract val name: String
    abstract var isSelected: Boolean
}

@Serializable
data class County (
    val id:Int,
    override val name:String,
    val code:String,
    val townships: List<Township>,
    override var isSelected: Boolean = false) : SelectionData()

@Serializable
data class Township(
    val id: Long,
    override val name: String,
    val code: String,
    @SerialName("packages")
    var packages: List<PackageData>?=null,
    override var isSelected: Boolean = false) : SelectionData()
