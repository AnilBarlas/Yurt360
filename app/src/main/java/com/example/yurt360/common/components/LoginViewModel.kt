package com.example.yurt360.common.components

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.common.model.Admin
import com.example.yurt360.common.model.TopUser
import com.example.yurt360.common.model.User
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
    private val _userMail = MutableStateFlow("")
    val userMail: StateFlow<String> = _userMail.asStateFlow()

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState.asStateFlow()

    fun onUsernameChange(newText: String) { _userMail.value = newText }
    fun onPasswordChange(newText: String) { _password.value = newText }

    fun onLoginClick() {
        val emailInput = _userMail.value.trim()
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
                    val userId = currentUser.id

                    val profile = supabase.from("users")
                        .select {
                            filter {
                                eq("id", userId)
                            }
                        }.decodeSingleOrNull<ProfileDto>()

                    if (profile != null) {
                        Log.d("LoginTypeControl", "Gelen Veri (Raw): ${profile.isAdmin}")
                        Log.d("LoginTypeControl", "Kullanıcı ID: ${profile.id}")
                        Log.d("LoginTypeControl", "Kullanıcı Name: ${profile.name}")
                        Log.d("LoginTypeControl", "Kullanıcı Surname: ${profile.surname}")
                        Log.d("LoginTypeControl", "Kullanıcı Gender: ${profile.gender}")
                        val topUser = profile.toTopUser()
                        Log.d("LoginTypeControl", "Oluşan Sınıf: ${topUser::class.java.simpleName}")
                        _loginState.value = LoginState.Success(topUser)
                    } else {
                        Log.e("LoginTypeControl", "HATA: Profil null geldi!")
                        _loginState.value = LoginState.Error("Kullanıcı profili bulunamadı.")
                    }
                } else {
                    _loginState.value = LoginState.Error("Giriş başarısız oldu.")
                }

            } catch (e: Exception) {
                Log.e("SupabaseLogin", "Error: ${e.message}")
                val msg = if (e.message?.contains("Invalid login") == true)
                    "Hatalı e-posta veya şifre."
                else "Bir hata oluştu: ${e.message}"
                _loginState.value = LoginState.Error(msg)
            }
        }
    }

    fun resetLoginState() { _loginState.value = LoginState.Idle }
}