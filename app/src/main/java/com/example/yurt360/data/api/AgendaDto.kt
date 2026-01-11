package com.example.yurt360.data.api

import kotlinx.serialization.Serializable

@Serializable
data class AgendaDto(
    val id: Long? = null,
    val user_id: String,
    val ref_type: String,
    val date: String,
    val time: String
)