package com.xd.mvvm.boilerplate.data

import com.squareup.moshi.FromJson
import com.squareup.moshi.ToJson
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class LocalDateTimeAdapter {
    private val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    @ToJson
    fun toJson(dateTime: LocalDateTime): String {
        return formatter.format(dateTime)
    }

    @FromJson
    fun fromJson(dateTimeString: String): LocalDateTime {
        return LocalDateTime.parse(dateTimeString, formatter)
    }
}
