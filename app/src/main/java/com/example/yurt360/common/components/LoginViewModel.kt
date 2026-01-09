package com.example.yurt360.common.components

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.common.model.TopUser
import com.example.yurt360.data.api.ProfileDto
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class LoginState {
    object Idle : LoginState()
    object Loading : LoginState()
    data class Success(val user: TopUser) : LoginState()
    data class Error(val message: String) : LoginState()
}

class LoginViewModel : ViewModel() {
    private val _username = MutableStateFlow("")
    val username: StateFlow<String> = _username.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun onUsernameChange(newText: String) { _username.value = newText }
    fun onPasswordChange(newText: String) { _password.value = newText }

    fun clearCredentials() {
        _username.value = ""
        _password.value = ""
        _loginState.value = LoginState.Idle

        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.signOut()
            } catch (e: Exception) {
                Log.e("LogoutError", "Çıkış yapılırken hata: ${e.message}")
            }
        }
    }

    fun onLoginClick() {
        val emailInput = _username.value.trim()
        val passInput = _password.value.trim()

        if (emailInput.isBlank() || passInput.isBlank()) {
            _loginState.value = LoginState.Error("Lütfen alanları doldurun.")
            return
        }

        _loginState.value = LoginState.Loading

        viewModelScope.launch {
            try {
                val supabase = SupabaseClient.client
                supabase.auth.signInWith(Email) {
                    email = emailInput
                    password = passInput
                }

                val currentUser = supabase.auth.currentUserOrNull()
                if (currentUser != null) {
                    val profile = supabase.from("users")
                        .select { filter { eq("id", currentUser.id) } }
                        .decodeSingleOrNull<ProfileDto>()

                    if (profile != null) {
                        _loginState.value = LoginState.Success(profile.toTopUser())
                    } else {
                        _loginState.value = LoginState.Error("Kullanıcı profili bulunamadı.")
                    }
                } else {
                    _loginState.value = LoginState.Error("Giriş başarısız.")
                }
            } catch (e: Exception) {
                val msg = if (e.message?.contains("Invalid login") == true) "Hatalı e-posta veya şifre." else "Hata: ${e.message}"
                _loginState.value = LoginState.Error(msg)
            }
        }
    }

    fun resetLoginState() { _loginState.value = LoginState.Idle }

    fun resetPassword(email: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.resetPasswordForEmail(email = email, redirectUrl = "com.example.yurt360://reset-callback")
                onSuccess()
            } catch (e: Exception) { onError(e.message ?: "Hata") }
        }
    }

    fun updatePassword(newPass: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                SupabaseClient.client.auth.modifyUser { password = newPass }
                onSuccess()
            } catch (e: Exception) { onError(e.message ?: "Hata") }
        }
    }
}