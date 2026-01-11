package com.example.yurt360.common.model

import kotlinx.serialization.Serializable

@Serializable
data class Announcement(
    val id: Int? = null,
    val title: String,
    val description: String,
    val created_at: String? = null
)

