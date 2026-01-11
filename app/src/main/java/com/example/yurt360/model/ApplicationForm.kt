package com.example.yurt360.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApplicationForm(
    @SerialName("user_id") val userId: String,
    @SerialName("type") val type: String,
    @SerialName("message") val message: String,
    @SerialName("isApproved") val isApproved: Boolean? = null,
    @SerialName("created_at") val createdAt: String? = null
)