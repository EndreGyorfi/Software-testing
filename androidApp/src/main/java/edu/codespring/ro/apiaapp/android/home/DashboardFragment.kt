@file:Suppress("WildcardImport")

package edu.codespring.ro.apiaapp.android.home

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.karumi.dexter.Dexter
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.single.BasePermissionListener
import com.squareup.picasso.Picasso
import edu.codespring.ro.apiaapp.android.R
import edu.codespring.ro.apiaapp.android.databinding.FragmentDashboardBinding
import edu.codespring.ro.apiaapp.android.packages.PackagesFragment
import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.data.model.CompactWeatherData
import edu.codespring.ro.apiaapp.data.model.PackageData
import edu.codespring.ro.apiaapp.data.model.PlannedPackageData
import edu.codespring.ro.apiaapp.ui.dashboard.DashboardPresenter
import edu.codespring.ro.apiaapp.ui.dashboard.DashboardView


class DashboardFragment : Fragment(), DashboardView {
    var binding: FragmentDashboardBinding? = null
    private var packageTileNum: TextView? = null
    private var packageTileLocation: TextView? = null
    private var plannedTileNum: TextView? = null

    private lateinit var dashboardPresenter: DashboardPresenter

    companion object {
        private const val MIN_TIME_MS: Long = 5000
        private const val MIN_DISTANCE_M = 0F
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        dashboardPresenter = DashboardPresenter()
        dashboardPresenter.attachView(this)

        binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboardPresenter.loadPackagesTile()
        dashboardPresenter.loadPlannedTile()
        fillWeatherTile(view)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun fillWeatherTile(view: View) {
        val loader = view.findViewById<ProgressBar>(R.id.weatherLoader)
        val errorTextView = view.findViewById<TextView>(R.id.weatherErrorText)
        val mainContainer = view.findViewById<LinearLayout>(R.id.weatherTileMainContainer)
        loader.visibility = View.VISIBLE
        errorTextView.visibility = View.GONE
        mainContainer.visibility = View.GONE

        Dexter.withContext(activity)
            .withPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            .withListener(object : BasePermissionListener() {
                @Override
                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    val currentLocation = getLocation()
                    if (currentLocation == null) {
                        loader.visibility = View.GONE
                        errorTextView.text = getString(R.string.weather_location_unavailable_error)
                        errorTextView.visibility = View.VISIBLE
                        return
                    }

                    val lat = currentLocation.latitude
                    val lon = currentLocation.longitude
                    dashboardPresenter.loadWeatherData(lat, lon)
                }

                @Override
                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    loader.visibility = View.GONE
                    errorTextView.text = getString(R.string.weather_location_unavailable_error)
                    errorTextView.visibility = View.VISIBLE
                }
            }).check()
    }

    override fun setWeatherData(weatherData: CompactWeatherData) {
        val loader = view?.findViewById<ProgressBar>(R.id.weatherLoader)
        val mainContainer = view?.findViewById<LinearLayout>(R.id.weatherTileMainContainer)
        val weatherIconView = view?.findViewById<ImageView>(R.id.weatherIcon)
        Picasso.get().load(weatherData.iconUrl).into(weatherIconView)
        view?.findViewById<TextView>(R.id.weatherTemperature)?.text =
            weatherData.temp.toString() + "°C"
        view?.findViewById<TextView>(R.id.weatherLocation)?.text = weatherData.city
        loader?.visibility = View.GONE
        mainContainer?.visibility = View.VISIBLE
    }

    @SuppressLint("MissingPermission")
    private fun getLocation(): Location? {
        val locationManager: LocationManager =
            (activity?.getSystemService(Context.LOCATION_SERVICE) as LocationManager?)!!

        var locationByGps: Location? = null
        val gpsLocationListener: LocationListener =
            LocationListener { location -> locationByGps = location }

        var locationByNetwork: Location? = null
        val networkLocationListener: LocationListener =
            LocationListener { location -> locationByNetwork = location }

        // if GPS as LocationResource is enabled
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MIN_TIME_MS, MIN_DISTANCE_M, gpsLocationListener
            )
        }
        // if network as LocationResource is enabled
        if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            locationManager.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                MIN_TIME_MS, MIN_DISTANCE_M, networkLocationListener
            )
        }

        val lastKnownLocationByGps =
            locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        lastKnownLocationByGps?.let { locationByGps = lastKnownLocationByGps }
        val lastKnownLocationByNetwork =
            locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
        lastKnownLocationByNetwork?.let { locationByNetwork = lastKnownLocationByNetwork }

        var currentLocation: Location? = null
        if (locationByGps != null) {
            if (locationByNetwork != null) {
                if (locationByGps!!.accuracy < locationByNetwork!!.accuracy) {
                    currentLocation = locationByGps
                } else {
                    currentLocation = locationByNetwork
                }
            } else {
                currentLocation = locationByGps
            }
        } else if (locationByNetwork != null) {
            currentLocation = locationByNetwork
        }

        return currentLocation
    }

    override fun setPackagesTile(packageList: List<PackageData>) {
        packageTileNum = binding?.packageTileDb?.packageTileNum
        packageTileLocation = binding?.packageTileDb?.packageTileLocation
        val countySet: MutableSet<String> = mutableSetOf()
        var countyListString = ""

        packageTileNum?.text = packageList.size.toString()
        packageList.forEach { it -> it.townships?.forEach { countySet.add(it.name) } }
        countySet.forEach { countyListString += "$it " }
        packageTileLocation?.text = countyListString

        binding?.packageTileDb?.packageTileMoreInfo?.setOnClickListener {
            findNavController().navigate(R.id.action_dashboardFragment_to_packagesFragment)
        }
    }

    override fun setPlannedTile(plannedList: List<PlannedPackageData>) {
        plannedTileNum = binding?.plannedTileDb?.plannedTileNum
        plannedTileNum?.text = plannedList.size.toString()
        binding?.plannedTileDb?.plannedTileMoreInfo?.setOnClickListener {
            findNavController().navigate(R.id.plannedPackagesFragment)
        }
    }

    override fun setUpErrorScreen(errors: ApiErrors) {
        PackagesFragment().setUpErrorScreen(errors)
    }

    override fun showWeatherError() {
        val loader = view?.findViewById<ProgressBar>(R.id.weatherLoader)
        val errorTextView = view?.findViewById<TextView>(R.id.weatherErrorText)
        loader?.visibility = View.GONE
        errorTextView?.text = getString(R.string.weather_error)
        errorTextView?.visibility = View.VISIBLE
    }
}



