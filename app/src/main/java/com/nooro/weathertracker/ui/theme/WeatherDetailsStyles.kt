package com.nooro.weathertracker.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val WeatherDetailsCityNameTextStyle = TextStyle(
    fontFamily = FontFamily.SansSerif,
    fontSize = 30.sp,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 45.sp,
    textAlign = TextAlign.Center,
    color = Color(0xFF2C2C2C)
)

val WeatherDetailsTemperatureTextStyle = TextStyle(
    fontSize = 50.sp,
    fontWeight = FontWeight.Normal,
    lineHeight = 60.sp,
    textAlign = TextAlign.Center,
    color = Color(0xFF2C2C2C)
)

val WeatherDetailsLabelTextStyle = TextStyle(
    color = Color(0xFFC4C4C4),
    fontSize = 12.sp,
    fontWeight = FontWeight.Normal
)

val WeatherDetailsValueTextStyle = TextStyle(
    color = Color(0xFF9A9A9A),
    fontSize = 15.sp,
    fontWeight = FontWeight.Medium
)

val WeatherDetailsCellBackgroundColor = Color(0xFFF2F2F2)
