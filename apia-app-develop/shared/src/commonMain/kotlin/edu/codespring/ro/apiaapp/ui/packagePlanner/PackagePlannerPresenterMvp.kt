package edu.codespring.ro.apiaapp.ui.packagePlanner

import edu.codespring.ro.apiaapp.data.model.ActivePackageData

interface PackagePlannerPresenterMvp {
    fun attachView(packageView: PackagePlannerView)
    fun detachView()
    fun openDetailedPackage(id: Int)
    fun updatePlannedPackagesList()
    fun addToActivePackages(id: Long)
    fun getSumOfPlannedPackages(): Long
    fun loadPlannedPackagesList()
    fun deletePlannedPackage(id: Long)
}
