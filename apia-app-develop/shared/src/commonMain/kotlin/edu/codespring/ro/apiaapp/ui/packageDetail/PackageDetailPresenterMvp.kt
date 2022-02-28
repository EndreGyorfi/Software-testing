package edu.codespring.ro.apiaapp.ui.packageDetail

interface PackageDetailPresenterMvp {
    fun attachView(detailsView: PackageDetailView)
    fun detachView()

    fun loadListElementById(id: Int)

    fun addPackageToPlanner(id: Long)
    fun checkPackageInPlannerAlready(id: Long)
    fun checkPackageInActive(id: Long)
}
