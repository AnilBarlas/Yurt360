package com.example.yurt360.common.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.R

@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        // 1. Arka Plan Görseli
        Image(
            painter = painterResource(id = R.drawable.bina),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .align(Alignment.TopCenter)
        )

        // Karartma efekti (Yazının okunması için hafif gölge)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color.Black.copy(alpha = 0.2f))
        )

        // 2. Üst Kısım: Geri Butonu ve Başlık
        Column(
            modifier = Modifier
                .padding(top = 50.dp, start = 20.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowLeft,
                contentDescription = "Geri",
                tint = Color.White,
                modifier = Modifier
                    .size(40.dp)
                    .clickable { onNavigateBack() }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Ayarlar",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // 3. Beyaz İçerik Alanı
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 220.dp) // Görselin üzerine binmesi için
                .clip(RoundedCornerShape(topStart = 60.dp, topEnd = 60.dp)) // Görseldeki gibi oval köşe
                .background(Color.White)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(50.dp))

            // --- GENEL BÖLÜMÜ ---
            SectionHeader(text = "GENEL")

            Spacer(modifier = Modifier.height(16.dp))

            // Dil Seçeneği Butonu
            SettingsItemRow(
                icon = painterResource(id = android.R.drawable.ic_menu_agenda), // Globe ikonu yoksa varsayılan
                title = "Dil",
                subtitle = "Türkçe",
                isIconVector = true // Globe ikonu için aşağıda vector kullanımı simüle edildi
            )

            Spacer(modifier = Modifier.height(40.dp))

            // --- BİLDİRİMLER BÖLÜMÜ (Anasayfa yerine) ---
            SectionHeader(text = "BİLDİRİMLER")

            Spacer(modifier = Modifier.height(16.dp))

            // Duyurular Butonu
            SettingsItemRow(
                iconVector = Icons.Default.Notifications,
                title = "Duyurular",
                subtitle = "Açık",
                isIconVector = true
            )
        }
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        letterSpacing = 1.sp
    )
}

@Composable
fun SettingsItemRow(
    iconVector: ImageVector? = null,
    icon: Painter? = null,
    title: String,
    subtitle: String,
    isIconVector: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Sol İkon
        if (isIconVector && iconVector != null) {
            Icon(
                imageVector = iconVector,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color.Black
            )
        } else if (icon != null) {
            Icon(
                painter = icon,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color.Black
            )
        } else {
            // Fallback icon (Dünya ikonu yerine geçici)
            Icon(
                imageVector = Icons.Default.Settings,
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color.Black
            )
        }

        Spacer(modifier = Modifier.width(20.dp))

        // Gri Hap Şeklindeki Buton Alanı
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            color = Color(0xFFEEEEEE), // Açık gri arka plan
            shape = RoundedCornerShape(30.dp) // Tam oval kenarlar
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (subtitle.isNotEmpty()) {
                    Text(
                        text = title,
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = subtitle,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                } else {
                    Text(
                        text = title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
    }
}