package edu.codespring.ro.apiaapp.ui.guestProfile

import edu.codespring.ro.apiaapp.constants.Errors
import edu.codespring.ro.apiaapp.data.model.AuthData
import edu.codespring.ro.apiaapp.data.model.UserData
import kotlinx.coroutines.Deferred

interface GuestProfilePresenterMvp {
    fun attachView(guestProfileView: GuestProfileView)
    fun detachView()

    fun login()
    fun loginFailed(error: Errors)
    fun loginSuccess()

    fun authUserBackendAsync(accessToken: String): Deferred<Any?>
    fun saveUser(authData: AuthData)
    fun createUserProfile(user: UserData, userId: Int)
}
