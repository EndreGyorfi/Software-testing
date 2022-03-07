package edu.codespring.ro.apiaapp.ui.editProfile

import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.data.model.Field
import edu.codespring.ro.apiaapp.data.model.UserData


interface EditProfileView {
    fun loadFields(fields: List<Field>)
    fun displayLogoutFailed()
    fun displayLogoutSuccess()
    fun setUserInfo(userInfo: UserData)
    fun setUpErrorScreen(error: ApiErrors)
    fun errorToastMessage(error: ApiErrors)
}
