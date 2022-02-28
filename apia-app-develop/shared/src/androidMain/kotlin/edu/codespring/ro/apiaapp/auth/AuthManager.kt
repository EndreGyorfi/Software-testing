@file:Suppress("WildcardImport")

package edu.codespring.ro.apiaapp.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.auth0.android.Auth0
import com.auth0.android.authentication.AuthenticationAPIClient
import com.auth0.android.authentication.AuthenticationException
import com.auth0.android.authentication.storage.CredentialsManager
import com.auth0.android.authentication.storage.CredentialsManagerException
import com.auth0.android.authentication.storage.SharedPreferencesStorage
import com.auth0.android.callback.Callback
import com.auth0.android.jwt.JWT
import com.auth0.android.provider.WebAuthProvider
import com.auth0.android.result.Credentials
import edu.codespring.ro.apiaapp.constants.AuthErrors
import edu.codespring.ro.apiaapp.constants.SPrefConstants
import edu.codespring.ro.apiaapp.data.model.AuthData
import edu.codespring.ro.apiaapp.data.model.UserData
import edu.codespring.ro.apiaapp.data.repository.AuthRepository
import edu.codespring.ro.apiaapp.spref.KMMStorage
import edu.codespring.ro.apiaapp.ui.editProfile.EditProfilePresenter
import edu.codespring.ro.apiaapp.ui.guestProfile.GuestProfilePresenter
import kotlinx.coroutines.*
import edu.codespring.ro.apiaapp.runCatching

@SuppressLint("StaticFieldLeak")
actual object AuthManager {
    private const val AUTH0_CLIENT_ID = "bcvEF7uG1lOmCeGVWDMRf5Fc4V4hYPDL"
    private const val AUTH0_DOMAIN = "dev-5vcsos0y.eu.auth0.com"

    private var viewContext: Context? = null
    private lateinit var appContext: Context

    private lateinit var auth0: Auth0
    private lateinit var authAPIClient: AuthenticationAPIClient
    private lateinit var sharedPrefStorage: SharedPreferencesStorage
    private lateinit var credentialsManager: CredentialsManager

    private const val PREFERENCES_NAME = "auth0_user_profile"
    private const val EMAIL = "email"
    private const val NAME = "name"
    private const val PICTURE_URL = "picture_url"

    fun setupAuthManager(appContext: Context) {
        this.appContext = appContext

        auth0 = Auth0(AUTH0_CLIENT_ID, AUTH0_DOMAIN)
        authAPIClient = AuthenticationAPIClient(auth0)
        sharedPrefStorage = SharedPreferencesStorage(this.appContext)
        credentialsManager = CredentialsManager(authAPIClient, sharedPrefStorage)
    }

    fun attachViewContext(viewContext: Context) {
        this.viewContext = viewContext
    }

    fun detachViewContext() {
        viewContext = null
    }

    fun saveCredentials(credentials: Credentials) {
        credentialsManager.saveCredentials(credentials)
    }

    fun clearCredentials() {
        credentialsManager.clearCredentials()
    }

    fun isAuthenticated(): Boolean {
        if (!credentialsManager.hasValidCredentials()) {
            // refresh tokens
            credentialsManager.getCredentials(object: Callback<Credentials, CredentialsManagerException> {
                override fun onSuccess(result: Credentials) {
                    // Credentials manager saves this automatically
                    CoroutineScope(Dispatchers.Main).launch {
                        val authResult: AuthData = AuthRepository().authUserBackend(result.accessToken)
                        KMMStorage.putString(SPrefConstants.USER_JWT_TOKEN, authResult.jwt)
                        KMMStorage.putInt(SPrefConstants.USER_ID, authResult.user.id!!)

                        // profile should be already created after first login,
                        // no need to check again
                    }
                }

                override fun onFailure(error: CredentialsManagerException) {
                    println(error.message)
                }
            })

            return credentialsManager.hasValidCredentials()
        }

        return true
    }

    fun saveUserWithJwt(idToken: String) {
        val jwt = JWT(idToken)

        saveUserInfo(UserData(
            jwt.getClaim("name").asString(),
            jwt.getClaim("email").asString(),
            jwt.getClaim("picture").asString()
        ))
    }

    actual fun getUserJwt(): String? {
        return KMMStorage.getString(SPrefConstants.USER_JWT_TOKEN)
    }

    private fun saveUserInfo(user: UserData) {
        val sp: SharedPreferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        sp.edit()
            .putString(NAME, user.name)
            .putString(EMAIL, user.email)
            .putString(PICTURE_URL, user.pictureURL)
            .apply()
    }

    fun getUserInfo(): UserData {
        val sp: SharedPreferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        return UserData(
            sp.getString(NAME, null),
            sp.getString(EMAIL, null),
            sp.getString(PICTURE_URL, null)
        )
    }

    fun deleteUserInfo() {
        val sp: SharedPreferences = appContext.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)

        sp.edit()
            .putString(NAME, null)
            .putString(EMAIL, null)
            .putString(PICTURE_URL, null)
            .apply()
    }

    @DelicateCoroutinesApi
    actual fun login(presenter: GuestProfilePresenter) {
        viewContext?.let {
            WebAuthProvider.login(auth0)
                .withScheme("demo")
                .withScope("openid profile email offline_access read:current_user update:current_user_metadata")
                .withAudience("https://$AUTH0_DOMAIN/api/v2/")
                // Launch the authentication passing the callback where the results will be received
                .start(it, object : Callback<Credentials, AuthenticationException> {
                    // Called when there is an authentication failure
                    override fun onFailure(error: AuthenticationException) {
                        presenter.loginFailed(AuthErrors.AUTH_ERROR)
                    }

                    // Called when authentication completed successfully
                    override fun onSuccess(result: Credentials) {
                        // Save credentials with manager
                        saveCredentials(result)

                        // 1. Auth user with backend - Strapi
                        // 2. Saves user data in KMMStorage
                        GlobalScope.launch(Dispatchers.Main) {
                            runCatching(
                                block = {
                                    presenter.authUserBackendAsync(result.accessToken).await()
                                },
                                success = { authResult ->
                                    presenter.saveUser(authResult)
                                    // Save user to SharedPreference
                                    saveUserWithJwt(result.idToken)

                                    // Create user profile in backend - Strapi if not exists
                                    presenter.createUserProfile(getUserInfo(), authResult.user.id!!)
                                    presenter.loginSuccess()
                                },
                                error = {
                                    presenter.loginFailed(it)
                                }
                            )
                        }
                    }
                })
        }
    }

    actual fun logout(presenter: EditProfilePresenter) {
        viewContext?.let {
            WebAuthProvider.logout(auth0)
                .withScheme("demo")
                .start(it, object: Callback<Void?, AuthenticationException> {
                    override fun onSuccess(result: Void?) {
                        clearCredentials()
                        deleteUserInfo()

                        presenter.logoutSuccess()
                    }

                    override fun onFailure(error: AuthenticationException) {
                        presenter.logoutFailed()
                    }
                })
        }
    }
}
