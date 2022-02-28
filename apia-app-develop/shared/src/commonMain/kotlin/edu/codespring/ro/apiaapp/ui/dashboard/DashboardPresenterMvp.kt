package edu.codespring.ro.apiaapp.ui.dashboard

interface DashboardPresenterMvp {
    fun attachView(dashboardView: DashboardView)
    fun detachView()
    fun updateDashboardScreen()
    fun loadPackagesTile()
    fun loadPlannedTile()
    fun loadWeatherData(lat : Double, lon : Double)
}
