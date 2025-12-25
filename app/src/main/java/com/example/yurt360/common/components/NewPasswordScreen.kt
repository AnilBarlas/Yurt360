package com.example.yurt360.common.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.R
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordScreen(
    onConfirmClick: (String) -> Unit
) {
    var newPassword by remember { mutableStateOf("") }
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    // Bildirim State'i
    var notificationMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(notificationMessage) {
        if (notificationMessage != null) {
            delay(2000)
            notificationMessage = null
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        // Arka plan
        Image(
            painter = painterResource(id = R.drawable.bina),
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().height(250.dp).align(Alignment.BottomCenter),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(screenHeight * 0.10f))

            Image(
                painter = painterResource(id = R.drawable.daire),
                contentDescription = "Logo",
                modifier = Modifier.size(200.dp),
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "YENİ ŞİFRE BELİRLE",
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextGray
            )

            Spacer(modifier = Modifier.height(30.dp))

            // Yeni Şifre Giriş Alanı
            Surface(
                modifier = Modifier.fillMaxWidth(0.85f).height(55.dp).shadow(4.dp, RoundedCornerShape(30.dp)),
                shape = RoundedCornerShape(30.dp),
                color = Color.White
            ) {
                TextField(
                    value = newPassword,
                    onValueChange = { newPassword = it },
                    placeholder = { Text("Yeni Şifreniz", color = Color.Gray) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = OrangePrimary
                    ),
                    modifier = Modifier.fillMaxSize(),
                    singleLine = true
                )
            }

            Spacer(modifier = Modifier.height(30.dp))

            Button(
                onClick = {
                    if (newPassword.length >= 6) {
                        onConfirmClick(newPassword)
                    } else {
                        notificationMessage = "Şifre en az 6 karakter olmalı."
                    }
                },
                modifier = Modifier.width(220.dp).height(50.dp).shadow(8.dp, RoundedCornerShape(25.dp)),
                colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                shape = RoundedCornerShape(25.dp)
            ) {
                Text("Şifreyi Güncelle", fontSize = 16.sp, color = Color.White)
            }
        }

        // --- ÖZEL BİLDİRİM KUTUSU ---
        AnimatedVisibility(
            visible = notificationMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            notificationMessage?.let { msg ->
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .shadow(6.dp, RoundedCornerShape(25.dp)),
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