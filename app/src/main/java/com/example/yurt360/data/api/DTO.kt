package com.example.yurt360.data.api

import com.example.yurt360.common.model.Admin
import com.example.yurt360.common.model.TopUser
import com.example.yurt360.common.model.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

data class LoginRequest(
    val email: String,
    val pass: String
)

@Serializable
data class ProfileDto(
    val id: String,
    val created_at: String? = "",
    val name: String? = "",
    val surname: String? = "",
    val email: String? = "",

    @SerialName("type")
    val isAdmin: Boolean = false,
    val studentNumber: String? = null,
    val phone: String? = null,
    val tc: String? = null,
    val gender: String? = null,
    val bloodType: String? = null,
    val birthDate: String? = null,
    val address: String? = null,
    val location: String? = null,
    val roomNo: String? = null,
    val image_url: String? = null
) {
    fun toTopUser(): TopUser {
        return if (isAdmin) {
            Admin(
                id = id,
                name = name ?: "",
                surname = surname ?: "",
                email = email ?: "",
                image_url = image_url ?: "",
                phone = phone ?: "",
                tc = tc ?: "",
                gender = gender ?: "",
                bloodType = bloodType ?: "",
                birthDate = birthDate ?: "",
                address = address ?: ""
            )
        } else {
            User(
                id = id,
                name = name ?: "",
                surname = surname ?: "",
                email = email ?: "",
                studentNumber = studentNumber ?: "",
                phone = phone ?: "",
                tc = tc ?: "",
                gender = gender ?: "",
                bloodType = bloodType ?: "",
                birthDate = birthDate ?: "",
                address = address ?: "",
                location = location ?: "",
                roomNo = roomNo ?: "",
                image_url = image_url ?: ""
            )
        }
    }
}