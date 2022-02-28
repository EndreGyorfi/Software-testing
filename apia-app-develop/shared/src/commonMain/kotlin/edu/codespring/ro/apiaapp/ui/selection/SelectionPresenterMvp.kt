package edu.codespring.ro.apiaapp.ui.selection

interface SelectionPresenterMvp {
    fun attachView(selectionView: SelectionView)
    fun detachView()

    // Selection listener
    fun onItemSelected(it: Int, currentPage: Int)

    // Validates if user has selected at least one county/township
    fun validateSelection(currentPage: Int): Boolean

    // Update only selection list for RecyclerView without calling API
    fun updateCountySelection(filterName: String? = null)
    fun updateTownshipSelection(filterName: String? = null)

    // Call API to load list again
    fun loadCountiesList()
    fun loadTownshipList()

    // Save user preferences
    fun saveSettings()
}
