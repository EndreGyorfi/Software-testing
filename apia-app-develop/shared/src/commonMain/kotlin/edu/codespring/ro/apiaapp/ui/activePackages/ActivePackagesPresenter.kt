package edu.codespring.ro.apiaapp.ui.activePackages

import edu.codespring.ro.apiaapp.data.model.ActivePackageData
import edu.codespring.ro.apiaapp.data.repository.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class ActivePackagesPresenter : ActivePackagesPresenterMvp {

    private var job: Job? = null
    private var activePackagesView: ActivePackagesView? = null
    private var activeList: MutableList<ActivePackageData>? = null
    private val dataRepository = DataRepository()

    override fun attachView(packageView: ActivePackagesView) {
        activePackagesView = packageView
    }

    override fun detachView() {
        activePackagesView = null
        job?.cancel()
    }

    override fun openDetailedPackage(it: Int) {
        activePackagesView?.navigateToDetails(it)
    }

    override fun updateActivePackagesList() {
        val newList: MutableList<ActivePackageData>? =
            activeList?.toMutableList()

        newList?.let {
            activePackagesView?.setActivePackagesList(newList)
        }
    }

    override fun loadActivePackagesList() {
        job = CoroutineScope(Dispatchers.Main).launch {
            edu.codespring.ro.apiaapp.runCatching(
                block = {
                    activePackagesView?.setUpLoadingScreen()
                    dataRepository.getActivePackages().toMutableList()
                },
                success = {
                    activeList = it
                    updateActivePackagesList()
                },
                error = {
                    activePackagesView?.setUpErrorScreen(it)
                }
            )
        }
    }
}
