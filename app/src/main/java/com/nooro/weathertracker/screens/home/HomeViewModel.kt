package com.nooro.weathertracker.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nooro.weathertracker.network.CityDetails
import com.nooro.weathertracker.network.CityItem
import com.nooro.weathertracker.network.WeatherService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherService: WeatherService
) : ViewModel() {

    private val _cities = MutableStateFlow<List<CityItem>>(emptyList())
    val cities: StateFlow<List<CityItem>> = _cities

    private val _selectedCity = MutableStateFlow<CityDetails?>(null)
    val selectedCity: StateFlow<CityDetails?> = _selectedCity

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    fun searchCities(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val results = weatherService.searchCities(query)
                _cities.value = results
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching cities: ${e.localizedMessage}"
            }
        }
    }

    fun getCityForecast(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = weatherService.getCityDetails(query)
                _selectedCity.value = response
            } catch (e: Exception) {
                _errorMessage.value = "Error fetching forecast: ${e.localizedMessage}"
            }
        }
    }

    fun selectCity(city: CityItem?) {
        if (city != null) {
            getCityForecast("${city.lat},${city.lon}")
        } else {
            _selectedCity.value = null
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}