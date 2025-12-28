package com.example.yurt360.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.R
import androidx.compose.ui.zIndex
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.Icons
import com.example.yurt360.common.utils.OrangePrimary
import com.example.yurt360.common.utils.purpleLinear

@Composable
fun PasswordUpdateScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigate: (String) -> Unit,
    onUpdatePassword: (String, (String?) -> Unit) -> Unit
) {
    var isSuccess by remember { mutableStateOf(false) }
    var currentPassword by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }

    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        // Arka Plan Görseli
        Image(
            painter = painterResource(id = R.drawable.bina),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (isSuccess) 350.dp else 220.dp)
                .clip(RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp))
                .background(Color.White)
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            if (!isSuccess) {
                Text(text = "Parolayı Güncelle", fontSize = 24.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(30.dp))

                CustomPasswordInput(label = "Mevcut Parola", value = currentPassword, onValueChange = { currentPassword = it })
                Spacer(modifier = Modifier.height(20.dp))
                CustomPasswordInput(label = "Yeni Parola", value = newPassword, onValueChange = { newPassword = it })

                if (errorMessage != null) {
                    Text(text = errorMessage!!, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(top = 8.dp))
                }

                Spacer(modifier = Modifier.height(40.dp))

                if (isLoading) {
                    CircularProgressIndicator(color = Color(0xFFFF8A65))
                } else {
                    // Gradient ve Boyut Ayarlı Buton
                    Button(
                        onClick = {
                            if (newPassword.length < 6) {
                                errorMessage = "Şifre en az 6 karakter olmalıdır."
                                return@Button
                            }
                            isLoading = true
                            errorMessage = null

                            onUpdatePassword(newPassword) { error ->
                                isLoading = false
                                if (error == null) isSuccess = true else errorMessage = error
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.5f) // Genişlik %50'ye düşürüldü
                            .height(50.dp)
                            .background(brush = purpleLinear, shape = RoundedCornerShape(20.dp))
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("Parolayı Güncelle", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Başarılı Ekranı
                Spacer(modifier = Modifier.height(60.dp))
                Text(text = "Parolanız güncellenmiştir.", fontSize = 18.sp, color = Color.Black, fontWeight = FontWeight.Medium)
                Spacer(modifier = Modifier.height(60.dp))
                SuccessButton(text = "Ana Sayfaya Geri Dön", onClick = onNavigateHome)
                Spacer(modifier = Modifier.height(25.dp))
                SuccessButton(text = "Profil Bilgilerine Geri Dön", onClick = onNavigateBack)
            }
        }

        if (!isSuccess) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Geri",
                tint = Color.White,
                modifier = Modifier
                    .padding(top = 50.dp, start = 20.dp)
                    .size(60.dp)
                    .clickable { onNavigateBack() }
            )
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            CustomBottomNavigationBar(onNavigate = onNavigate)
        }
    }
}

@Composable
fun CustomPasswordInput(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {

        // Etiket Kısmı: Surface kaldırıldı, sadece Text var
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
        )

        // Input Kısmı
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .zIndex(2f),
            shape = RoundedCornerShape(50.dp),
            shadowElevation = 4.dp,
            color = Color.White
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = PasswordVisualTransformation(),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                singleLine = true
            )
        }
    }
}

@Composable
fun SuccessButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        // 1. Butonun kendi rengini şeffaf yapıyoruz
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        // 2. İç padding'i sıfırlıyoruz ki gradient tam dolsun
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(45.dp)
            // 3. Brush'ı modifier üzerinden shape ile birlikte veriyoruz
            .background(brush = purpleLinear, shape = RoundedCornerShape(20.dp))
    ) {
        // Metni ortalamak için Box kullanıyoruz
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}