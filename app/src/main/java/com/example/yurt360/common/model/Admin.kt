package com.example.yurt360.common.model


data class Admin(
    override val id: String,
    override val name: String,
    override val surname: String,
    override val email: String
) : TopUser(){

}