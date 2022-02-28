package edu.codespring.ro.apiaapp.ui.packages

import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.data.model.PackageData

interface PackageView {
    fun setPackagesList(packageList: List<PackageData>)
    fun navigateToDetails(it:Int)
    fun setUpLoadingScreen()
    fun setUpErrorScreen(error: ApiErrors)
}
