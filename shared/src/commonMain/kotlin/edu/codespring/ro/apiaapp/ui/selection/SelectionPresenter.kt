package edu.codespring.ro.apiaapp.ui.selection

import edu.codespring.ro.apiaapp.constants.SPrefConstants.COUNTY_PREFERENCE
import edu.codespring.ro.apiaapp.constants.SPrefConstants.TOWNSHIP_PREFERENCE
import edu.codespring.ro.apiaapp.data.model.County
import edu.codespring.ro.apiaapp.data.model.Township
import edu.codespring.ro.apiaapp.data.repository.DataRepository
import edu.codespring.ro.apiaapp.spref.KMMStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import edu.codespring.ro.apiaapp.runCatching

class SelectionPresenter: SelectionPresenterMvp {
    private var selectionView: SelectionView? = null

    private val dataRepository = DataRepository()

    private var countyItems: MutableList<County>? = null
    private var townshipItems: MutableList<Township>? = null

    private var job: Job? = null

    override fun attachView(selectionView: SelectionView) {
        this.selectionView = selectionView
    }

    override fun detachView() {
        selectionView = null
        job?.cancel()
    }

    override fun onItemSelected(it: Int, currentPage: Int) {
        if (currentPage == 0) {
            countyItems?.set(it, countyItems!![it].copy(isSelected = !countyItems!![it].isSelected))
            updateCountySelection()
        }
        else {
            townshipItems?.set(it, townshipItems!![it].copy(isSelected = !townshipItems!![it].isSelected))
            updateTownshipSelection()
        }
    }

    override fun validateSelection(currentPage: Int): Boolean {
        return when (currentPage) {
            0 -> countyItems?.filter { it.isSelected }?.count() != 0
            1 -> townshipItems?.filter { it.isSelected }?.count() != 0
            else -> true
        }
    }

    override fun updateCountySelection(filterName: String?) {
        val newList: MutableList<County>? = when (filterName) {
            null -> countyItems?.toMutableList()
            else -> countyItems?.filter { it.name.lowercase().contains(filterName.lowercase()) }?.toMutableList()
        }

        if (newList != null)
            selectionView?.setCountiesList(newList)
    }

    override fun updateTownshipSelection(filterName: String?) {
        val newList: MutableList<Township>? = when (filterName) {
            null -> townshipItems?.toMutableList()
            else -> townshipItems?.filter { it.name.lowercase().contains(filterName.lowercase()) }?.toMutableList()
        }

        if (newList != null)
            selectionView?.setTownshipsList(newList)
    }

    override fun loadCountiesList() {
        if (countyItems == null) {
            job = CoroutineScope(Dispatchers.Main).launch {
                runCatching(
                    block = {
                        selectionView?.setUpLoadingScreen()
                        dataRepository.getAllCounties().sortedBy { it.name }.toMutableList()
                    },
                    success = {
                        countyItems = it
                        updateCountySelection()
                    },
                    error = {
                        selectionView?.setUpErrorScreen(it)
                    }
                )
            }
        } else {
            updateCountySelection()
        }
    }

    override fun loadTownshipList() {
        val countyCodeList = countyItems?.filter { it.isSelected }?.map { it.code }

        if (countyCodeList != null) {
            job = CoroutineScope(Dispatchers.Main).launch {
                runCatching (
                    block = {
                        dataRepository.getTownshipsByCountyIdList(countyCodeList)
                            .sortedBy { it.name }
                            .toMutableList()
                    },
                    success = {
                        townshipItems = it
                        updateTownshipSelection()
                    },
                    error = {
                        selectionView?.setUpErrorScreen(it)
                    }
                )
            }
        }
    }

    override fun saveSettings() {
        val countyCodeSet = countyItems?.filter { it.isSelected }?.map { it.code }?.toSet()
        val townshipIdSet = townshipItems?.filter { it.isSelected }?.map { it.id.toString() }?.toSet()

        if (countyCodeSet != null && townshipIdSet !== null) {
            KMMStorage.putStringSet(COUNTY_PREFERENCE, countyCodeSet)
            KMMStorage.putStringSet(TOWNSHIP_PREFERENCE, townshipIdSet)
        }
    }
}
