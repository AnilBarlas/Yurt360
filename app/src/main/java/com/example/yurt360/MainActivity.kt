package com.example.yurt360

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.common.components.CustomBottomNavigationBar
import com.example.yurt360.common.components.LoginScreen
import com.example.yurt360.common.components.LoginViewModel
import com.example.yurt360.common.components.NewPasswordScreen
import com.example.yurt360.user.mainScreen.UserHomeScreen
import com.example.yurt360.user.mainScreen.ProfileScreen
import com.example.yurt360.common.model.Admin
import com.example.yurt360.common.model.TopUser
import com.example.yurt360.common.model.User
import com.example.yurt360.data.api.SupabaseClient
import com.example.yurt360.user.mainScreen.CalendarScreen
import com.example.yurt360.user.mainScreen.PasswordUpdateScreen
import io.github.jan.supabase.gotrue.handleDeeplinks

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val supabase = SupabaseClient.client
        supabase.handleDeeplinks(intent = intent)

        val isResetLink = intent?.data?.host == "reset-callback"

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: LoginViewModel = viewModel()

                    var showNewPasswordScreen by remember { mutableStateOf(isResetLink) }
                    var currentUser by remember { mutableStateOf<TopUser?>(null) }

                    var currentScreenRoute by remember { mutableStateOf("home") }

                    if (showNewPasswordScreen) {
                        NewPasswordScreen(
                            onConfirmClick = { newPass ->
                                viewModel.updatePassword(
                                    newPass = newPass,
                                    onSuccess = {
                                        Toast.makeText(this@MainActivity, "Şifreniz güncellendi! Giriş yapabilirsiniz.", Toast.LENGTH_LONG).show()
                                        showNewPasswordScreen = false
                                    },
                                    onError = { msg ->
                                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                                    }
                                )
                            }
                        )
                    } else {
                        if (currentUser == null) {
                            LoginScreen(
                                onLoginSuccess = { topUser ->
                                    currentUser = topUser
                                    currentScreenRoute = "home"
                                }
                            )
                        } else {
                            when (val user = currentUser) {
                                is Admin -> {
                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Text("Admin Paneli")
                                    }
                                }
                                is User -> {
                                    when (currentScreenRoute) {
                                        "home" -> {
                                            UserHomeScreen(
                                                user = user,
                                                onMenuClick = { menuTitle ->
                                                    Toast.makeText(this@MainActivity, "$menuTitle seçildi", Toast.LENGTH_SHORT).show()
                                                },
                                                onNavigation = { route ->
                                                    currentScreenRoute = route
                                                }
                                            )
                                        }
                                        "profile" -> {
                                            ProfileScreen(
                                                user = user,
                                                onNavigate = { route ->
                                                    currentScreenRoute = route
                                                }
                                            )
                                        }
                                        "update_password" -> {
                                            PasswordUpdateScreen(
                                                onNavigateBack = {
                                                    currentScreenRoute = "profile"
                                                },
                                                onNavigateHome = {
                                                    currentScreenRoute = "home"
                                                },
                                                onNavigate = { route ->
                                                    currentScreenRoute = route
                                                },
                                                onUpdatePassword = { newPassword, resultCallback ->
                                                    viewModel.updatePassword(
                                                        newPass = newPassword,
                                                        onSuccess = {
                                                            resultCallback(null)
                                                        },
                                                        onError = { errorMsg ->
                                                            resultCallback(errorMsg)
                                                        }
                                                    )
                                                }
                                            )
                                        }
                                        "calendar" -> {
                                            CalendarScreen(
                                                onNavigate = { route ->
                                                    currentScreenRoute = route
                                                }
                                            )
                                        }
                                        else -> {
                                            currentScreenRoute = "home"
                                        }
                                    }
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