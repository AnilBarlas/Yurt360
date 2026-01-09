package com.example.yurt360.common.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Menu(
    val id: Long, // Veritabanında 'int8' ise Long kullanmak en iyisidir

    @SerialName("tarih") // Supabase'deki sütun adı
    val date: String,    // Kod içinde kullanacağın isim

    @SerialName("yemekler") // Supabase'deki sütun adı
    val foods: String       // Kod içinde kullanacağın isim
)