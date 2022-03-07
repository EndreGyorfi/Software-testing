package edu.codespring.ro.apiaapp.android

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import edu.codespring.ro.apiaapp.android.databinding.ActivityMainBinding
import edu.codespring.ro.apiaapp.auth.AuthManager
import edu.codespring.ro.apiaapp.constants.SPrefConstants
import edu.codespring.ro.apiaapp.data.repository.DataRepository
import edu.codespring.ro.apiaapp.spref.KMMStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {
    companion object {
        private const val BACK_BUTTON_NEEDED = "backButtonNeeded"
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        val nightModeFlags: Int = resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK
        when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> { setTheme(R.style.DarkTheme) }
            Configuration.UI_MODE_NIGHT_NO -> setTheme(R.style.AppTheme)
            Configuration.UI_MODE_NIGHT_UNDEFINED -> setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Attach view context to auth0manager
        AuthManager.attachViewContext(this)


        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.navHostFragmentActivityMain)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.selectionFragment,
                R.id.packagesFragment, R.id.dashboardFragment,
                R.id.diaryFragment, R.id.guestProfileFragment, R.id.packageDetailFragment
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        // Hide bottom navigation or toolbar based on which fragment we see
        navController.addOnDestinationChangedListener { _, nd: NavDestination, _ ->
            if (nd.id == R.id.selectionFragment || nd.id == R.id.packageDetailFragment) {
                navView.visibility = View.GONE
                supportActionBar?.hide()
            } else if (nd.id == R.id.guestProfileFragment || nd.id == R.id.userProfileFragment) {
                navView.visibility = View.VISIBLE
                supportActionBar?.hide()
            } else {
                navView.visibility = View.VISIBLE
                supportActionBar?.show()
            }
        }

        // If user has not set yet a county/township preference, navigate to SelectionFragment
        if (KMMStorage.getStringSet(SPrefConstants.COUNTY_PREFERENCE) === null ||
            KMMStorage.getStringSet(SPrefConstants.TOWNSHIP_PREFERENCE) === null) {
            val bundle = bundleOf(BACK_BUTTON_NEEDED to false)
            navController.navigate(R.id.selectionFragment, bundle)
        }
        else {
            // If the user is authenticated and has planned packages, open the Dashboard on app startup
            this.lifecycleScope.launch {
                val dataRepository = DataRepository()
                if (AuthManager.isAuthenticated() && dataRepository.getPlannedPackages().isNotEmpty()
                ) {
                    navController.navigate(R.id.dashboardFragment)
                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        AuthManager.detachViewContext()
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.navHostFragmentActivityMain)
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
