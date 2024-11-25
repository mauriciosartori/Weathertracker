package com.nooro.weathertracker.network

data class CityItem(
    val id: Int,
    val name: String,
    val country: String,
    val lat: Double,
    val lon: Double,
    val url: String,
    val temp: Double? = null,
    val icon: String? = null
)
