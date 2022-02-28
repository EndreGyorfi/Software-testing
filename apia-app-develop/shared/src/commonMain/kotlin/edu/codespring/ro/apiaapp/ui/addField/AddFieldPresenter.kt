package edu.codespring.ro.apiaapp.ui.addField

import edu.codespring.ro.apiaapp.data.model.Field
import edu.codespring.ro.apiaapp.data.model.PlannedPackageData
import edu.codespring.ro.apiaapp.data.repository.DataRepository
import edu.codespring.ro.apiaapp.runCatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AddFieldPresenter : AddFieldPresenterMvp {
    private var addFieldView: AddFieldView? = null

    private val dataRepository = DataRepository()

    private var fullFieldsList: MutableList<Field>? = null
    private var fieldsListOfPackage: MutableList<Field>? = null
    private var plannedFieldsList: MutableList<Field>? = null
    private var plannedPackageData: PlannedPackageData? = null
    var fieldById: Field? = null

    private var job: Job? = null

    override fun attachView(selectionView: AddFieldView) {
        this.addFieldView = selectionView
    }

    override fun detachView() {
        addFieldView = null
        job?.cancel()
    }


    private fun setChecked(list1: List<Field>, list2: List<Field>) {
        list1.forEach { i ->
            list2.forEach { j ->
                if (i == j) {
                    i.checked = true
                }
            }
        }
    }

    override fun loadFieldsList(packageId: Int) {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    addFieldView?.setUpLoadingScreen()
                    dataRepository.getFields().toMutableList()
                },
                success = {
                    fullFieldsList = it
                },
                error = {
                    addFieldView?.setUpErrorScreen(it)
                }
            )
            runCatching(
                block = {
                    dataRepository.getPlannedPackageById(packageId).fields.toMutableList()
                },
                success = {
                    fieldsListOfPackage = it
                    setChecked(fullFieldsList!!, fieldsListOfPackage!!)
                    addFieldView?.setFieldsList(fullFieldsList!!)
                },
                error = {
                    addFieldView?.setUpErrorScreen(it)
                }
            )
        }
    }

    override fun addField(fieldId: Int, packageId: Int) {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    dataRepository.getFields().toMutableList()
                },
                success = {
                    fullFieldsList = it
                },
                error = { addFieldView?.setUpErrorScreen(it) }
            )
            runCatching(
                block = {
                    dataRepository.getPlannedPackageById(packageId)
                },
                success = {
                    plannedPackageData = it
                    plannedFieldsList = plannedPackageData?.fields?.toMutableList()
                    setChecked(fullFieldsList!!, plannedFieldsList!!)
                },
                error = { addFieldView?.setUpErrorScreen(it) }
            )

            runCatching(
                block = {
                    dataRepository.getFieldById(fieldId)
                },
                success = {
                    fieldById = it
                },
                error = { addFieldView?.setUpErrorScreen(it) }
            )

            if (plannedFieldsList?.any { it.id == fieldId } == false) {
                fullFieldsList?.find { it.id == fieldId }?.checked = true
                fieldById?.let { plannedFieldsList?.add(it) }

            } else {
                fullFieldsList?.find { it.id == fieldId }?.checked = false
                plannedFieldsList?.remove(fieldById)
            }
            plannedPackageData?.fields = plannedFieldsList!!
            if (plannedPackageData != null) {
                runCatching(
                    block = {
                        dataRepository.updatePlannedPackage(packageId, plannedPackageData!!)
                    },
                    success = {
                        addFieldView?.setFieldsList(fullFieldsList!!)
                    },
                    error = { addFieldView?.setUpErrorScreen(it) }
                )
            }
        }
    }
}
