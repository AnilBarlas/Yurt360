package com.example.yurt360.navigation

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
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.yurt360.admin.mainScreen.*
import com.example.yurt360.common.components.*
import com.example.yurt360.common.model.Admin
import com.example.yurt360.common.model.User
import com.example.yurt360.common.passwordScreens.NewPasswordScreen
import com.example.yurt360.common.passwordScreens.ResetPasswordScreen
import com.example.yurt360.user.mainScreen.*
import com.example.yurt360.user.refectory.MenuScreen

object Routes {
    const val LOGIN = "login"
    const val FORGOT_PASSWORD = "forgot_password"
    const val NEW_PASSWORD = "new_password"

    // User Rotaları
    const val USER_HOME = "user_home"
    const val USER_PROFILE = "user_profile"
    const val USER_MENU = "user_menu"
    const val USER_CALENDAR = "calendar"
    const val USER_STUDY = "user_study_area"
    const val USER_LAUNDRY = "user_laundry"
    const val USER_APPLICATIONS = "user_applications"

    const val USER_SETTINGS = "user_settings"
    const val USER_ABOUT_US = "user_about_us"
    const val USER_UPDATE_PASSWORD = "user_update_password"

    // Admin Rotaları
    const val ADMIN_HOME = "admin_home"
    const val ADMIN_PROFILE = "admin_profile"
    const val ADMIN_MENU = "admin_menu"
    const val ADMIN_LAUNDRY = "admin_laundry"
    const val ADMIN_APPLICATIONS = "admin_applications"
    const val ADD_ANNOUNCEMENT = "add_announcement"

    const val ADMIN_SETTINGS = "admin_settings"
    const val ADMIN_ABOUT_US = "admin_about_us"
    const val ADMIN_UPDATE_PASSWORD = "admin_update_password"
}

