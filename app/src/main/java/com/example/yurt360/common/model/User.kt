package com.example.yurt360.common.model



data class User(
    override val id: String,
    override val name: String,
    override val surname: String,
    override val email: String,
    val studentNumber: String,
    override val phone: String,
    override val tc: String,
    override val gender: String,
    override val bloodType: String,
    override val birthDate: String,
    override val address: String,
    val location: String,
    val roomNo: String,
    override val image_url: String
) : TopUser(){

}
