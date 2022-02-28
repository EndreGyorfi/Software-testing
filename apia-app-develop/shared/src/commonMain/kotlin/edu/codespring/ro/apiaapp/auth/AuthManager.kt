package edu.codespring.ro.apiaapp.auth

import edu.codespring.ro.apiaapp.ui.editProfile.EditProfilePresenter
import edu.codespring.ro.apiaapp.ui.guestProfile.GuestProfilePresenter

expect object AuthManager {
    fun login(presenter: GuestProfilePresenter)
    fun logout(presenter: EditProfilePresenter)
    fun getUserJwt(): String?
}