@Composable
fun RootNavigation() {
    val navController = rememberNavController()
    val context = LocalContext.current

    var currentUser by remember { mutableStateOf<User?>(null) }
    var currentAdmin by remember { mutableStateOf<Admin?>(null) }
    var isMenuOpen by remember { mutableStateOf(false) }

    val loginViewModel: LoginViewModel = viewModel()
    val announcementViewModel: AnnouncementViewModel = viewModel()


    val isSessionChecked by loginViewModel.isSessionChecked.collectAsState()
    val loginState by loginViewModel.loginState.collectAsState()

    LaunchedEffect(Unit) {
        loginViewModel.checkExistingSession()
    }

    LaunchedEffect(isSessionChecked, loginState) {
        if (isSessionChecked && currentUser == null && currentAdmin == null) {
            if (loginState is LoginState.Success) {
                val topUser = (loginState as LoginState.Success).user
                when (topUser) {
                    is Admin -> {
                        currentAdmin = topUser
                        currentUser = null
                    }
                    is User -> {
                        currentUser = topUser
                        currentAdmin = null
                    }
                }
            }
        }
    }

    if (!isSessionChecked) return

    val startRoute = when {
        currentAdmin != null -> Routes.ADMIN_HOME
        currentUser != null -> Routes.USER_HOME
        else -> Routes.LOGIN
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.fillMaxSize()
        ) {
            // --- GİRİŞ (AUTH) ---
            composable(Routes.LOGIN) {
                LoginScreen(
                    viewModel = loginViewModel,
                    onForgotPasswordClick = { navController.navigate(Routes.FORGOT_PASSWORD) },
                    onLoginSuccess = { topUser ->
                        when (topUser) {
                            is Admin -> {
                                currentAdmin = topUser
                                currentUser = null
                                navController.navigate(Routes.ADMIN_HOME) {
                                    popUpTo(Routes.LOGIN) { inclusive = true }
                                }
                            }
                            is User -> {
                                currentUser = topUser
                                currentAdmin = null
                                navController.navigate(Routes.USER_HOME) {
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
                        // İsteğinize uygun olarak işlem bitince Login ekranına yönlendiriyoruz.
                        Toast.makeText(context, "Sıfırlama bağlantısı gönderildi. Lütfen e-postanızı kontrol edin.", Toast.LENGTH_LONG).show()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // DEEP LINK EKLENMİŞ ALAN
            composable(
                route = Routes.NEW_PASSWORD,
                deepLinks = listOf(
                    navDeepLink {
                        // Maildeki link formatınız: https://www.yurt360.com/new_password
                        // Bu linke tıklandığında uygulama açılacak ve bu ekrana düşecektir.
                        uriPattern = "https://www.yurt360.com/new_password"
                    }
                )
            ) {
                NewPasswordScreen(
                    onConfirmClick = { newPassword ->
                        Toast.makeText(context, "Şifreniz başarıyla güncellendi.", Toast.LENGTH_SHORT).show()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    }
                )
            }

            // --- KULLANICI (USER) BÖLÜMÜ ---
            composable(Routes.USER_HOME) {
                currentUser?.let { user ->
                    UserHomeScreen(
                        user = user,
                        viewModel = announcementViewModel,
                        onMenuClick = { isMenuOpen = true },
                        onNavigation = { handleUserNavigation(navController, it) }
                    )
                } ?: NavigateToLogin(navController)
            }

            composable(Routes.USER_PROFILE) {
                currentUser?.let { user ->
                    ProfileScreen(
                        user = user,
                        onMenuClick = { isMenuOpen = true },
                        onNavigate = { handleUserNavigation(navController, it) }
                    )
                } ?: NavigateToLogin(navController)
            }

            composable(Routes.USER_MENU) {
                MenuScreen(onNavigate = { handleUserNavigation(navController, it) })
            }

            composable(Routes.USER_CALENDAR) {
                CalendarScreen(onNavigate = { handleUserNavigation(navController, it) })
            }

            composable(Routes.USER_SETTINGS) {
                UserSettingsScreen(
                    userLocation = currentUser?.location ?: "Bilinmiyor",
                    onMenuClick = { isMenuOpen = true },
                    onNavigate = { handleUserNavigation(navController, it) }
                )
            }

            composable(Routes.USER_ABOUT_US) {
                UserAboutUsScreen(
                    onMenuClick = { isMenuOpen = true },
                    onNavigate = { handleUserNavigation(navController, it) }
                )
            }

            composable(Routes.USER_UPDATE_PASSWORD) {
                UserPasswordUpdateScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateHome = {
                        handleUserNavigation(navController, Routes.USER_HOME)
                    },
                    onNavigate = { handleUserNavigation(navController, it) }
                )
            }

            // --- YÖNETİCİ (ADMIN) BÖLÜMÜ ---
            composable(Routes.ADMIN_HOME) {
                currentAdmin?.let { admin ->
                    AdminHomeScreen(
                        admin = admin,
                        viewModel = announcementViewModel,
                        onMenuClick = { isMenuOpen = true },
                        onNavigation = { handleAdminNavigation(navController, it) }
                    )
                } ?: NavigateToLogin(navController)
            }

            composable(Routes.ADMIN_PROFILE) {
                currentAdmin?.let { admin ->
                    val adminUser = Admin(id = admin.id, name = admin.name, surname = admin.surname, email = admin.email)
                    AdminProfileScreen(
                        admin = adminUser,
                        onMenuClick = { isMenuOpen = true },
                        onNavigate = { handleAdminNavigation(navController, it) }
                    )
                } ?: NavigateToLogin(navController)
            }

            // Duyuru Ekleme Ekranı/Dialog'u
            dialog(Routes.ADD_ANNOUNCEMENT) {
                AnnouncementDialog(
                    onDismiss = {
                        navController.popBackStack()
                    },
                    onConfirm = { title, description ->
                        announcementViewModel.addAnnouncement(title, description) {
                            navController.popBackStack()
                        }
                    }
                )
            }

            composable(Routes.ADMIN_SETTINGS) {
                AdminSettingsScreen(
                    onMenuClick = { isMenuOpen = true },
                    onNavigate = { handleAdminNavigation(navController, it) }
                )
            }

            composable(Routes.ADMIN_ABOUT_US) {
                AdminAboutUsScreen(
                    onMenuClick = { isMenuOpen = true },
                    onNavigate = { handleAdminNavigation(navController, it) }
                )
            }

            composable(Routes.ADMIN_UPDATE_PASSWORD) {
                AdminPasswordUpdateScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateHome = {
                        handleAdminNavigation(navController, Routes.ADMIN_HOME)
                    },
                    onNavigate = { handleAdminNavigation(navController, it) }
                )
            }
        }

        // --- YAN MENÜLER ---
        if (currentAdmin != null) {
            val adminToUser = User(id = currentAdmin!!.id, name = currentAdmin!!.name, surname = currentAdmin!!.surname, email = currentAdmin!!.email, phone = "", tc = "", gender = "", bloodType = "", birthDate = "", address = "", location = "", roomNo = "", image_url = "")
            AdminSideMenuView(
                isOpen = isMenuOpen,
                user = adminToUser,
                onClose = { isMenuOpen = false },
                onNavigate = { handleAdminNavigation(navController, it); isMenuOpen = false },
                onLogout = {
                    isMenuOpen = false
                    currentAdmin = null
                    loginViewModel.clearCredentials()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) }
                }
            )
        } else if (currentUser != null) {
            SideMenuView(
                isOpen = isMenuOpen,
                user = currentUser,
                onClose = { isMenuOpen = false },
                onNavigate = { handleUserNavigation(navController, it); isMenuOpen = false },
                onLogout = {
                    isMenuOpen = false
                    currentUser = null
                    loginViewModel.clearCredentials()
                    navController.navigate(Routes.LOGIN) { popUpTo(0) }
                }
            )
        }
    }
}

// --- NAVİGASYON YÖNETİCİLERİ ---

fun handleUserNavigation(navController: NavController, route: String) {
    val target = when (route) {
        "user_home" -> Routes.USER_HOME
        "user_profile" -> Routes.USER_PROFILE
        "user_menu", "menu" -> Routes.USER_MENU
        "calendar" -> Routes.USER_CALENDAR
        "user_study_area" -> Routes.USER_STUDY
        "user_laundry" -> Routes.USER_LAUNDRY
        "user_applications" -> Routes.USER_APPLICATIONS
        "user_settings" -> Routes.USER_SETTINGS
        "user_update_password" -> Routes.USER_UPDATE_PASSWORD
        "user_about_us" -> Routes.USER_ABOUT_US
        else -> route
    }
    executeSafeNavigation(navController, target)
}

fun handleAdminNavigation(navController: NavController, route: String) {
    val target = when (route) {
        "admin_home" -> Routes.ADMIN_HOME
        "admin_profile" -> Routes.ADMIN_PROFILE
        "admin_menu" -> Routes.ADMIN_MENU
        "admin_laundry" -> Routes.ADMIN_LAUNDRY
        "admin_applications" -> Routes.ADMIN_APPLICATIONS
        "add_announcement" -> Routes.ADD_ANNOUNCEMENT
        "admin_settings" -> Routes.ADMIN_SETTINGS
        "admin_update_password" -> Routes.ADMIN_UPDATE_PASSWORD
        "admin_about_us" -> Routes.ADMIN_ABOUT_US
        else -> route
    }
    executeSafeNavigation(navController, target)
}

private fun executeSafeNavigation(navController: NavController, targetRoute: String) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    if (targetRoute == currentRoute &&
        targetRoute != Routes.USER_HOME &&
        targetRoute != Routes.ADMIN_HOME) {
        return
    }

    navController.navigate(targetRoute) {
        popUpTo(navController.graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
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