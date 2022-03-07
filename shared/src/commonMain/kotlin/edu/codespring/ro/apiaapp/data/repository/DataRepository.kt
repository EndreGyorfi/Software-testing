package edu.codespring.ro.apiaapp.data.repository

import edu.codespring.ro.apiaapp.data.model.County
import edu.codespring.ro.apiaapp.data.model.PackageData
import edu.codespring.ro.apiaapp.data.model.PlannedPackageData
import edu.codespring.ro.apiaapp.data.model.Township
import edu.codespring.ro.apiaapp.data.model.ActivePackageData
import edu.codespring.ro.apiaapp.data.model.Field
import edu.codespring.ro.apiaapp.data.network.DataApi

class DataRepository {

    private val dataApi = DataApi()

    suspend fun getAllCounties(): List<County> {
        return dataApi.getAllCounties()
    }

    suspend fun getTownshipsByCountyIdList(countyCodeList: List<String>): List<Township> {
        val townshipList = mutableListOf<Township>()
        countyCodeList.forEach {
            townshipList.addAll(dataApi.getTownshipsByCountyCode(it))
        }

        return townshipList
    }

    suspend fun getPackageById(id: Int): PackageData {
        return dataApi.getPackageById(id)
    }

    suspend fun getPackagesByTownshipId(id: Int): List<PackageData> {
        return dataApi.getPackagesByTownshipId(id)
    }

    suspend fun getPackagesByTownshipIdList(townshipIdList: List<Int>?): List<PackageData>? {
        if (townshipIdList == null) {
            return null
        }

        val packageSet = mutableSetOf<PackageData>()
        townshipIdList.forEach { townshipIdIt ->
            val packages = getPackagesByTownshipId(townshipIdIt)

            packages.forEach { packageIt -> packageSet.add(packageIt) }
        }

        return packageSet.toList()
    }

    suspend fun getPlannedPackages(): List<PlannedPackageData> {
        return dataApi.getPlannedPackages()
    }

    suspend fun getActivePackages(): List<ActivePackageData> {
        return dataApi.getActivePackages()
    }

    suspend fun getPlannedPackageById(packageId: Int): PlannedPackageData {
        return dataApi.getPlannedPackageById(packageId)
    }

    suspend fun updatePlannedPackage(packageId: Int, plannedPackageData: PlannedPackageData) {
        return dataApi.updatePlannedPackage(packageId, plannedPackageData)
    }

    suspend fun addPlannedPackage(id: Long) {
        return dataApi.addPlannedPackage(id)
    }

    suspend fun addActivePackage(id: Long) {
        return dataApi.addActivePackage(id)
    }

    suspend fun deletePlannedPackage(id: Long) {
        return dataApi.deletePlannedPackage(id)
    }

    suspend fun checkPackageInPlanner(id: Long) {
        return dataApi.checkPackageInPlanner(id)
    }

    suspend fun checkPackageInActive(id: Long) {
        return dataApi.checkPackageInActive(id)
    }

    suspend fun getFields(): List<Field> {
        return dataApi.getFields()
    }

    suspend fun getFieldById(fieldId: Int): Field {
        return dataApi.getFieldById(fieldId)
    }

    suspend fun addField(name: String, size: Float) {
        dataApi.addField(name, size)
    }

    suspend fun deleteField(id: Int) {
        dataApi.deleteField(id)
    }

    suspend fun updateField(id: Int, name: String, size: Float) {
        dataApi.updateField(id, name, size)
    }
}
