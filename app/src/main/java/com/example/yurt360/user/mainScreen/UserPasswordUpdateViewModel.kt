package com.example.yurt360.user.mainScreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class PasswordUpdateUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class UserPasswordUpdateViewModel : ViewModel() {

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

        if (currentState.newPassword.length < 6) {
            _uiState.update { it.copy(errorMessage = "Şifre en az 6 karakter olmalıdır.") }
            return
        }

        _uiState.update { it.copy(isLoading = true, errorMessage = null) }

        viewModelScope.launch {
            try {
                delay(1500)

                _uiState.update { it.copy(isLoading = false, isSuccess = true) }

            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isLoading = false, errorMessage = e.message ?: "Bir hata oluştu")
                }
            }
        }
    }

    fun resetState() {
        _uiState.value = PasswordUpdateUiState()
    }
}