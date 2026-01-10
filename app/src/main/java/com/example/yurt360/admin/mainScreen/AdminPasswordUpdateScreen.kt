package com.example.yurt360.admin.mainScreen

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.R
import com.example.yurt360.common.utils.purpleLinear
import com.example.yurt360.common.components.CustomAdminBottomNavigationBar

@Composable
fun AdminPasswordUpdateScreen(
    onNavigateBack: () -> Unit,
    onNavigateHome: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: AdminPasswordUpdateViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.profilebackground),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .align(Alignment.TopCenter)
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (uiState.isSuccess) 350.dp else 220.dp)
                .clip(RoundedCornerShape(topStart = 100.dp, topEnd = 100.dp))
                .background(Color.White)
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            if (!uiState.isSuccess) {
                Text(text = "Parolayı Güncelle", fontSize = 24.sp, color = Color.Black)
                Spacer(modifier = Modifier.height(30.dp))

                CustomPasswordInput(
                    label = "Mevcut Parola",
                    value = uiState.currentPassword,
                    onValueChange = { viewModel.onCurrentPasswordChange(it) }
                )
                Spacer(modifier = Modifier.height(20.dp))

                CustomPasswordInput(
                    label = "Yeni Parola",
                    value = uiState.newPassword,
                    onValueChange = { viewModel.onNewPasswordChange(it) }
                )

                if (uiState.errorMessage != null) {
                    Text(
                        text = uiState.errorMessage!!,
                        color = Color.Red,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(40.dp))

                if (uiState.isLoading) {
                    CircularProgressIndicator(color = Color(0xFFFF8A65))
                } else {
                    Button(
                        onClick = { viewModel.updatePassword() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                        contentPadding = PaddingValues(),
                        shape = RoundedCornerShape(20.dp),
                        modifier = Modifier
                            .fillMaxWidth(0.5f)
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
                Spacer(modifier = Modifier.height(60.dp))
                Text(
                    text = "Parolanız güncellenmiştir.",
                    fontSize = 18.sp,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(60.dp))
                SuccessButton(text = "Ana Sayfaya Geri Dön", onClick = onNavigateHome)
                Spacer(modifier = Modifier.height(25.dp))
                SuccessButton(text = "Profil Bilgilerine Geri Dön", onClick = onNavigateBack)
            }
        }

        if (!uiState.isSuccess) {
            // Geri butonu için tıklama alanı genişletilmiş kapsayıcı
            Box(
                modifier = Modifier
                    .padding(top = 26.dp, start = 6.dp) // Görsel konumu korumak için ayarlandı
                    .size(48.dp)
                    .clickable { onNavigateBack() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow),
                    contentDescription = "Geri",
                    tint = Color.Unspecified,
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            CustomAdminBottomNavigationBar(onNavigate = onNavigate)
        }
    }
}

@Composable
fun CustomPasswordInput(label: String, value: String, onValueChange: (String) -> Unit) {
    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(vertical = 8.dp)) {

        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(start = 16.dp, bottom = 6.dp)
        )

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
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
        contentPadding = PaddingValues(),
        shape = RoundedCornerShape(20.dp),
        modifier = Modifier
            .fillMaxWidth(0.8f)
            .height(45.dp)
            .background(brush = purpleLinear, shape = RoundedCornerShape(20.dp))
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}