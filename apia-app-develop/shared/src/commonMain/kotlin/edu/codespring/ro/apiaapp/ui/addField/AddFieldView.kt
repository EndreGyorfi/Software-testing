package edu.codespring.ro.apiaapp.ui.addField

import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.data.model.County
import edu.codespring.ro.apiaapp.data.model.Field
import edu.codespring.ro.apiaapp.data.model.Township

interface AddFieldView {
    fun setFieldsList(fieldList: List<Field>)
    fun setUpLoadingScreen()
    fun setUpErrorScreen(error: ApiErrors)
}
