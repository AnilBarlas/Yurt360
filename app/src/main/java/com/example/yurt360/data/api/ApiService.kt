package com.example.yurt360.data.api

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("auth/login")
    suspend fun girisYap(@Body request: LoginRequest): Response<LoginResponse>
}