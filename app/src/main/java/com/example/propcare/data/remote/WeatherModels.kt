package com.example.propcare.data.remote

import com.google.gson.annotations.SerializedName

@Suppress("unused")
data class WeatherResponse(
    @SerializedName("current_weather")
    val currentWeather: CurrentWeather?,
)

data class CurrentWeather(
    val temperature: Double,
    @SerializedName("windspeed")
    val windSpeed: Double,
    @SerializedName("weathercode")
    val weatherCode: Int,
)
