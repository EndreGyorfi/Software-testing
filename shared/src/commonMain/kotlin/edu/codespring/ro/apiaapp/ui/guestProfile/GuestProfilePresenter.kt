@file:Suppress("WildcardImport")
package edu.codespring.ro.apiaapp.ui.guestProfile

import edu.codespring.ro.apiaapp.auth.AuthManager
import edu.codespring.ro.apiaapp.constants.Errors
import edu.codespring.ro.apiaapp.constants.SPrefConstants.USER_ID
import edu.codespring.ro.apiaapp.constants.SPrefConstants.USER_JWT_TOKEN
import edu.codespring.ro.apiaapp.data.model.AuthData
import edu.codespring.ro.apiaapp.data.model.UserData
import edu.codespring.ro.apiaapp.data.repository.AuthRepository
import edu.codespring.ro.apiaapp.spref.KMMStorage
import edu.codespring.ro.apiaapp.runCatching
import kotlinx.coroutines.*

class GuestProfilePresenter : GuestProfilePresenterMvp {

    private val authRepository = AuthRepository()
    private var job: Job? = null

    private var guestProfileView: GuestProfileView? = null

    override fun attachView(guestProfileView: GuestProfileView) {
        this.guestProfileView = guestProfileView
    }

    override fun detachView() {
        guestProfileView = null
        job?.cancel()
    }

    override fun login() {
        AuthManager.login(this)
    }

    override fun loginFailed(error: Errors) {
        guestProfileView?.errorToastMessage(error)
    }

    override fun loginSuccess() {
        guestProfileView?.displayLoginSuccess()
    }

    override fun authUserBackendAsync(accessToken: String)
    : Deferred<AuthData> = CoroutineScope(Dispatchers.Main).async {
        authRepository.authUserBackend(accessToken)
    }

    override fun saveUser(authData: AuthData) {
        KMMStorage.putString(USER_JWT_TOKEN, authData.jwt)
        KMMStorage.putInt(USER_ID, authData.user.id!!)
    }

    // Strapi
    override fun createUserProfile(user: UserData, userId: Int) {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    authRepository.checkUserProfileExists(user.email!!)
                },
                success = {
                    if (!it) {
                        // Create user profile
                        authRepository.createUserProfile(user.name!!, userId)
                    }
                },
                error = {
                    guestProfileView?.errorToastMessage(it)
                }
            )
        }
    }
}
