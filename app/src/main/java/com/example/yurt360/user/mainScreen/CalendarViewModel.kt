package com.example.yurt360.user.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.common.model.CalendarUiState
import com.example.yurt360.data.api.AgendaDto
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CalendarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    init {
        // ViewModel ilk açıldığında bugünün verilerini çek
        fetchEventsForDate(LocalDate.now())
    }

    fun onDateSelected(date: LocalDate) {
        _uiState.value = _uiState.value.copy(selectedDate = date)
        fetchEventsForDate(date)
    }

    private fun fetchEventsForDate(date: LocalDate) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)

            try {
                // 1. Giriş yapan kullanıcının ID'sini kontrol et
                val currentUser = SupabaseClient.client.auth.currentUserOrNull()
                val userId = currentUser?.id

                if (userId == null) {
                    println("CalendarViewModel: Kullanıcı girişi yapılmamış (User ID null).")
                    _uiState.value = _uiState.value.copy(events = emptyList(), isLoading = false)
                    return@launch
                }

                // Tarihi 'YYYY-MM-DD' formatına çevir
                val dateString = date.format(DateTimeFormatter.ISO_LOCAL_DATE)
                println("CalendarViewModel: Veri çekiliyor... Tablo: 'ajanda', UserID: $userId, Tarih: $dateString")

                // 2. Supabase Sorgusu - Tablo ismi "ajanda" olarak güncellendi
                val result = SupabaseClient.client.from("ajanda").select {
                    filter {
                        eq("user_id", userId)
                        eq("date", dateString)
                    }
                }.decodeList<AgendaDto>()

                println("CalendarViewModel: ${result.size} adet etkinlik bulundu.")

                // 3. Veriyi UI Modelini (Event) Dönüştür
                val uiEvents = result.map { dto ->
                    // Saat formatlama (HH:mm:ss -> HH:mm)
                    val parsedTime = try {
                        LocalTime.parse(dto.time)
                    } catch (e: Exception) {
                        LocalTime.of(0, 0)
                    }

                    Event(
                        title = mapRefTypeToTitle(dto.ref_type),
                        subtitle = mapRefTypeToSubtitle(dto.ref_type),
                        time = parsedTime.format(DateTimeFormatter.ofPattern("HH:mm")),
                        isSelected = false
                    )
                }.sortedBy { it.time } // Saate göre sırala

                _uiState.value = _uiState.value.copy(
                    events = uiEvents,
                    isLoading = false
                )

            } catch (e: Exception) {
                // Hata durumunda log bas
                e.printStackTrace()
                println("CalendarViewModel HATA: ${e.message}")

                _uiState.value = _uiState.value.copy(
                    events = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    private fun mapRefTypeToTitle(refType: String): String {
        val type = refType.lowercase()
        return when {
            type.contains("work") -> "Library"
            type.contains("laundry") -> "Laundry Room"
            type.contains("menu") -> "Food"
            else -> "Etkinlik"
        }
    }

    private fun mapRefTypeToSubtitle(refType: String): String {
        val type = refType.lowercase()
        return when {
            type.contains("work") -> "Çalışma Rezervasyonu"
            type.contains("laundry") -> "Yıkama/Kurutma Randevusu"
            type.contains("menu") -> "Yemekhane Menüsü"
            else -> "Planlanmış Etkinlik"
        }
    }
}