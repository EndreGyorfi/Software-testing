package edu.codespring.ro.apiaapp.data.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
class PlannedPackageData(
    val id: Long,
    @SerialName("package")
    val packageData: PackageData,
    var fields: List<Field>,
    var addedFieldsString: String? = "",
    var addedFieldsSize: Float? = 0F
)
