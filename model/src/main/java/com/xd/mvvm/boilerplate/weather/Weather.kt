package com.xd.mvvm.boilerplate.weather

import com.squareup.moshi.Json
import java.time.LocalDateTime

data class Weather(
    @Json(name = "timezone")
    val timeZone: String,
    val hourly: Hourly
)

data class Hourly(
    val time: List<LocalDateTime>,
    @Json(name = "temperature_2m")
    val temperature: List<String>,
    @Json(name = "relativehumidity_2m")
    val humidity: List<Int>
)
