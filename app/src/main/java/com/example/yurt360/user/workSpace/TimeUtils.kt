/*package com.example.yurt360.user.workSpace  // Burada doğru paket yapısına dikkat et

import java.net.HttpURLConnection
import java.net.URL
import java.io.InputStreamReader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import java.time.ZoneId
import java.time.format.TextStyle

// DayInfo veri sınıfı, her bir günü temsil eder
data class DayInfo(val number: Int, val short: String, val full: String)

// İstanbul'daki gün bilgisini API'den alır
suspend fun getCurrentDayInIstanbul(): LocalDate {
    return withContext(Dispatchers.IO) {
        val url = URL("https://worldtimeapi.org/api/timezone/Europe/Istanbul")  // HTTPS versiyonunu kullanıyoruz

        // Bağlantıyı başlatıyoruz
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        // Zaman aşımı ayarları
        connection.connectTimeout = 15000 // 15 saniye
        connection.readTimeout = 15000 // 15 saniye

        // Bağlantıyı başlatıyoruz
        connection.connect()

        // Gelen yanıtı okuyoruz
        val reader = InputStreamReader(connection.inputStream)
        val response = reader.readText()

        // JSON parsing: 'datetime' bilgisinden tarihi alıyoruz
        val jsonResponse = JSONObject(response)
        val datetime = jsonResponse.getString("datetime")

        // 'datetime' bilgisinden LocalDate olarak tarihi alıyoruz
        val formatter = DateTimeFormatter.ISO_DATE_TIME
        LocalDate.parse(datetime, formatter)
    }
}

// Verilen bir gün için 5 günlük bilgi listesi oluşturur
fun getFiveDayInfo(today: LocalDate): List<DayInfo> {
    return (-2..2).map { offset ->
        val date = today.plusDays(offset.toLong())
        val shortEn = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) // Mon, Tue...
        val fullEn = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH) // Monday...
        DayInfo(number = date.dayOfMonth, short = shortEn, full = fullEn)
    }
}
*/