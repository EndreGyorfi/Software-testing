package edu.codespring.ro.apiaapp.ui.guestProfile

import edu.codespring.ro.apiaapp.constants.Errors

interface GuestProfileView {
    fun displayLoginSuccess()
    fun errorToastMessage(error: Errors)
}
