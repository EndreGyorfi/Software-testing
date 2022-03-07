package edu.codespring.ro.apiaapp.android

import android.app.Application
import edu.codespring.ro.apiaapp.auth.AuthManager
import edu.codespring.ro.apiaapp.spref.KMMStorage

class ApiaApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Set up storage
        KMMStorage.setSPref(this)
        // Set up auth manager
        AuthManager.setupAuthManager(this)
    }
}
