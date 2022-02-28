package edu.codespring.ro.apiaapp.ui.packageDetail

import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.data.model.PackageData

interface PackageDetailView {
    fun loadPackage(detailsData: PackageData)

    fun setUpLoadingScreen()
    fun setUpErrorScreen(error: ApiErrors)
    fun successToastMessage()
    fun errorToastMessage(error: ApiErrors)

    fun buttonAddToPlannerEnabled(id : Long)
    fun buttonNavigateToPlannerEnabled()
    fun buttonNavigateToActiveEnabled()
}
