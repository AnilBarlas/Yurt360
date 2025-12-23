package com.example.yurt360.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Menu(
    val id: Long = 0,
    @SerialName("tarih") val date: String,    // Supabase'deki kolon adı: tarih
    @SerialName("yemekler") val foods: String // Supabase'deki kolon adı: yemekler
)