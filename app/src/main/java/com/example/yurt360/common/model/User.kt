package com.example.yurt360.common.model

import com.example.yurt360.common.model.TopUser

data class User(
    override val id: Int,
    override val name: String,
    override val surname: String,
    override val email: String,
    val phone: String,
    val tc: String,
    val gender: String,
    val bloodType: String,
    val birthDate: String,
    val address: String,
    val location: String,
    val roomNo: String
) : TopUser()