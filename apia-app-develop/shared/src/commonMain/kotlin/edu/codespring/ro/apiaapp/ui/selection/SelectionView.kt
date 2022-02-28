package edu.codespring.ro.apiaapp.ui.selection

import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.data.model.County
import edu.codespring.ro.apiaapp.data.model.Township

interface SelectionView {
    fun setCountiesList(selectionList: List<County>)
    fun setTownshipsList(selectionList: List<Township>)
    fun showContent()
    fun setUpLoadingScreen()
    fun setUpErrorScreen(error: ApiErrors)
}
