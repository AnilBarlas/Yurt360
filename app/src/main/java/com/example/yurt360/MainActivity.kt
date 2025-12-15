package com.example.yurt360

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.common.components.LoginScreen
import com.example.yurt360.common.components.LoginViewModel
import com.example.yurt360.common.components.NewPasswordScreen
import com.example.yurt360.user.mainScreen.UserHomeScreen
import com.example.yurt360.common.model.Admin
import com.example.yurt360.common.model.TopUser
import com.example.yurt360.common.model.User
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.gotrue.handleDeeplinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Supabase Deep Link Kontrolü (Mailden gelen linki yakalar)
        val supabase = SupabaseClient.client
        supabase.handleDeeplinks(intent = intent)

        // Linkin türünü kontrol et: "reset-callback" mi?
        val isResetLink = intent?.data?.host == "reset-callback"

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: LoginViewModel = viewModel()

                    // Linkten geldiysek bu state TRUE başlar
                    var showNewPasswordScreen by remember { mutableStateOf(isResetLink) }

                    // Mevcut kullanıcı durumu
                    var currentUser by remember { mutableStateOf<TopUser?>(null) }

                    // EKRAN YÖNETİMİ
                    if (showNewPasswordScreen) {
                        //Şifre Yenileme Ekranı (Linkten gelindiyse)
                        NewPasswordScreen(
                            onConfirmClick = { newPass ->
                                viewModel.updatePassword(
                                    newPass = newPass,
                                    onSuccess = {
                                        Toast.makeText(this@MainActivity, "Şifreniz güncellendi! Giriş yapabilirsiniz.", Toast.LENGTH_LONG).show()
                                        showNewPasswordScreen = false // Login ekranına dön
                                    },
                                    onError = { msg ->
                                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        )
                    } else {
                        //Normal (Login veya Ana Sayfa)
                        if (currentUser == null) {
                            LoginScreen(
                                onLoginSuccess = { topUser ->
                                    currentUser = topUser
                                }
                            )
                        } else {
                            when (val user = currentUser) {
                                is Admin -> {
                                    //Admin ekranı buraya gelecek
                                }
                                is User -> {
                                    UserHomeScreen(user = user)
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }
    }
}