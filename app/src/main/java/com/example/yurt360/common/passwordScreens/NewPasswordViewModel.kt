package com.example.yurt360.common.passwordScreens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class NewPasswordViewModel : ViewModel() {

    var newPassword by mutableStateOf("")
        private set

    var notificationMessage by mutableStateOf<String?>(null)
        private set

    fun onPasswordChange(input: String) {
        newPassword = input
    }

    fun submitPassword(onSuccess: (String) -> Unit) {
        if (newPassword.length >= 6) {
            onSuccess(newPassword)
        } else {
            notificationMessage = "Şifre en az 6 karakter olmalı."
        }
    }

    fun clearNotification() {
        notificationMessage = null
    }
}