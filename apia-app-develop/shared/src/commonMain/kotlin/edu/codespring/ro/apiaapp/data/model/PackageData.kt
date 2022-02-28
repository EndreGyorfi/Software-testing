package edu.codespring.ro.apiaapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PackageData(
    val id: Long,
    val name: String,
    val description: String,
    val category: String,
    @SerialName("relevant_years")
    val relevantYears: String,
    val range: String,
    var value: Long,
    val requirements: List<String>,
    val rules: List<String>? = null,
    var townships: List<Township>? = null,
    @SerialName("required_documents")
    var requiredDocuments: List<CheckableCondition>,
    var preconditions: List<CheckableCondition>,
    @SerialName("post_production")
    var postProduction: String,
    @SerialName("support_category")
    var supportCategory: String,
    var deadline: String
)

@Serializable
data class CheckableCondition(
    override val name:String,
    override var isSelected: Boolean = false) : SelectionData()

