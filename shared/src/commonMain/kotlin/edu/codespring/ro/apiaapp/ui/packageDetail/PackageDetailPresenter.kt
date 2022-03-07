package edu.codespring.ro.apiaapp.ui.packageDetail

import edu.codespring.ro.apiaapp.data.model.PackageData
import edu.codespring.ro.apiaapp.data.repository.DataRepository
import edu.codespring.ro.apiaapp.runCatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PackageDetailPresenter : PackageDetailPresenterMvp {

    private var packageDetailView: PackageDetailView? = null

    private val dataRepository = DataRepository()

    var packageData: PackageData? = null

    private var job: Job? = null

    override fun attachView(detailsView: PackageDetailView) {
        this.packageDetailView = detailsView
    }

    override fun detachView() {
        this.packageDetailView = null
        job?.cancel()
    }

    override fun loadListElementById(id: Int) {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    packageDetailView?.setUpLoadingScreen()
                    dataRepository.getPackageById(id)
                },
                success = {
                    packageData = it
                    packageDetailView?.loadPackage(packageData!!)
                },
                error = {
                    packageDetailView?.setUpErrorScreen(it)
                }
            )
        }
    }

    override fun addPackageToPlanner(id: Long) {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    dataRepository.addPlannedPackage(id)
                },
                success = {
                    packageDetailView?.successToastMessage()
                    packageDetailView?.buttonNavigateToPlannerEnabled()
                },
                error = {
                    packageDetailView?.errorToastMessage(it)
                }
            )
        }
    }

    override fun checkPackageInPlannerAlready(id: Long) {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    dataRepository.checkPackageInPlanner(id)
                },
                success = {
                    // Already in planner (we got result from API)
                    packageDetailView?.buttonNavigateToPlannerEnabled()
                },
                error = {
                    packageDetailView?.buttonAddToPlannerEnabled(id)
                }
            )
        }
    }

    override fun checkPackageInActive(id: Long) {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    dataRepository.checkPackageInActive(id)
                },
                success = {
                    // Already in active packages (we got result from API)
                    packageDetailView?.buttonNavigateToActiveEnabled()
                },
                error = {
                    packageDetailView?.buttonAddToPlannerEnabled(id)
                }
            )
        }
    }
}
