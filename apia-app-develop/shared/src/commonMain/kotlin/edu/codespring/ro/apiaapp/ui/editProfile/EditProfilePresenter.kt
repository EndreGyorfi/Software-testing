package edu.codespring.ro.apiaapp.ui.editProfile

import edu.codespring.ro.apiaapp.auth.AuthManager
import edu.codespring.ro.apiaapp.data.model.UserData
import edu.codespring.ro.apiaapp.data.repository.DataRepository
import edu.codespring.ro.apiaapp.runCatching
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class EditProfilePresenter : EditProfilePresenterMvp {

    private var job: Job? = null

    private var editProfileView: EditProfileView? = null

    private val dataRepository = DataRepository()

    override fun attachView(editProfileView: EditProfileView) {
        this.editProfileView = editProfileView
    }

    override fun detachView() {
        editProfileView = null
        job?.cancel()
    }

    override fun showFields() {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    dataRepository.getFields()
                },
                success = {
                    editProfileView?.loadFields(it)
                },
                error = {
                    editProfileView?.errorToastMessage(it)
                }
            )
        }
    }

    override fun addField(name: String, size: Float) {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    dataRepository.addField(name, size)
                    dataRepository.getFields()
                },
                success = {
                    editProfileView?.loadFields(it)
                },
                error = {
                    editProfileView?.setUpErrorScreen(it)
                }
            )
        }
    }

    override fun deleteField(id: Int) {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    dataRepository.deleteField(id)
                    dataRepository.getFields()
                },
                success = {
                    editProfileView?.loadFields(it)
                },
                error = {
                    editProfileView?.setUpErrorScreen(it)
                }
            )
        }
    }

    override fun updateField(id: Int, name: String, size: Float) {
        job = CoroutineScope(Dispatchers.Main).launch {
            runCatching(
                block = {
                    dataRepository.updateField(id, name, size)
                    dataRepository.getFields()
                },
                success = {
                    editProfileView?.loadFields(it)
                },
                error = {
                    editProfileView?.setUpErrorScreen(it)
                }
            )
        }
    }

    override fun logout() {
        AuthManager.logout(this)
    }

    override fun logoutFailed() {
        editProfileView?.displayLogoutFailed()
    }

    override fun logoutSuccess() {
        editProfileView?.displayLogoutSuccess()
    }

    override fun setUserData(userInfo: UserData) {
        editProfileView?.setUserInfo(userInfo)
    }
}
