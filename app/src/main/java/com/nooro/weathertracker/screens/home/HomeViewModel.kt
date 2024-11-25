package com.nooro.weathertracker.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nooro.weathertracker.network.CityDetails
import com.nooro.weathertracker.network.CityItem
import com.nooro.weathertracker.network.Condition
import com.nooro.weathertracker.network.Current
import com.nooro.weathertracker.network.Location
import com.nooro.weathertracker.network.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(private val weatherService: WeatherService) : ViewModel() {
    private val _cities = MutableStateFlow<List<CityItem>>(emptyList())
    val cities: StateFlow<List<CityItem>> = _cities

    private val _selectedCity = MutableStateFlow<CityDetails?>(null)
    val selectedCity: StateFlow<CityDetails?> = _selectedCity

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun searchCities(query: String) {
        viewModelScope.launch {
            try {
                val searchResults = weatherService.searchCities(query)
                val citiesWithForecast = searchResults.map { city ->
                    val forecast = weatherService.getCityForecast("${city.lat},${city.lon}")
                    city.copy(
                        temp = forecast.current.temp_c,
                        icon = "https:${forecast.current.condition.icon}"
                    )
                }
                _cities.value = citiesWithForecast
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching cities or forecasts"
            }
        }
    }

    fun selectCity(city: CityItem?) {
        _selectedCity.value = city?.let {
            CityDetails(
                location = Location(it.name),
                current = Current(
                    temp_c = it.temp ?: 0.0,
                    feelslike_c = it.temp ?: 0.0,
                    condition = Condition(
                        text = "Unknown",
                        icon = ""
                    ),
                    humidity = 0,
                    uv = 0.0
                )
            )
        }
    }


    fun clearError() {
        _errorMessage.value = null
    }
}
