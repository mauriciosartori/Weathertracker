package com.nooro.weathertracker.screens.home

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.nooro.weathertracker.network.CityDetails
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
    val selectedCity by viewModel.selectedCity.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val context = LocalContext.current
    if (errorMessage != null) {
        LaunchedEffect(errorMessage) {
            Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
            viewModel.clearError()
        }
    }

    if (selectedCity == null) {
        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            items(cities) { city ->
                CityItem(city = city, onClick = { viewModel.selectCity(city) })
            }
        }
    } else {
        SelectedCityDetails(
            city = selectedCity,
            onBackClick = { viewModel.selectCity(null) }
        )
    }
}

@Composable
fun CityItem(city: CityItem) {
    Text(
        text = "${city.name}, ${city.country} (Lat: ${city.lat}, Lon: ${city.lon})",
        modifier = Modifier.padding(16.dp)
    )
}

@Composable
fun CityItem(city: CityItem, onClick: () -> Unit) {
    Log.d("coco seco", "City: $city")
    Log.d("coco seco", "coco 3")
    Text(
        text = "${city.name}, ${city.country} (Lat: ${city.lat}, Lon: ${city.lon})",
        modifier = Modifier
            .padding(16.dp)
            .clickable(onClick = onClick)
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SelectedCityDetails(city: CityDetails?, onBackClick: () -> Unit) {
    city?.let {
        Column(modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)) {
            Text(
                text = "City: ${it.location.name}",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Temperature: ${it.current.temp_c}",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Condition: ${it.current.condition.text}",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            GlideImage(
                model = "https:${it.current.condition.icon}",
                contentDescription = "Weather Icon",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Humidity: ${it.current.humidity}",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "UV: ${it.current.uv}",
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = "Feels like: ${it.current.feelslike_c}",
                modifier = Modifier.padding(bottom = 8.dp)
            )

            // Button to go back to the list
            Text(
                text = "Back to List",
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { onBackClick() }
            )
        }
    }
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