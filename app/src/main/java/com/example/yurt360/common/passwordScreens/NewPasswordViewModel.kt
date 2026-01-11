package com.example.yurt360.common.passwordScreens

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.launch

class NewPasswordViewModel : ViewModel() {

    var newPassword by mutableStateOf("")
        private set

    var isLoading by mutableStateOf(false)
        private set

    var notificationMessage by mutableStateOf<String?>(null)
        private set

    fun onPasswordChange(input: String) {
        newPassword = input
    }

    fun submitPassword(onSuccess: (String) -> Unit) {
        if (newPassword.isBlank() || newPassword.length < 6) {
            notificationMessage = "Şifre en az 6 karakter olmalıdır."
            return
        }

        viewModelScope.launch {
            isLoading = true

            // Kontrol: Oturum gerçekten var mı?
            val session = SupabaseClient.client.auth.currentSessionOrNull()
            if (session == null) {
                isLoading = false
                notificationMessage = "Oturum süresi dolmuş veya geçersiz. Lütfen tekrar şifre sıfırlama bağlantısı isteyin."
                return@launch
            }

            try {
                SupabaseClient.client.auth.updateUser {
                    password = newPassword
                }

                isLoading = false
                notificationMessage = "Şifreniz başarıyla güncellendi"
                onSuccess(newPassword)
            } catch (e: Exception) {
                isLoading = false
                notificationMessage = "Güncelleme başarısız: ${e.localizedMessage ?: "Bilinmeyen hata"}"
            }
        }
    }

    fun clearNotification() {
        notificationMessage = null
    }
}