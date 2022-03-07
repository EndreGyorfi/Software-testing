package edu.codespring.ro.apiaapp.ui.dashboard

import edu.codespring.ro.apiaapp.constants.ApiErrors
import edu.codespring.ro.apiaapp.data.model.CompactWeatherData
import edu.codespring.ro.apiaapp.data.model.PackageData
import edu.codespring.ro.apiaapp.data.model.PlannedPackageData

interface DashboardView {
    fun setPackagesTile(packageList: List<PackageData>)
    fun setPlannedTile(plannedList: List<PlannedPackageData>)
    fun setUpErrorScreen(errors: ApiErrors)
    fun setWeatherData(weatherData: CompactWeatherData)
    fun showWeatherError()
}
