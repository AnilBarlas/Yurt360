package com.example.yurt360.common.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.R
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Public

@Composable
fun AboutUsScreen(
    onMenuClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(onNavigate = onNavigate)
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
            // verticalScroll kaldırıldı: Ekran sabit kalır.
        ) {
            // --- ÜST GÖRSEL VE BAŞLIK ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bina),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Menü İkonu
                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier.padding(top = 40.dp, start = 10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menu",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Text(
                    text = "Hakkımızda",
                    color = Color.White,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(start = 24.dp, bottom = 40.dp)
                )
            }

            // --- NEDİR KARTI (Görselin üzerine binen kavisli yapı) ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-30).dp),
                shape = RoundedCornerShape(topStart = 0.dp, topEnd = 80.dp),
                color = Color.White,
                shadowElevation = 4.dp
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = "Yurt7tepe Nedir?",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Yurt7tepe, Yeditepe Üniversitesi öğrencilerinin yurt ve kampüs yaşamında karşılaştıkları günlük operasyonel ihtiyaçları tek bir mobil platformda birleştirmeyi hedefleyen kullanıcı odaklı bir uygulamadır.",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.DarkGray
                    )
                }
            }

            // --- İLETİŞİM BÖLÜMÜ ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 500.dp)
                    .offset(y = -10.dp),
                shape = RoundedCornerShape(topStart = 80.dp, topEnd = 80.dp),
                color = Color.White,
                border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "BİZİMLE İLETİŞİME GEÇİN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ContactInfoRow(Icons.Default.Public, "Web Sitesi", "yeditepe.edu.tr")
                    ContactInfoRow(Icons.Default.Phone, "Telefon", "(0216) 578 00 00")
                    ContactInfoRow(Icons.Default.Print, "Faks", "(0216) 578 02 99")
                    ContactInfoRow(Icons.Default.Email, "E-Posta", "info@yeditepe.edu.tr")
                }
            }
        }
    }
}

@Composable
fun ContactInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp), // Dikey boşluk minimal tutuldu
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center // Tüm satırı yatayda ortalar
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp), // İkon biraz daha küçültüldü
            tint = Color.Black
        )

        Spacer(modifier = Modifier.width(12.dp))

        Surface(
            modifier = Modifier
                .width(220.dp) // Genişlik 220dp'ye sabitlenerek kısaltıldı
                .height(48.dp), // Yükseklik daha kompakt hale getirildi
            shape = RoundedCornerShape(24.dp),
            color = Color(0xFFF7F7F7),
            shadowElevation = 0.dp
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = label,
                    fontSize = 10.sp,
                    color = Color.Gray
                )
                Text(
                    text = value,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    maxLines = 1 // Taşmaları önlemek için tek satır
                )
            }
        }
    }
}