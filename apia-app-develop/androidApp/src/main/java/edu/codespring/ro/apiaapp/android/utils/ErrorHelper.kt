package edu.codespring.ro.apiaapp.android.utils

import android.content.Context
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.constants.AuthErrors
import edu.codespring.ro.apiaapp.constants.Errors
import edu.codespring.ro.apiaapp.constants.PlannerErrors

fun getErrorMessage(context: Context, error: Errors): String {
    return when (error) {
        ApiErrors.CLIENT_ERROR -> context.getString(R.string.client_error)
        ApiErrors.SERVER_ERROR -> context.getString(R.string.server_error)
        ApiErrors.REDIRECT_ERROR -> context.getString(R.string.redirect_error)
        ApiErrors.CONNECT_ERROR -> context.getString(R.string.connect_error)
        // Auth
        AuthErrors.AUTH_ERROR -> context.getString(R.string.auth_error)
        // Planned packages
        PlannerErrors.ADD_PACKAGE_ERROR -> context.getString(R.string.add_package_failed)
        else -> context.getString(R.string.error)
    }
}

