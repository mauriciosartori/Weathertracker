package com.nooro.weathertracker.screens.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.nooro.weathertracker.network.CityDetails
import com.nooro.weathertracker.network.CityItem
import com.nooro.weathertracker.network.Condition
import com.nooro.weathertracker.network.Current
import com.nooro.weathertracker.network.Location
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
    }
}

@Composable
fun WeatherScreen(modifier: Modifier = Modifier, viewModel: HomeViewModel) {
    val cities by viewModel.cities.collectAsState()
    val selectedCity by viewModel.selectedCity.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val (searchQuery, setSearchQuery) = remember { mutableStateOf("") }
    val context = LocalContext.current

    if (errorMessage != null) {
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        viewModel.clearError()
    }

    Column(modifier = modifier.fillMaxSize()) {
        SearchBar(
            query = searchQuery,
            onQueryChange = setSearchQuery,
            onSearch = { viewModel.searchCities(searchQuery) }
        )
        if (selectedCity == null) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
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
}

@Composable
fun CityItem(city: CityItem, onClick: (() -> Unit)? = null) {
    Text(
        text = "${city.name}, ${city.country} (Lat: ${city.lat}, Lon: ${city.lon})",
        modifier = Modifier
            .padding(16.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
    )
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SelectedCityDetails(city: CityDetails?, onBackClick: () -> Unit) {
    city?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
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
                model = if (it.current.condition.icon.isNotEmpty()) "https:${it.current.condition.icon}" else null,
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

            Text(
                text = "Back to List",
                modifier = Modifier
                    .padding(top = 16.dp)
                    .clickable { onBackClick() }
            )
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp),
            singleLine = true,
            placeholder = { Text("Search for a city") },
            keyboardActions = KeyboardActions(
                onSearch = { onSearch() }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            )
        )
        IconButton(onClick = { onSearch() }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
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

@Preview(showBackground = true)
@Composable
fun SelectedCityDetailsPreview() {
    WeatherTrackerTheme {
        SelectedCityDetails(
            city = CityDetails(
                location = Location(
                    name = "Washington"
                ),
                current = Current(
                    temp_c = 25.0,
                    feelslike_c = 27.0,
                    condition = Condition(
                        text = "Sunny",
                        icon = "//cdn.weatherapi.com/weather/64x64/day/113.png"
                    ),
                    humidity = 50,
                    uv = 5.0
                )
            ),
            onBackClick = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SearchBarPreview() {
    WeatherTrackerTheme {
        var query by remember { mutableStateOf("") }

        SearchBar(
            query = query,
            onQueryChange = { query = it },
            onSearch = { }
        )
    }
}