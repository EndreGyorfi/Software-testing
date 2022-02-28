package edu.codespring.ro.apiaapp.ui.packages

interface PackagePresenterMvp {
    fun attachView(packageView: PackageView)
    fun detachView()

    // Selection listener which opens detailed view of the package
    fun openDetailedPackage(it: Int)

    // Update only selection list for RecyclerView without calling API / Database query
    fun updatePackagesList(filterName: String? = null)

    // Call API / Database query to load list again
    fun loadPackagesList()
}
