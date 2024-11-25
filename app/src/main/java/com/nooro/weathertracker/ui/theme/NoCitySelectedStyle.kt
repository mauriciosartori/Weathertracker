package com.nooro.weathertracker.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

val NoCitySelectedTextStyle = TextStyle(
    color = Color(0xFF2C2C2C),
    fontSize = 30.sp,
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 45.sp,
    textAlign = TextAlign.Center
)

val PleaseSearchForCityTextStyle = TextStyle(
    color = Color(0xFF2C2C2C),
    fontSize = 15.sp,
    fontFamily = FontFamily.SansSerif,
    fontWeight = FontWeight.SemiBold,
    lineHeight = 22.5.sp,
    textAlign = TextAlign.Center
)
