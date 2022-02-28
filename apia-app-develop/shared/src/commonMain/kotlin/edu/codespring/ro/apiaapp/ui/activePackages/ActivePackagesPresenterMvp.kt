package edu.codespring.ro.apiaapp.ui.activePackages

import edu.codespring.ro.apiaapp.data.model.PackageData
import edu.codespring.ro.apiaapp.ui.packagePlanner.PackagePlannerView

interface ActivePackagesPresenterMvp {
    fun attachView(packageView: ActivePackagesView)
    fun detachView()
    fun openDetailedPackage(it: Int)
    fun updateActivePackagesList()
    fun loadActivePackagesList()
}
