package com.example.yurt360.navigation

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.yurt360.admin.mainScreen.AdminHomeScreen
import com.example.yurt360.admin.mainScreen.AdminProfileScreen
import com.example.yurt360.common.components.*
import com.example.yurt360.common.model.Admin
import com.example.yurt360.common.model.User
import com.example.yurt360.common.passwordScreens.NewPasswordScreen
import com.example.yurt360.common.passwordScreens.PasswordUpdateScreen
import com.example.yurt360.common.passwordScreens.ResetPasswordScreen
import com.example.yurt360.user.mainScreen.ProfileScreen
import com.example.yurt360.user.mainScreen.UserHomeScreen
import com.example.yurt360.user.refectory.MenuScreen

object Routes {
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"

    // User Rotaları
    const val HOME = "home"
    const val PROFILE = "profile"
    const val MENU = "menu"

    // Admin Rotaları
    const val ADMIN_HOME = "admin_home"
    const val ADMIN_PROFILE = "admin_profile"

    // Ortak Rotalar
    const val SETTINGS = "settings"
    const val CALENDAR = "calendar"
    const val ABOUT_US = "about_us"
    const val UPDATE_PASSWORD = "update_password"
    const val NEW_PASSWORD = "new_password"
}

@Composable
fun RootNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    var currentUser by remember { mutableStateOf<User?>(null) }
    var currentAdmin by remember { mutableStateOf<Admin?>(null) }
    var isMenuOpen by remember { mutableStateOf(false) }
    val loginViewModel: LoginViewModel = viewModel()

    val isSessionChecked by loginViewModel.isSessionChecked.collectAsState()
    val loginState by loginViewModel.loginState.collectAsState()

    val announcementViewModel: AnnouncementViewModel = viewModel()

    LaunchedEffect(Unit) {
        loginViewModel.checkExistingSession()
    }

    if (!isSessionChecked) {
        return
    }

    val startRoute = when {
        currentAdmin != null -> Routes.ADMIN_HOME
        currentUser != null -> Routes.HOME
        else -> Routes.LOGIN
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.fillMaxSize()
        ) {
            // login
            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = loginViewModel,
                    onForgotPasswordClick = { navController.navigate(Routes.FORGOT_PASSWORD) },
                    onLoginSuccess = { topUser ->
                        when (topUser) {
                            is Admin -> {
                                currentAdmin = topUser
                                navController.navigate(Routes.ADMIN_HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                            is User -> {
                                currentUser = topUser
                                navController.navigate(Routes.HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                        }
                    }
                )
            }

            composable(Routes.FORGOT_PASSWORD) {
                ResetPasswordScreen(
                    onSendClick = { email ->
                        loginViewModel.resetPassword(
                            email = email,
                            onSuccess = {
                                Toast.makeText(context, "Sıfırlama bağlantısı gönderildi!", Toast.LENGTH_LONG).show()
                                navController.popBackStack()
                            },
                            onError = { msg -> Toast.makeText(context, msg, Toast.LENGTH_LONG).show() }
                        )
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // User
            composable(Routes.HOME) {
                currentUser?.let { user ->
                    UserHomeScreen(
                        user = user,
                        viewModel = announcementViewModel,
                        onMenuClick = { isMenuOpen = true },
                        onNavigation = { route -> handleNavigation(navController, route, isAdmin = false) }
                    )
                } ?: NavigateToLogin(navController)
            }

            composable(Routes.PROFILE) {
                currentUser?.let { user ->
                    ProfileScreen(
                        user = user,
                        onMenuClick = { isMenuOpen = true },
                        onNavigate = { route -> handleNavigation(navController, route, isAdmin = false) }
                    )
                } ?: NavigateToLogin(navController)
            }

            // Admin
            composable(Routes.ADMIN_HOME) {
                currentAdmin?.let { admin ->
                    AdminHomeScreen(
                        admin = admin,
                        viewModel = announcementViewModel,
                        onMenuClick = { isMenuOpen = true },
                        onNavigation = { route -> handleNavigation(navController, route, isAdmin = true) }
                    )
                } ?: NavigateToLogin(navController)
            }

            composable(Routes.ADMIN_PROFILE) {
                currentAdmin?.let { admin ->
                    AdminProfileScreen(
                        user = admin,
                        onMenuClick = { isMenuOpen = true },
                        onNavigate = { route -> handleNavigation(navController, route, isAdmin = true) }
                    )
                } ?: NavigateToLogin(navController)
            }

            // Ortak
            composable(Routes.CALENDAR) {
                CalendarScreen(
                    onNavigate = { route ->
                        val isAdmin = currentAdmin != null
                        handleNavigation(navController, route, isAdmin)
                    }
                )
            }

            composable(Routes.SETTINGS) {
                val locationInfo = currentUser?.location ?: currentAdmin?.let { "Yönetim Paneli" } ?: ""
                SettingsScreen(
                    userLocation = locationInfo,
                    onMenuClick = { isMenuOpen = true },
                    onNavigate = { route ->
                        val isAdmin = currentAdmin != null
                        handleNavigation(navController, route, isAdmin)
                    }
                )
            }

            composable(Routes.ABOUT_US) {
                AboutUsScreen(
                    onMenuClick = { isMenuOpen = true },
                    onNavigate = { route ->
                        val isAdmin = currentAdmin != null
                        handleNavigation(navController, route, isAdmin)
                    }
                )
            }

            composable(Routes.UPDATE_PASSWORD) {
                PasswordUpdateScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateHome = {
                        if (currentAdmin != null) navController.navigate(Routes.ADMIN_HOME)
                        else navController.navigate(Routes.HOME)
                    },
                    onNavigate = { route ->
                        val isAdmin = currentAdmin != null
                        handleNavigation(navController, route, isAdmin)
                    }
                )
            }

            composable(Routes.NEW_PASSWORD) {
                NewPasswordScreen(
                    onConfirmClick = { newPass ->
                        loginViewModel.updatePassword(
                            newPass = newPass,
                            onSuccess = { navController.navigate(Routes.LOGIN) },
                            onError = { /* Hata log */ }
                        )
                    }
                )
            }

            composable(Routes.MENU) {
                MenuScreen(
                    onNavigate = { route ->
                        val isAdmin = currentAdmin != null
                        handleNavigation(navController, route, isAdmin)
                    }
                )
            }
        }

        val menuUser = currentUser ?: currentAdmin?.let { admin ->
            User(id = admin.id, name = admin.name, surname = admin.surname, email = admin.email, phone = "", tc = "", gender = "", bloodType = "", birthDate = "", address = "", location = "", roomNo = "", image_url = "")
        }

        SideMenuView(
            isOpen = isMenuOpen,
            user = menuUser,
            onClose = { isMenuOpen = false },
            onNavigate = { route ->
                isMenuOpen = false
                val isAdmin = currentAdmin != null
                val target = when(route) {
                    "profile" -> "profile"
                    "about_us" -> Routes.ABOUT_US
                    "settings" -> Routes.SETTINGS
                    "update_password" -> Routes.UPDATE_PASSWORD
                    else -> route
                }
                handleNavigation(navController, target, isAdmin)
            },
            onLogout = {
                isMenuOpen = false
                currentUser = null
                currentAdmin = null
                loginViewModel.clearCredentials()
                navController.navigate(Routes.LOGIN) {
                    popUpTo(0) { inclusive = true }
                }
            }
        )
    }
}

fun handleNavigation(navController: NavController, route: String, isAdmin: Boolean) {
    val targetRoute = when (route) {
        "home" -> if (isAdmin) Routes.ADMIN_HOME else Routes.HOME
        "profile" -> if (isAdmin) Routes.ADMIN_PROFILE else Routes.PROFILE
        "calendar" -> Routes.CALENDAR
        "settings" -> Routes.SETTINGS
        "about_us" -> Routes.ABOUT_US
        "update_password" -> Routes.UPDATE_PASSWORD
        "menu" -> Routes.MENU
        else -> route
    }

    val currentRoute = navController.currentBackStackEntry?.destination?.route
    if (targetRoute == currentRoute) return

    navController.navigate(targetRoute) {
        if (route == "home" || route == "profile" || route == "calendar") {
            popUpTo(navController.graph.findStartDestination().id) {
                saveState = false
            }
        }
        launchSingleTop = true
        restoreState = false
    }
}

@Composable
fun NavigateToLogin(navController: NavController) {
    LaunchedEffect(Unit) {
        navController.navigate(Routes.LOGIN) {
            popUpTo(0) { inclusive = true }
        }
    }
}