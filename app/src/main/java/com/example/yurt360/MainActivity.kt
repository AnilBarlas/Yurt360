package com.example.yurt360

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.common.components.*
import com.example.yurt360.user.mainScreen.*
import com.example.yurt360.admin.mainScreen.AdminHomeScreen
import com.example.yurt360.common.model.Admin
import com.example.yurt360.common.model.TopUser
import com.example.yurt360.common.model.User
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.gotrue.handleDeeplinks
import com.example.yurt360.admin.mainScreen.AdminProfileScreen
import com.example.yurt360.user.changeRoom.ApplicationsScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val supabase = SupabaseClient.client
        supabase.handleDeeplinks(intent = intent)

        val isResetLink = intent?.data?.host == "reset-callback"

        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    val loginViewModel: LoginViewModel = viewModel()

                    var showNewPasswordScreen by remember { mutableStateOf(isResetLink) }
                    var currentUser by remember { mutableStateOf<TopUser?>(null) }
                    var currentScreenRoute by remember { mutableStateOf("home") }
                    var isMenuOpen by remember { mutableStateOf(false) }


                    if (showNewPasswordScreen) {
                        NewPasswordScreen(
                            onConfirmClick = { newPass ->
                                loginViewModel.updatePassword(
                                    newPass = newPass,
                                    onSuccess = {
                                        Toast.makeText(this@MainActivity, "Şifre güncellendi!", Toast.LENGTH_LONG).show()
                                        showNewPasswordScreen = false
                                    },
                                    onError = { msg -> Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show() }
                                )
                            }
                        )
                    } else {
                        if (currentUser == null) {
                            LoginScreen(
                                viewModel = loginViewModel,
                                onLoginSuccess = { topUser ->
                                    currentUser = topUser
                                    currentScreenRoute = "home"
                                }
                            )
                        } else {
                            // Yan menü
                            SideMenuView(
                                isOpen = isMenuOpen,
                                user = currentUser as? User, // Admin için null gidebilir, sorun yok.
                                onClose = { isMenuOpen = false },
                                onNavigate = { route ->
                                    currentScreenRoute = route
                                    isMenuOpen = false
                                },
                                onLogout = {
                                    loginViewModel.clearCredentials()
                                    currentUser = null
                                    isMenuOpen = false
                                    currentScreenRoute = "home"
                                }
                            )

                            Box(modifier = Modifier.fillMaxSize()) {
                                when (val user = currentUser) {
                                    // --- USER BLOĞU ---
                                    is User -> {
                                        when (currentScreenRoute) {
                                            "home" -> UserHomeScreen(
                                                user = user,
                                                onMenuClick = { isMenuOpen = true },
                                                onNavigation = { currentScreenRoute = it }
                                            )
                                            "profile" -> ProfileScreen(
                                                user = user,
                                                onNavigate = { currentScreenRoute = it },
                                                onMenuClick = { isMenuOpen = true }
                                            )
                                            "applications" -> ApplicationsScreen(
                                                onNavigate = { currentScreenRoute = it }
                                            )


                                            "update_password" -> PasswordUpdateScreen(
                                                onNavigateBack = { currentScreenRoute = "profile" },
                                                onNavigateHome = { currentScreenRoute = "home" },
                                                onNavigate = { currentScreenRoute = it },
                                                onUpdatePassword = { newPass, callback ->
                                                    loginViewModel.updatePassword(newPass, { callback(null) }, { callback(it) })
                                                }
                                            )
                                            "calendar" -> CalendarScreen(
                                                onNavigate = { currentScreenRoute = it },
                                            )
                                            "about_us" -> AboutUsScreen(
                                                onMenuClick = { isMenuOpen = true },
                                                onNavigate = { currentScreenRoute = it }
                                            )
                                            "settings" -> SettingsScreen(
                                                onNavigateBack = {
                                                    currentScreenRoute = "home"
                                                }
                                            )
                                        }
                                    }

                                    // --- ADMIN BLOĞU ---
                                    is Admin -> {
                                        when (currentScreenRoute) {
                                            "home" -> AdminHomeScreen(
                                                admin = user,
                                                onMenuClick = { isMenuOpen = true },
                                                onNavigation = { currentScreenRoute = it }
                                            )
                                            "profile" -> AdminProfileScreen(
                                                user = user,
                                                onNavigate = { currentScreenRoute = it },
                                                onMenuClick = { isMenuOpen = true }
                                            )
                                            "update_password" -> PasswordUpdateScreen(
                                                onNavigateBack = { currentScreenRoute = "profile" },
                                                onNavigateHome = { currentScreenRoute = "home" },
                                                onNavigate = { currentScreenRoute = it },
                                                onUpdatePassword = { newPass, callback ->
                                                    loginViewModel.updatePassword(newPass, { callback(null) }, { callback(it) })
                                                }
                                            )
                                            "calendar" -> CalendarScreen(
                                                onNavigate = { currentScreenRoute = it },
                                            )
                                            "about_us" -> AboutUsScreen(
                                                onMenuClick = { isMenuOpen = true },
                                                onNavigate = { currentScreenRoute = it }
                                            )
                                            "settings" -> SettingsScreen(
                                                onNavigateBack = {
                                                    currentScreenRoute = "home"
                                                }
                                            )
                                            else -> AdminHomeScreen(
                                                admin = user,
                                                onMenuClick = { isMenuOpen = true },
                                                onNavigation = { currentScreenRoute = it }
                                            )
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
}