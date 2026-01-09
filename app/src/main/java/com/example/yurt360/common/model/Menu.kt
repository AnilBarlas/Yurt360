package com.example.yurt360.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Menu(
    val id: Long = 0, // String yerine Long yaptık, varsayılan değer 0
    val date: String,
    val foods: String
)