package com.example.yurt360.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    @SerialName("name") val firstName: String? = "",
    @SerialName("surname") val lastName: String? = "",
    @SerialName("studentNumber") val studentNumber: String? = "",
    @SerialName("roomNo") val roomNumber: String? = "",
    @SerialName("location") val location: String? = ""
)

@Serializable
data class ApplicationForm(
    @SerialName("user_id") val userId: String,
    @SerialName("type") val type: String,
    @SerialName("message") val message: String,
    @SerialName("isApproved") val isApproved: Boolean? = null,
    @SerialName("created_at") val createdAt: String? = null,

    @SerialName("users") val profile: Profile? = null
)