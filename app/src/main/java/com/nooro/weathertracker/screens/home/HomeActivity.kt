package com.nooro.weathertracker.screens.home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.nooro.weathertracker.network.CityItem
import com.nooro.weathertracker.ui.theme.WeatherTrackerTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val homeViewModel: HomeViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WeatherTrackerTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    WeatherScreen(
                        modifier = Modifier.padding(innerPadding),
                        viewModel = homeViewModel
                    )
                }
            }
        }
        homeViewModel.searchCities("washington")
    }
}

@Composable
fun WeatherScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    val cities by viewModel.cities.collectAsState()

    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(cities) { city ->
            CityItem(city)
        }
    }
}

@Composable
fun CityItem(city: CityItem) {
    Text(
        text = "${city.name}, ${city.country} (Lat: ${city.lat}, Lon: ${city.lon})",
        modifier = Modifier.padding(16.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun WeatherScreenPreview() {
    WeatherTrackerTheme {
        val mockCities = listOf(
            CityItem(1, "Washington", "USA", 38.9, -77.04, ""),
            CityItem(2, "Washington", "UK", 54.91, -1.51, "")
        )
        LazyColumn {
            items(mockCities) { city ->
                CityItem(city)
            }
        }
    }
}