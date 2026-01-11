package com.example.yurt360.common.model

data class PasswordUpdateUiState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

