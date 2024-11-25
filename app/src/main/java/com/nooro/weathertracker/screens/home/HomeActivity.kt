package com.nooro.weathertracker.screens.home

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.nooro.weathertracker.R
import com.nooro.weathertracker.network.CityDetails
import com.nooro.weathertracker.network.CityItem
import com.nooro.weathertracker.network.Condition
import com.nooro.weathertracker.network.Current
import com.nooro.weathertracker.network.Location
import com.nooro.weathertracker.ui.theme.NoCitySelectedTextStyle
import com.nooro.weathertracker.ui.theme.PleaseSearchForCityTextStyle
import com.nooro.weathertracker.ui.theme.SearchBarBackgroundColor
import com.nooro.weathertracker.ui.theme.SearchBarPlaceholderColor
import com.nooro.weathertracker.ui.theme.WeatherDetailsCellBackgroundColor
import com.nooro.weathertracker.ui.theme.WeatherDetailsCityNameTextStyle
import com.nooro.weathertracker.ui.theme.WeatherDetailsLabelTextStyle
import com.nooro.weathertracker.ui.theme.WeatherDetailsTemperatureTextStyle
import com.nooro.weathertracker.ui.theme.WeatherDetailsValueTextStyle
import com.nooro.weathertracker.ui.theme.WeatherTrackerTheme
import com.nooro.weathertracker.ui.theme.searchBarColors
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
    val isLoading by viewModel.isLoading.collectAsState()
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
            onSearch = {
                viewModel.selectCity(null)
                viewModel.searchCities(searchQuery)
            }
        )

        when {
            isLoading -> {
                // Show a spinner while loading
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                }
            }

            selectedCity == null && cities.isEmpty() -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    NoCitySelectedMessage()
                }
            }

            selectedCity == null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(cities) { city ->
                        CityItem(city = city, onClick = { viewModel.selectCity(city) })
                    }
                }
            }

            else -> {
                SelectedCityDetails(city = selectedCity)
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun CityItem(city: CityItem, onClick: (() -> Unit)? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable(enabled = onClick != null) { onClick?.invoke() }
            .background(
                color = Color(0xFFF2F2F2),
                shape = RoundedCornerShape(16.dp)
            ),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 30.dp, top = 25.dp, bottom = 20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = city.name,
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 30.sp,
                    color = Color(0xFF2C2C2C),
                    textAlign = TextAlign.Start
                )
            )
            Text(
                text = city.temp?.let { "$it°" } ?: "Loading...",
                style = TextStyle(
                    fontSize = 50.sp,
                    fontWeight = FontWeight.Normal,
                    lineHeight = 60.sp,
                    color = Color(0xFF2C2C2C)
                )
            )
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(end = 30.dp),
            horizontalAlignment = Alignment.End
        ) {
            city.icon?.let {
                GlideImage(
                    model = it,
                    contentDescription = "Weather Icon",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(end = 16.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SelectedCityDetails(city: CityDetails?) {
    city?.let {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            GlideImage(
                model = if (it.current.condition.icon.startsWith("http")) it.current.condition.icon else "https:${it.current.condition.icon}",
                contentDescription = "Weather Icon",
                modifier = Modifier
                    .size(123.dp)
            )

            Text(
                text = it.location.name,
                style = WeatherDetailsCityNameTextStyle
            )

            Text(
                text = "${it.current.temp_c}°",
                modifier = Modifier
                    .width(105.dp)
                    .height(70.dp),
                style = WeatherDetailsTemperatureTextStyle
            )

            Row(
                modifier = Modifier
                    .wrapContentWidth()
                    .background(
                        color = WeatherDetailsCellBackgroundColor,
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(45.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Humidity",
                        style = WeatherDetailsLabelTextStyle
                    )
                    Text(
                        text = "${it.current.humidity}%",
                        style = WeatherDetailsValueTextStyle
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "UV",
                        style = WeatherDetailsLabelTextStyle
                    )
                    Text(
                        text = "${it.current.uv}",
                        style = WeatherDetailsValueTextStyle
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Feels Like",
                        style = WeatherDetailsLabelTextStyle
                    )
                    Text(
                        text = "${it.current.feelslike_c}°",
                        style = WeatherDetailsValueTextStyle
                    )
                }
            }
        }
    }
}


@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(46.dp)
            .background(color = SearchBarBackgroundColor, shape = RoundedCornerShape(16.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = query.trim(),
            onValueChange = onQueryChange,
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp),
            singleLine = true,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.search_location),
                    color = SearchBarPlaceholderColor,
                    fontSize = 13.sp,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.Light,
                    lineHeight = 18.sp
                )
            },
            keyboardActions = KeyboardActions(
                onSearch = {
                    focusManager.clearFocus()
                    onSearch()
                }
            ),
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Search
            ),
            colors = searchBarColors(),
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 13.sp,
                lineHeight = 18.sp
            )
        )
        IconButton(onClick = {
            focusManager.clearFocus()
            onSearch()
        }) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = stringResource(id = R.string.search_icon_description),
                modifier = Modifier.size(17.49.dp),
                tint = Color(0xFFC4C4C4)
            )
        }
    }
}

@Composable
fun NoCitySelectedMessage() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(id = R.string.no_city_selected),
            style = NoCitySelectedTextStyle
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.please_search_for_city),
            style = PleaseSearchForCityTextStyle
        )
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
            )
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