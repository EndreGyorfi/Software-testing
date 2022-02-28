@file:Suppress("WildcardImport", "MatchingDeclarationName")

package edu.codespring.ro.apiaapp.data.network

import edu.codespring.ro.apiaapp.data.model.WeatherForecast
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.request.*

class WeatherApi {
    companion object {
        private const val API_KEY = "fb0b302629f38f1c943e3ba8af849646"
        private const val BASE_URL = "http://api.openweathermap.org/data/2.5/"

        private const val MAX_CONNECTIONS_COUNT: Int = 1000
        private const val MAX_CONNECTIONS_PER_ROUTE: Int = 100
        private const val PIPELINE_MAX_SIZE: Int = 20
        private const val KEEP_ALIVE_TIME: Long = 5000
        private const val CONNECT_TIMEOUT: Long = 5000
        private const val CONNECT_ATTEMPTS: Int = 5
    }

    private val httpClient = HttpClient(CIO) {
        engine {
            maxConnectionsCount = WeatherApi.MAX_CONNECTIONS_COUNT
            endpoint {
                maxConnectionsPerRoute = WeatherApi.MAX_CONNECTIONS_PER_ROUTE
                pipelineMaxSize = WeatherApi.PIPELINE_MAX_SIZE
                keepAliveTime = WeatherApi.KEEP_ALIVE_TIME
                connectTimeout = WeatherApi.CONNECT_TIMEOUT
                connectAttempts = WeatherApi.CONNECT_ATTEMPTS
            }
        }
        install(JsonFeature) {
            val json = kotlinx.serialization.json.Json { ignoreUnknownKeys = true }
            serializer = KotlinxSerializer(json)
        }
    }

    suspend fun getHourlyForecast(lat : Double, lon : Double) : WeatherForecast {
        return httpClient.get(BASE_URL + "forecast?lat=$lat&lon=$lon&appid=$API_KEY&units=metric")
    }
}
