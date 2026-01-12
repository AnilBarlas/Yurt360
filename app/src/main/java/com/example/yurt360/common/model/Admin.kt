package com.example.yurt360.common.model


data class Admin(
    override val id: String,
    override val name: String,
    override val surname: String,
    override val email: String,
    override val image_url: String,
    override val phone: String,
    override val tc: String,
    override val gender: String,
    override val bloodType: String,
    override val birthDate: String,
    override val address: String
) : TopUser(){

}