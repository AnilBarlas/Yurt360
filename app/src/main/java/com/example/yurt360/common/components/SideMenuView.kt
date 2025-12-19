package com.example.yurt360.common.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.yurt360.common.model.User
import com.example.yurt360.user.mainScreen.OrangePrimary

@Composable
fun SideMenuView(
    isOpen: Boolean,
    user: User?,
    onClose: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
        // Karartılmış Arka Plan
        AnimatedVisibility(
            visible = isOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onClose() }
            )
        }

        // Menü İçeriği
        AnimatedVisibility(
            visible = isOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.75f),
                color = Color.White,
                shape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // 1. Üst Kısım: Geri Butonu ve Profil
                    Row(
                        modifier = Modifier
                            .padding(top = 40.dp, start = 20.dp)
                            .clickable { onClose() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, modifier = Modifier.size(30.dp))
                        Text("Geri", fontSize = 18.sp)
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF5F5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("fotoğrafı", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${user?.name ?: ""} ${user?.surname ?: ""}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(40.dp))

                    // 2. Menü Linkleri (Görseldeki kavisli tasarım)
                    val menuItems = listOf("Profil", "Hakkımızda", "Ayarlar")
                    menuItems.forEach { item ->
                        MenuRowItem(title = item) {
                            when (item) {
                                "Profil" -> onNavigate("profile")
                                "Hakkımızda" -> onNavigate("about_us") // Bu satırı ekledik/güncelledik
                            }
                            onClose()
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 3. Alt Linkler
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Parola Güncelle",
                            color = OrangePrimary,
                            modifier = Modifier.clickable {
                                onNavigate("update_password")
                                onClose()
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Çıkış Yap",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onLogout() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuRowItem(title: String, onClick: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .height(80.dp)
            .clickable { onClick() }
            .shadow(elevation = 5.dp, shape = RoundedCornerShape(topEnd = 60.dp, bottomEnd = 60.dp)),
        color = Color.White,
        shape = RoundedCornerShape(topEnd = 60.dp, bottomEnd = 60.dp)
    ) {
        Box(contentAlignment = Alignment.CenterStart, modifier = Modifier.padding(start = 30.dp)) {
            Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Medium)
        }
    }
}