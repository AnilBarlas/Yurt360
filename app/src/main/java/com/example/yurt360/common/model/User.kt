package com.example.yurt360.common.model



data class User(
    override val id: String,
    override val name: String,
    override val surname: String,
    override val email: String,
    val studentNumber: String,
    val phone: String,
    val tc: String,
    val gender: String,
    val bloodType: String,
    val birthDate: String,
    val address: String,
    val location: String,
    val roomNo: String,
    val image_url: String
) : TopUser(){

}
