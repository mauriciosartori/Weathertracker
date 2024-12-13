package com.nooro.weathertracker.network

import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {
    /**
     * Returns a list of matching cities for the search query.
     */
    @GET("search.json")
    suspend fun searchCities(
        @Query("q") query: String
    ): List<CityItem>

    @GET("v1/forecast.json")
    suspend fun getCityForecast(
        @Query("q") query: String,
        @Query("days") days: Int = 1
    ): CityDetails
}