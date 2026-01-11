package com.example.yurt360.user.changeRoom

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

fun formatDate(isoString: String?): String {
    if (isoString == null) return ""
    return try {
        val parsedDate = ZonedDateTime.parse(isoString)
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy", Locale.getDefault())
        parsedDate.format(formatter)
    } catch (e: Exception) {
        isoString.take(10)
    }
}