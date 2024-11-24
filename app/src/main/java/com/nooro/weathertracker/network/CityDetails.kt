package com.nooro.weathertracker.network

data class CityDetails(
    val location: Location,
    val current: Current
)

data class Location(
    val name: String
)

data class Current(
    val temp_c: Double,
    val feelslike_c: Double,
    val condition: Condition,
    val humidity: Int,
    val uv: Double
)

data class Condition(
    val text: String,
    val icon: String
)