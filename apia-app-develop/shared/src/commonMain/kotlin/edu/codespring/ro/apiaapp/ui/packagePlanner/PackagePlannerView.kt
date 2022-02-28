package edu.codespring.ro.apiaapp.ui.packagePlanner

import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.constants.Errors
import edu.codespring.ro.apiaapp.data.model.ActivePackageData
import edu.codespring.ro.apiaapp.data.model.PackageData
import edu.codespring.ro.apiaapp.data.model.PlannedPackageData

interface PackagePlannerView {
    fun navigateToDetails(id: Int)
    fun deletePlannedPackage(id: Int)
    fun addToActivePackages(id: Int)
    fun setPlannedPackagesList(packageList: List<PlannedPackageData>)
    fun successToastMessage()
    fun errorToast(error: Errors)

    fun setUpLoadingScreen()
    fun setUpErrorScreen(error: ApiErrors)
    }
