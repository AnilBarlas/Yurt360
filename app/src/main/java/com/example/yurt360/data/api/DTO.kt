package com.example.yurt360.data.api

import com.google.gson.annotations.SerializedName

// API'ye gidecek istek
data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

// API'den gelen Ham Cevap
// Not: API bize "role" diye bir alan dönmeli ki Admin mi User mı anlayalım.
data class LoginResponse(
    val success: Boolean,
    val message: String,
    val data: UserDto?
)

// Gelen Ham Kullanıcı Verisi
data class UserDto(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,        // "admin" veya "user"

)