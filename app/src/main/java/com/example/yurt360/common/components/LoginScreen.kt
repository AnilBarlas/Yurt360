package com.example.yurt360.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.R
import com.example.yurt360.common.model.TopUser
import kotlinx.coroutines.delay
import com.example.yurt360.common.utils.OrangePrimary
import com.example.yurt360.common.utils.Orange
val TextGray = Color(0xFF4A4A4A)

@Composable
fun LoginScreen(
    viewModel: LoginViewModel = viewModel(),
    onLoginSuccess: (TopUser) -> Unit
) {
    val username by viewModel.username.collectAsState()
    val password by viewModel.password.collectAsState()
    val loginState by viewModel.loginState.collectAsState()

    var showResetScreen by remember { mutableStateOf(false) }

    // Bildirim mesajını tutan state
    var notificationMessage by remember { mutableStateOf<String?>(null) }

    // Mesaj geldiğinde 2 saniye sonra otomatik kaybolması için
    LaunchedEffect(notificationMessage) {
        if (notificationMessage != null) {
            delay(2000) // 2 saniye bekle
            notificationMessage = null // Mesajı temizle
        }
    }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is LoginState.Success -> {
                notificationMessage = "Giriş Başarılı!"
                delay(500)
                onLoginSuccess(state.user)
                viewModel.resetLoginState()
            }
            is LoginState.Error -> {
                notificationMessage = state.message
                viewModel.resetLoginState()
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        if (showResetScreen) {
            ResetPasswordScreen(
                onSendClick = { email ->
                    viewModel.resetPassword(
                        email = email,
                        onSuccess = {
                            notificationMessage = "Sıfırlama bağlantısı e-postana gönderildi!"
                            showResetScreen = false
                        },
                        onError = { msg ->
                            notificationMessage = msg
                        }
                    )
                },
                onBackClick = {
                    showResetScreen = false
                }
            )
        } else {
            // --- UI İÇERİĞİ ---
            Image(
                painter = painterResource(id = R.drawable.bina),
                contentDescription = "Bina Görseli",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .align(Alignment.BottomCenter),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 80.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Image(
                    painter = painterResource(id = R.drawable.daire),
                    contentDescription = "Logo",
                    modifier = Modifier.size(150.dp),
                    contentScale = ContentScale.Fit
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "KULLANICI GİRİŞİ",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextGray,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                CustomLoginTextField(
                    value = username,
                    onValueChange = { viewModel.onUsernameChange(it) },
                    placeholder = "Kullanıcı Maili"
                )

                Spacer(modifier = Modifier.height(16.dp))

                CustomLoginTextField(
                    value = password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    placeholder = "Parola",
                    isPassword = true
                )

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        if (loginState !is LoginState.Loading) {
                            viewModel.onLoginClick()
                        }
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                        .shadow(8.dp, RoundedCornerShape(25.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(25.dp)
                ) {
                    Text(
                        text = if (loginState is LoginState.Loading) "Giriş Yapılıyor..." else "Giriş",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Parolamı Unuttum",
                    color = Orange,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable {
                        showResetScreen = true
                    }
                )
            }
        }

        // --- ÖZEL BİLDİRİM KUTUSU ---
        AnimatedVisibility(
            visible = notificationMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter) // Alt ortaya hizala
                .padding(bottom = 80.dp)       // En aşağıya değmemesi için boşluk bırak
        ) {
            notificationMessage?.let { msg ->
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .shadow(6.dp, RoundedCornerShape(25.dp)), // Daha yuvarlak hatlar
                    shape = RoundedCornerShape(25.dp),
                    color = Color.White,
                    tonalElevation = 4.dp
                ) {
                    Text(
                        text = msg,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomLoginTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    isPassword: Boolean = false
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(55.dp)
            .shadow(4.dp, RoundedCornerShape(30.dp)),
        shape = RoundedCornerShape(30.dp),
        color = Color.White
    ) {
        TextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                Text(text = placeholder, color = Color.Gray, fontSize = 14.sp)
            },
            visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
            keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
            singleLine = true,
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                cursorColor = OrangePrimary
            ),
            modifier = Modifier.fillMaxSize(),
            textStyle = androidx.compose.ui.text.TextStyle(
                fontSize = 16.sp,
                color = Color.Black
            )
        )
    }
}