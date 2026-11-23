
package com.example.propcare.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

@Suppress("unused")
interface WeatherApiService {

    @GET("v1/forecast")
    suspend fun getCurrentWeather(
        @Query("latitude") latitude: Double = -1.2864,
        @Query("longitude") longitude: Double = 36.8172,
        @Query("current_weather") currentWeather: Boolean = true,
    ): WeatherResponse
}
