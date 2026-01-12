package com.example.yurt360.common.model

sealed class TopUser {
    abstract val id: String
    abstract val name: String
    abstract val surname: String
    abstract val email: String
    abstract val image_url: String
    abstract val phone: String
    abstract val tc: String
    abstract val gender: String
    abstract val bloodType: String
    abstract val birthDate: String
    abstract val address: String
}




