package com.nooro.weathertracker.screens.home

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nooro.weathertracker.network.CityDetails
import com.nooro.weathertracker.network.CityItem
import com.nooro.weathertracker.network.Condition
import com.nooro.weathertracker.network.Current
import com.nooro.weathertracker.network.Location
import com.nooro.weathertracker.network.WeatherService
import com.nooro.weathertracker.utils.SELECTED_CITY_KEY
import com.nooro.weathertracker.utils.dataStore
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val weatherService: WeatherService,
    @ApplicationContext private val context: Context
) : ViewModel() {
    private val _cities = MutableStateFlow<List<CityItem>>(emptyList())
    val cities: StateFlow<List<CityItem>> = _cities

    private val _selectedCity = MutableStateFlow<CityDetails?>(null)
    val selectedCity: StateFlow<CityDetails?> = _selectedCity

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        viewModelScope.launch {
            val savedCityName = context.dataStore.data.first()[SELECTED_CITY_KEY]
            savedCityName?.let { savedCity ->
                val savedCityDetails = fetchCityDetails(savedCity)
                _selectedCity.value = savedCityDetails
            }
        }
    }

    fun searchCities(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
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
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCity(city: CityItem?) {
        viewModelScope.launch {
            city?.let {
                _selectedCity.value = CityDetails(
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
                context.dataStore.edit { preferences ->
                    preferences[SELECTED_CITY_KEY] = it.name
                }
            } ?: run {
                _selectedCity.value = null
                context.dataStore.edit { preferences ->
                    preferences.remove(SELECTED_CITY_KEY)
                }
            }
        }
    }

    private suspend fun fetchCityDetails(cityName: String): CityDetails? {
        return try {
            val forecast = weatherService.getCityForecast(cityName)
            CityDetails(
                location = Location(cityName),
                current = Current(
                    temp_c = forecast.current.temp_c,
                    feelslike_c = forecast.current.feelslike_c,
                    condition = forecast.current.condition,
                    humidity = forecast.current.humidity,
                    uv = forecast.current.uv
                )
            )
        } catch (e: Exception) {
            null
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
