@file:Suppress("ConstructorParameterNaming")

package edu.codespring.ro.apiaapp.data.model

import kotlinx.serialization.Serializable

@Serializable
data class WeatherForecast(
    val list : List<WeatherData>,
    val city: WeatherCity
)

@Serializable
data class WeatherData(
    val main : WeatherDataMain,
    val weather : List<WeatherDataDetails>,
    val dt_txt : String
)

@Serializable
data class WeatherDataMain(
    val temp : Double
)

@Serializable
data class WeatherDataDetails(
    val icon : String
)

@Serializable
data class WeatherCity(
    val name : String
)

data class CompactWeatherData(
    var temp: Int = 0,
    var city: String = "",
    var iconUrl: String = ""
)
