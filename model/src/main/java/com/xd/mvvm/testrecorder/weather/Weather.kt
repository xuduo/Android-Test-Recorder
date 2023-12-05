package com.xd.mvvm.testrecorder.weather

import com.squareup.moshi.Json
import java.time.LocalDateTime

data class Weather(
    @Json(name = "timezone")
    val timeZone: String,
    val hourly: Hourly

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Weather

        if (timeZone != other.timeZone) return false
        if (hourly != other.hourly) return false

        return true
    }

    override fun hashCode(): Int {
        var result = timeZone.hashCode()
        result = 31 * result + hourly.hashCode()
        return result
    }
}

data class Hourly(
    val time: List<LocalDateTime>,
    @Json(name = "temperature_2m")
    val temperature: List<String>,
    @Json(name = "relativehumidity_2m")
    val humidity: List<Int>

) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Hourly

        if (time != other.time) return false
        if (temperature != other.temperature) return false
        if (humidity != other.humidity) return false

        return true
    }

    override fun hashCode(): Int {
        var result = time.hashCode()
        result = 31 * result + temperature.hashCode()
        result = 31 * result + humidity.hashCode()
        return result
    }
}
