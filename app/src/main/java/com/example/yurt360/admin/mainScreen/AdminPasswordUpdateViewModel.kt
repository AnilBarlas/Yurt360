package com.example.yurt360.admin.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.common.model.PasswordUpdateUiState
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AdminPasswordUpdateViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PasswordUpdateUiState())
    val uiState: StateFlow<PasswordUpdateUiState> = _uiState.asStateFlow()

    fun onCurrentPasswordChange(newValue: String) {
        _uiState.update { it.copy(currentPassword = newValue) }
    }

    fun onNewPasswordChange(newValue: String) {
        _uiState.update { it.copy(newPassword = newValue, errorMessage = null) }
    }

    fun updatePassword() {
        val currentState = _uiState.value

        // Basit validasyonlar
        if (currentState.newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "Şifre en az 6 karakter olmalıdır.") }
            return
        }

        if (currentState.currentPassword.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Lütfen mevcut şifrenizi giriniz.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                val auth = SupabaseClient.client.auth
                val currentUser = auth.currentUserOrNull()
                val userEmail = currentUser?.email

                if (userEmail == null) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Yönetici oturumu bulunamadı.")
                    }
                    return@launch
                }

                try {
                    auth.signInWith(Email) {
                        email = userEmail
                        password = currentState.currentPassword
                    }
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = "Mevcut şifre hatalı.")
                    }
                    return@launch
                }

                auth.updateUser {
                    password = currentState.newPassword
                }

                _uiState.update { it.copy(isLoading = false, isSuccess = true) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Şifre güncellenirken bir hata oluştu")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = PasswordUpdateUiState()
    }
}