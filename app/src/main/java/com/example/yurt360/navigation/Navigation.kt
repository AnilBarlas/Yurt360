package com.example.yurt360.navigation

import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.yurt360.R
import com.example.yurt360.admin.changeRoom.AdminApplicationsScreen
import com.example.yurt360.admin.changeRoom.AdminApplicationsViewModel
import com.example.yurt360.admin.mainScreen.*
import com.example.yurt360.admin.refectory.AdminMenuScreen
import com.example.yurt360.common.components.*
import com.example.yurt360.common.model.Admin
import com.example.yurt360.common.model.User
import com.example.yurt360.common.passwordScreens.NewPasswordScreen
import com.example.yurt360.common.passwordScreens.ResetPasswordScreen
import com.example.yurt360.user.mainScreen.*
import com.example.yurt360.user.refectory.MenuScreen
import com.example.yurt360.data.api.SupabaseClient
import com.example.yurt360.user.workSpace.WorkSpace1_Kuzey1
import com.example.yurt360.user.changeRoom.ApplicationsScreen
import com.example.yurt360.user.laundry.Laundry1_1_CamasirKuzey1
import com.example.yurt360.user.laundry.Laundry1_2_CamasirKuzey2
import com.example.yurt360.user.laundry.Laundry1_3_CamasirMeydan
import com.example.yurt360.user.laundry.Laundry1_4_CamasirAltguney
import com.example.yurt360.user.laundry.Laundry1_5_CamasirErkek
import com.example.yurt360.user.laundry2.Laundry2_1_KurutmaKuzey1
import com.example.yurt360.user.laundry2.Laundry2_2_KurutmaKuzey2
import com.example.yurt360.user.laundry2.Laundry2_3_KurutmaMeydan
import com.example.yurt360.user.laundry2.Laundry2_4_KurutmaAltguney
import com.example.yurt360.user.laundry2.Laundry2_5_KurutmaErkek
import com.example.yurt360.user.workSpace.WorkSpace2_Kuzey2
import com.example.yurt360.user.workSpace.WorkSpace3_Meydan
import com.example.yurt360.user.workSpace.WorkSpace4_Altguney
import com.example.yurt360.user.workSpace.WorkSpace5_Erkek
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.handleDeeplinks
import io.github.jan.supabase.gotrue.user.UserSession
import java.util.Locale

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
    const val USER_DRY = "user_dry"
    const val USER_LAUNDRYMAIN = "user_laundrymain"
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
fun RootNavigation(currentIntent: Intent?) {
    val navController = rememberNavController()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Intent verisini kontrol et (Deep Link var mı?)
    // MainActivity'den gelen currentIntent parametresini kullanıyoruz.
    val intentData = currentIntent?.data

    // Linkin "new_password" hostuna sahip olup olmadığını kontrol ediyoruz
    val isPasswordResetLink = intentData?.scheme == "yurt360" && intentData?.host == "new_password"

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

    // Uygulama açıkken yeni bir Intent gelirse (onNewIntent) ve bu bir şifre sıfırlama linkiyse,
    // Supabase session işlemini başlat ve yönlendirme yap.
    LaunchedEffect(currentIntent) {
        currentIntent?.let { intent ->
            try {
                // 1. Önce standart kütüphane yöntemini dene
                SupabaseClient.client.handleDeeplinks(intent)

                // 2. Eğer kütüphane otomatik alamazsa (Android Fragment sorunu için) manuel parse et
                val data = intent.data
                // Oturum yoksa veya sadece deep link ile gelindiyse kontrol et
                if (data != null) {
                    // URL içindeki fragment (#) kısmını al: access_token=...&refresh_token=...
                    val fragment = data.fragment
                    if (!fragment.isNullOrEmpty() && fragment.contains("access_token")) {
                        // Basit bir parametre ayıklama işlemi
                        val params = fragment.split("&").associate {
                            val parts = it.split("=")
                            if (parts.size == 2) parts[0] to parts[1] else "" to ""
                        }

                        val accessToken = params["access_token"]
                        val refreshToken = params["refresh_token"] ?: ""

                        if (!accessToken.isNullOrEmpty()) {
                            SupabaseClient.client.auth.importSession(
                                UserSession(
                                    accessToken = accessToken,
                                    refreshToken = refreshToken,
                                    expiresIn = 3600, // Varsayılan süre
                                    tokenType = "bearer",
                                    user = null // User objesi başta null
                                )
                            )

                            try {
                                SupabaseClient.client.auth.retrieveUserForCurrentSession(updateSession = true)
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (isPasswordResetLink) {
            // Eğer zaten oradaysak tekrar yönlendirme yapma
            if (navController.currentDestination?.route != Routes.NEW_PASSWORD) {
                navController.navigate(Routes.NEW_PASSWORD) {
                    launchSingleTop = true
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {


        val startRoute = when {
            isPasswordResetLink -> Routes.NEW_PASSWORD
            currentAdmin != null -> Routes.ADMIN_HOME
            currentUser != null -> Routes.USER_HOME
            else -> Routes.LOGIN
        }

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
                        Toast.makeText(context, "Sıfırlama bağlantısı gönderildi. Lütfen e-postanızı kontrol edin.", Toast.LENGTH_LONG).show()
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.LOGIN) { inclusive = true }
                        }
                    },
                    onBackClick = { navController.popBackStack() }
                )
            }

            // GÜNCELLENMİŞ DEEP LINK ALANI
            composable(
                route = Routes.NEW_PASSWORD,
                deepLinks = listOf(
                    navDeepLink {
                        uriPattern = "yurt360://new_password"
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

            composable(Routes.USER_LAUNDRYMAIN) {
                currentUser?.let { user ->
                    Laundry(
                        onNavigation = { handleUserNavigation(navController, it) }
                    )
                } ?: NavigateToLogin(navController)
            }

            composable(Routes.USER_LAUNDRY) {
                currentUser?.let { user ->
                    val location = user.location.lowercase(Locale("tr", "TR"))
                    when {
                        location.contains("kuzey") && location.contains("1") -> {
                            Laundry1_1_CamasirKuzey1(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                        location.contains("kuzey") && location.contains("2") -> {
                            Laundry1_2_CamasirKuzey2(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                        location.contains("meydan") -> {
                            Laundry1_3_CamasirMeydan(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                        location.contains("alt") -> {
                            Laundry1_4_CamasirAltguney(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                        else -> {
                            Laundry1_5_CamasirErkek(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                    }
                } ?: NavigateToLogin(navController)
            }

            composable(Routes.USER_DRY) {
                currentUser?.let { user ->
                    val location = user.location.lowercase(Locale("tr", "TR"))
                    when {
                        location.contains("kuzey") && location.contains("1") -> {
                            Laundry2_1_KurutmaKuzey1(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                        location.contains("kuzey") && location.contains("2") -> {
                            Laundry2_2_KurutmaKuzey2(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                        location.contains("meydan") -> {
                            Laundry2_3_KurutmaMeydan(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                        location.contains("alt") -> {
                            Laundry2_4_KurutmaAltguney(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                        else -> {
                            Laundry2_5_KurutmaErkek(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                    }
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

            // GÜNCELLENEN KISIM: CalendarScreen'e geri butonu fonksiyonu eklendi
            composable(Routes.USER_CALENDAR) {
                CalendarScreen(
                    onNavigate = { handleUserNavigation(navController, it) },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Routes.USER_STUDY) {
                currentUser?.let { user ->
                    val location = user.location.lowercase(Locale("tr", "TR"))
                    when {
                        location.contains("kuzey") && location.contains("1") -> {
                            WorkSpace1_Kuzey1(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                        location.contains("kuzey") && location.contains("2") -> {
                            WorkSpace2_Kuzey2(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                        location.contains("meydan") -> {
                            WorkSpace3_Meydan(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                        location.contains("alt") -> {
                            WorkSpace4_Altguney(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                        else -> {
                            WorkSpace5_Erkek(
                                onNavigateHome = { handleUserNavigation(navController, Routes.USER_HOME) },
                                onNavigation = { handleUserNavigation(navController, it) },
                                user = user
                            )
                        }
                    }
                } ?: NavigateToLogin(navController)
            }

            // --- YENİ EKLENEN KISIM: Başvurular Ekranı ---
            composable(Routes.USER_APPLICATIONS) {
                ApplicationsScreen(
                    onNavigate = { handleUserNavigation(navController, it) }
                )
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
                    val adminUser = Admin(id = admin.id, name = admin.name, surname = admin.surname, email = admin.email, image_url = admin.image_url,
                        phone = admin.phone, tc = admin.tc, gender = admin.gender, bloodType = admin.bloodType, birthDate = admin.birthDate, address = admin.address)
                    AdminProfileScreen(
                        admin = adminUser,
                        onMenuClick = { isMenuOpen = true },
                        onNavigate = { handleAdminNavigation(navController, it) }
                    )
                } ?: NavigateToLogin(navController)
            }

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

            composable(Routes.ADMIN_MENU) {
                AdminMenuScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigate = { handleAdminNavigation(navController, it) }
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

            composable(Routes.ADMIN_APPLICATIONS) {
                val viewModel: AdminApplicationsViewModel = viewModel()
                AdminApplicationsScreen(
                    onNavigate = { route -> handleAdminNavigation(navController, route) },
                    viewModel = viewModel
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

        // --- LOADING SCREEN ve MENÜLER (Overlay) ---
        if (!isSessionChecked) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(2f)
            ) {
                LoadingScreen()
            }
        }

        if (currentAdmin != null) {
            AdminSideMenuView(
                isOpen = isMenuOpen,
                user = User(id = currentAdmin!!.id, name = currentAdmin!!.name, surname = currentAdmin!!.surname, email = currentAdmin!!.email, studentNumber = "",phone = "", tc = "", gender = "", bloodType = "", birthDate = "", address = "", location = "", roomNo = "", image_url = ""),
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

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.loadingscreen),
            contentDescription = "Loading",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}

fun handleUserNavigation(navController: NavController, route: String) {
    val target = when (route) {
        "user_home" -> Routes.USER_HOME
        "user_profile" -> Routes.USER_PROFILE
        "user_menu", "menu" -> Routes.USER_MENU
        "calendar" -> Routes.USER_CALENDAR
        "user_study_area" -> Routes.USER_STUDY
        "user_laundry" -> Routes.USER_LAUNDRY
        "user_dry" -> Routes.USER_DRY
        "user_laundrymain" -> Routes.USER_LAUNDRYMAIN
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