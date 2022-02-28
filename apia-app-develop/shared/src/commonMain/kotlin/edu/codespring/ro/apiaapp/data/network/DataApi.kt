@file:Suppress("WildcardImport", "MatchingDeclarationName")

package edu.codespring.ro.apiaapp.data.network

import edu.codespring.ro.apiaapp.auth.AuthManager
import edu.codespring.ro.apiaapp.data.model.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.json.Json

class DataApi {
    companion object {
        private const val BASE_URL = "http://10.0.2.2:3001/"
        private const val COUNTY_ENDPOINT = BASE_URL + "counties/"
        private const val TOWNSHIP_ENDPOINT = BASE_URL + "townships/"
        private const val PACKAGES_ENDPOINT = BASE_URL + "packages/"
        private const val PLANNED_PACKAGES_ENDPOINT = BASE_URL + "planned-packages/"
        private const val ACTIVE_PACKAGES_ENDPOINT = BASE_URL + "active-packages/"
        private const val FIELD_ENDPOINT = BASE_URL + "fields/"

        private const val MAX_CONNECTIONS_COUNT: Int = 1000
        private const val MAX_CONNECTIONS_PER_ROUTE: Int = 100
        private const val PIPELINE_MAX_SIZE: Int = 20
        private const val KEEP_ALIVE_TIME: Long = 5000
        private const val CONNECT_TIMEOUT: Long = 5000
        private const val CONNECT_ATTEMPTS: Int = 5
    }

    private val httpClient = HttpClient(CIO) {
        engine {
            maxConnectionsCount = MAX_CONNECTIONS_COUNT
            endpoint {
                maxConnectionsPerRoute = MAX_CONNECTIONS_PER_ROUTE
                pipelineMaxSize = PIPELINE_MAX_SIZE
                keepAliveTime = KEEP_ALIVE_TIME
                connectTimeout = CONNECT_TIMEOUT
                connectAttempts = CONNECT_ATTEMPTS
            }
        }
        install(JsonFeature) {
            val json = Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
    }

    suspend fun getAllCounties(): List<County> {
        return httpClient.get(COUNTY_ENDPOINT)
    }

    suspend fun getTownshipsByCountyCode(code: String): List<Township> {
        return httpClient.get("$TOWNSHIP_ENDPOINT?code=$code")
    }

    suspend fun getPackagesByTownshipId(id: Int): List<PackageData> {
        return httpClient.get("$PACKAGES_ENDPOINT?townships.id=$id")
    }

    suspend fun getPackageById(id: Int): PackageData {
        return httpClient.get(PACKAGES_ENDPOINT + id)
    }

    suspend fun getPlannedPackages(): List<PlannedPackageData> {
        return httpClient.get(PLANNED_PACKAGES_ENDPOINT) {
            headers {
                append("Authorization", "Bearer ${AuthManager.getUserJwt()}")
            }
        }
    }

    suspend fun getActivePackages(): List<ActivePackageData> {
        return httpClient.get(ACTIVE_PACKAGES_ENDPOINT) {
            headers {
                append("Authorization", "Bearer ${AuthManager.getUserJwt()}")
            }
        }
    }

    suspend fun getPlannedPackageById(packageId: Int): PlannedPackageData {
        return httpClient.get(PLANNED_PACKAGES_ENDPOINT + packageId) {
            headers {
                append("Authorization", "Bearer ${AuthManager.getUserJwt()}")
            }
        }
    }

    suspend fun addPlannedPackage(id: Long) {
        return httpClient.post(PLANNED_PACKAGES_ENDPOINT) {
            headers {
                append("Authorization", "Bearer ${AuthManager.getUserJwt()}")
            }

            contentType(ContentType.Application.Json)
            body = BaseEntityData(id)
        }
    }

    suspend fun addActivePackage(id: Long) {
        return httpClient.post(ACTIVE_PACKAGES_ENDPOINT) {
            headers {
                append("Authorization", "Bearer ${AuthManager.getUserJwt()}")
            }

            contentType(ContentType.Application.Json)
            body = BaseEntityData(id)
        }
    }

    suspend fun updatePlannedPackage(packageId: Int, plannedPackageData : PlannedPackageData) {
        return httpClient.put(PLANNED_PACKAGES_ENDPOINT + packageId) {
            headers {
                append("Authorization", "Bearer ${AuthManager.getUserJwt()}")
            }

            contentType(ContentType.Application.Json)
            body = plannedPackageData
        }
    }

    suspend fun deletePlannedPackage(id: Long) {
        return httpClient.delete(PLANNED_PACKAGES_ENDPOINT + id) {
            headers {
                append("Authorization", "Bearer ${AuthManager.getUserJwt()}")
            }
        }
    }

    suspend fun checkPackageInPlanner(id: Long) {
        return httpClient.get("$PLANNED_PACKAGES_ENDPOINT?packageId=$id") {
            headers {
                append("Authorization", "Bearer ${AuthManager.getUserJwt()}")
            }
        }
    }

    suspend fun checkPackageInActive(id: Long) {
        return httpClient.get("$ACTIVE_PACKAGES_ENDPOINT?packageId=$id") {
            headers {
                append("Authorization", "Bearer ${AuthManager.getUserJwt()}")
            }
        }
    }

    suspend fun getFields():List<Field>{
        return httpClient.get(FIELD_ENDPOINT)
    }

    suspend fun getFieldById(fieldId: Int):Field{
        return httpClient.get(FIELD_ENDPOINT + fieldId)
    }

    suspend fun addField(name: String, size: Float) {
        httpClient.post<HttpResponse>(FIELD_ENDPOINT) {
            contentType(ContentType.Application.Json)
            @Suppress("MagicNumber")
            body = Field(0, name, size)
        }
    }

    suspend fun deleteField(id: Int) {
        httpClient.delete<HttpResponse>(FIELD_ENDPOINT+ id)
    }

    suspend fun updateField(id: Int, name: String, size: Float) {
        httpClient.put<HttpResponse>(FIELD_ENDPOINT+ id) {
            contentType(ContentType.Application.Json)
            @Suppress("MagicNumber")
            body = Field(0, name, size)
        }
    }
}
