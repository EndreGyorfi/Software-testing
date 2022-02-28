package edu.codespring.ro.apiaapp.ui.editProfile

import edu.codespring.ro.apiaapp.data.model.UserData

interface EditProfilePresenterMvp {
    fun attachView(editProfileView: EditProfileView)
    fun detachView()
    fun showFields()
    fun addField(name: String, size: Float)
    fun deleteField(id: Int)
    fun updateField(id: Int, name: String, size: Float)
    fun logout()
    fun logoutFailed()
    fun logoutSuccess()
    fun setUserData(userInfo: UserData)
}
