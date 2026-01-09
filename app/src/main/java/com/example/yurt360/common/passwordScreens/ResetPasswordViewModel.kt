package com.example.yurt360.common.passwordScreens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class ResetPasswordViewModel : ViewModel() {

    var email by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var notificationMessage by mutableStateOf<String?>(null)
        private set

    fun onEmailChange(input: String) {
        email = input
    }

    fun sendResetLink(onSuccess: (String) -> Unit) {
        if (email.isBlank()) {
            notificationMessage = "Lütfen e-posta adresinizi girin."
            return
        }

        viewModelScope.launch {
            isLoading = true
            try {
                delay(1500)
                isLoading = false
                notificationMessage = "Sıfırlama bağlantısı gönderildi"
                onSuccess(email.trim())
            } catch (e: Exception) {
                isLoading = false
                notificationMessage = "Bir hata oluştu"
            }
        }
    }

    fun clearNotification() {
        notificationMessage = null
    }
}