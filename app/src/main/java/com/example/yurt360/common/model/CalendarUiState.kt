package com.example.yurt360.common.model

import com.example.yurt360.user.mainScreen.Event
import java.time.LocalDate

data class CalendarUiState(
    val selectedDate: LocalDate = LocalDate.now(),
    val events: List<Event> = emptyList(),
    val isLoading: Boolean = false
)
