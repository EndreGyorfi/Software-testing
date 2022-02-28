package edu.codespring.ro.apiaapp.ui.activePackages

import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.constants.Errors
import edu.codespring.ro.apiaapp.data.model.ActivePackageData

interface ActivePackagesView {
    fun navigateToDetails(it:Int)
    fun setActivePackagesList(activePackageList: List<ActivePackageData>)
    fun errorToast(error: Errors)

    fun setUpLoadingScreen()
    fun setUpErrorScreen(error: ApiErrors)
}
