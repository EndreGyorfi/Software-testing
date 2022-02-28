package edu.codespring.ro.apiaapp.ui.packagePlanner

import edu.codespring.ro.apiaapp.data.model.ActivePackageData
import edu.codespring.ro.apiaapp.data.model.PackageData
import edu.codespring.ro.apiaapp.data.model.PlannedPackageData
import edu.codespring.ro.apiaapp.data.repository.DataRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import edu.codespring.ro.apiaapp.runCatching

class PackagePlannerPresenter : PackagePlannerPresenterMvp {

    private var job: Job? = null
    private var packagePlannerView: PackagePlannerView? = null
    private var plannedList: MutableList<PlannedPackageData>? = null
    private val dataRepository = DataRepository()

    override fun attachView(packageView: PackagePlannerView) {
        packagePlannerView = packageView
    }

    override fun detachView() {
        packagePlannerView = null
        job?.cancel()
    }

    override fun openDetailedPackage(id: Int) {
        packagePlannerView?.navigateToDetails(id)
    }

    override fun deletePlannedPackage(id: Long) {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    packagePlannerView?.setUpLoadingScreen()
                    dataRepository.deletePlannedPackage(id)
                },
                success = {
                    plannedList?.removeAll { it.id == id }
                    updatePlannedPackagesList()
                },
                error = {
                    packagePlannerView?.setUpErrorScreen(it)
                }
            )
        }
    }

    override fun updatePlannedPackagesList() {
        val newList: MutableList<PlannedPackageData>? =
            plannedList?.toMutableList()

        newList?.let {
            setAddedFieldsStringAndSize(newList)
            packagePlannerView?.setPlannedPackagesList(newList)
        }
    }

    override fun getSumOfPlannedPackages(): Long {
        // Return sum of packages value, or 0 if there is no package
        return plannedList?.map { it.packageData.value }?.sum() ?: 0
    }

    override fun loadPlannedPackagesList() {
        if (plannedList == null) {
            job = CoroutineScope(Dispatchers.Main).launch {
                runCatching(
                    block = {
                        packagePlannerView?.setUpLoadingScreen()
                        dataRepository.getPlannedPackages().toMutableList()
                    },
                    success = {
                        plannedList = it
                        updatePlannedPackagesList()
                    },
                    error = {
                        packagePlannerView?.setUpErrorScreen(it)
                    }
                )
            }
        }
    }

    override fun addToActivePackages(id: Long) {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    dataRepository.addActivePackage(id)
                    dataRepository.deletePlannedPackage(id)
                },
                success = {
                    plannedList?.removeAll { it.id == id }
                    updatePlannedPackagesList()
                    packagePlannerView?.successToastMessage()
                },
                error = {
                    packagePlannerView?.setUpErrorScreen(it)
                }
            )
        }
    }

    private fun setAddedFieldsStringAndSize(list: MutableList<PlannedPackageData>?){
        list?.forEach { plannedData ->
            plannedData.fields.forEach {
                plannedData.addedFieldsString += it.name
                plannedData.addedFieldsString += ", "
                plannedData.addedFieldsSize = plannedData.addedFieldsSize?.plus(it.size)
            }
            plannedData.addedFieldsString = plannedData.addedFieldsString?.dropLast(2)
        }
    }
}
