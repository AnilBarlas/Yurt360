package com.example.yurt360.common.model

import java.time.LocalDate
import java.time.LocalTime
import java.util.UUID

data class AgendaEntity(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val refType: String,
    val date: LocalDate,
    val time: LocalTime
)
