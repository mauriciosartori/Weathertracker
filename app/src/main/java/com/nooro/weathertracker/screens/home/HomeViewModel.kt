package com.nooro.weathertracker.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun searchCities(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val results = weatherService.searchCities(query)
                _cities.value = results
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error searching cities", e)
            }
        }
    }
}