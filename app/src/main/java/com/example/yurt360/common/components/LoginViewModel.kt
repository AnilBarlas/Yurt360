package com.example.yurt360.common.components

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class LoginViewModel : ViewModel() {
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    fun onUsernameChange(newText: String) {
        _username.value = newText
    }

    fun onPasswordChange(newText: String) {
        _password.value = newText
    }

    fun onLoginClick() {
        println("Giriş yapılıyor: ${_username.value}")
    }
}