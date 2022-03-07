package edu.codespring.ro.apiaapp.ui.addField

interface AddFieldPresenterMvp {
    fun attachView(selectionView: AddFieldView)
    fun detachView()

    // Call API to load list
    fun loadFieldsList(packageId: Int)

    // Call API to update planned package
    fun addField(fieldId: Int, packageId: Int)
}
