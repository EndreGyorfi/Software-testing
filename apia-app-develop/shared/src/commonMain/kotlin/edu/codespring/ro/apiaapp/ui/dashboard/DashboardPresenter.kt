@file:Suppress("WildcardImport")
package edu.codespring.ro.apiaapp.ui.dashboard

import edu.codespring.ro.apiaapp.constants.SPrefConstants
import edu.codespring.ro.apiaapp.data.model.CompactWeatherData
import edu.codespring.ro.apiaapp.data.model.PackageData
import edu.codespring.ro.apiaapp.data.model.PlannedPackageData
import edu.codespring.ro.apiaapp.data.model.WeatherData
import edu.codespring.ro.apiaapp.data.model.WeatherForecast
import edu.codespring.ro.apiaapp.data.network.WeatherApi
import edu.codespring.ro.apiaapp.data.repository.DataRepository
import edu.codespring.ro.apiaapp.spref.KMMStorage
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class DashboardPresenter : DashboardPresenterMvp {
    private var job: Job? = null
    private var dashboardView: DashboardView? = null
    private var packageList: MutableList<PackageData>? = null
    private var plannedList: MutableList<PlannedPackageData>? = null
    private val dataRepository = DataRepository()

    companion object {
        private const val TOMORROW_HOUR = 12
        private const val TOMORROW_MIN = 0
    }

    override fun attachView(dashboardView: DashboardView) {
        this.dashboardView = dashboardView
       this.dashboardView = dashboardView
    }

    override fun detachView() {
        dashboardView = null
        job?.cancel()
    }

    override fun updateDashboardScreen() {
        packageList?.let { dashboardView?.setPackagesTile(it) }
        plannedList?.let { dashboardView?.setPlannedTile(it) }
    }

    override fun loadPackagesTile() {
        if (packageList == null) {
            job = CoroutineScope(Dispatchers.Main).launch {
                edu.codespring.ro.apiaapp.runCatching(
                    block = {
                        dataRepository.getPackagesByTownshipIdList(
                            KMMStorage.getStringSet(SPrefConstants.TOWNSHIP_PREFERENCE)
                                ?.map { it.toInt() }?.toList()
                        )?.toMutableList()
                    },
                    success = {
                        packageList = it
                        updateDashboardScreen()
                    },
                    error = {
                        dashboardView?.setUpErrorScreen(it)
                    }
                )
            }
        } else {
            updateDashboardScreen()
        }
    }

    override fun loadWeatherData(lat: Double, lon: Double) {
        val weatherData = CompactWeatherData()

        job = CoroutineScope(Dispatchers.Main).launch {
            edu.codespring.ro.apiaapp.runCatching(
                block = {
                    WeatherApi().getHourlyForecast(lat, lon)
                },
                success = {
                    val tomorrow = findTomorrow(it)
                    if (tomorrow == null) {
                        dashboardView?.showWeatherError()
                        return@launch
                    }
                    weatherData.iconUrl = "http://openweathermap.org/img/wn/" + tomorrow.weather[0].icon + "@4x.png"
                    weatherData.temp = tomorrow.main.temp.toInt()
                    weatherData.city = it.city.name
                    dashboardView?.setWeatherData(weatherData)
                },
                error = {
                    dashboardView?.showWeatherError()
                }
            )
        }
    }

    private fun findTomorrow(weatherForecast: WeatherForecast): WeatherData? {
        val tomorrow = LocalDate.now().plus(1, ChronoUnit.DAYS).atTime(TOMORROW_HOUR, TOMORROW_MIN)
        for(w in weatherForecast.list) {
            val dateString = w.dt_txt
            val date = LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
            if (date.equals(tomorrow)) {
                return w
            }
        }
        return null
    }

    override fun loadPlannedTile() {
        if (plannedList == null) {
            job = CoroutineScope(Dispatchers.Main).launch {
                edu.codespring.ro.apiaapp.runCatching(
                    block = {
                        dataRepository.getPlannedPackages().toMutableList()
                    },
                    success = {
                        plannedList = it
                        updateDashboardScreen()
                    },
                    error = {
                        dashboardView?.setUpErrorScreen(it)
                    }
                )
            }
        } else {
            updateDashboardScreen()
        }
    }
}

