package edu.codespring.ro.apiaapp.ui.packages

import edu.codespring.ro.apiaapp.constants.SPrefConstants.TOWNSHIP_PREFERENCE
import edu.codespring.ro.apiaapp.data.model.PackageData
import edu.codespring.ro.apiaapp.data.repository.DataRepository
import edu.codespring.ro.apiaapp.runCatching
import edu.codespring.ro.apiaapp.spref.KMMStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class PackagePresenter : PackagePresenterMvp {

    private var job: Job? = null
    private var packageView: PackageView? = null
    private var packageList: MutableList<PackageData>? = null
    private val dataRepository = DataRepository()

    override fun attachView(packageView: PackageView) {
        this.packageView = packageView
    }

    override fun detachView() {
        packageView = null
        job?.cancel()
    }

    override fun openDetailedPackage(it: Int) {
        packageView?.navigateToDetails(it)
    }

    override fun updatePackagesList(filterName: String?) {
        val newList: MutableList<PackageData>? = when (filterName) {
            null -> packageList?.toMutableList()
            else -> packageList?.filter { it.name.lowercase().contains(filterName.lowercase()) }?.toMutableList()
        }

        newList?.let {
            packageView?.setPackagesList(newList)
        }
    }

    override fun loadPackagesList() {
        if (packageList == null) {
            job = CoroutineScope(Dispatchers.Main).launch {
                runCatching(
                    block = {
                        packageView?.setUpLoadingScreen()
                        dataRepository.getPackagesByTownshipIdList(
                            KMMStorage.getStringSet(TOWNSHIP_PREFERENCE)?.map { it.toInt() }?.toList()
                        )?.toMutableList()
                    },
                    success = {
                        packageList = it
                        updatePackagesList()
                    },
                    error = {
                        packageView?.setUpErrorScreen(it)
                    }
                )
            }
        }
    }
}
