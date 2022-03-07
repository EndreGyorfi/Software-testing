package edu.codespring.ro.apiaapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class ActivePackageData(
    val id: Long,
    @SerialName("package")
    val packageData: PackageData,
    val fields: List<Field>
)
