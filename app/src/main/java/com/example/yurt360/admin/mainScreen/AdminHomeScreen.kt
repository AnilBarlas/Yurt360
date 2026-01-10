package com.example.yurt360.admin.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.R
import com.example.yurt360.common.components.AnnouncementViewModel
import com.example.yurt360.common.components.CustomAdminBottomNavigationBar
import com.example.yurt360.common.model.Admin
import com.example.yurt360.common.utils.OrangePrimary
import com.example.yurt360.common.utils.purpleLinear

@Composable
fun AdminHomeScreen(
    admin: Admin,
    viewModel: AnnouncementViewModel = viewModel(),
    onMenuClick: () -> Unit,
    onNavigation: (String) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.observeAnnouncements(context)
    }

    val latestAnnouncement = viewModel.announcements.firstOrNull()

    Scaffold(
        bottomBar = {
            CustomAdminBottomNavigationBar(
                onNavigate = onNavigation
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp), // Ana ekran kenar boşluğu
            horizontalAlignment = Alignment.Start
        ) {
            // Sidebar İkonu
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sidebar),
                    contentDescription = "Menu",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onMenuClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // İçerik Kolonu (UserHomeScreen ile aynı iç padding: 20.dp)
            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
                // Profil Bölümü
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mainscreenperson),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.White),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier.fillMaxSize().scale(2f)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Hoş Geldin!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Yeditepeli",
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                // Duyuru Kartı
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(purpleLinear)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.duyuru_arkaplan),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f,
                        modifier = Modifier.matchParentSize()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 28.dp, vertical = 4.dp)
                            ) {
                                Text("Duyurular", color = Color.White, fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = latestAnnouncement?.title ?: "Duyuru Yok",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = latestAnnouncement?.description ?: "Şu anda görüntülenecek güncel bir duyuru bulunmamaktadır.",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 20.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        // Megafon ve Duyuru Ekle Butonu
                        Box(contentAlignment = Alignment.BottomCenter) {
                            Image(
                                painter = painterResource(id = R.drawable.megafon),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.White),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(85.dp)
                                    .offset(y = 15.dp)
                            )
                            // Admin Özel Buton
                            Box(
                                modifier = Modifier
                                    .offset(y = 25.dp)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFFFF5722)) // Turuncu buton
                                    .clickable { onNavigation("add_announcement") }
                                    .padding(horizontal = 12.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    "DUYURU EKLE",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Hızlı Erişim Başlığı
                Text(
                    text = "Hızlı Erişim",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Hızlı Erişim - Maksimum Boyut ve İnce Ayarlı Butonlar
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(1.20f)
                            .wrapContentHeight()
                    ) {
                        // Ana Görsel
                        Image(
                            painter = painterResource(id = R.drawable.adminmainscreen),
                            contentDescription = "Admin Menüsü",
                            modifier = Modifier.fillMaxWidth(),
                            contentScale = ContentScale.FillWidth
                        )

                        // Şeffaf Tıklama Alanları - Resim büyüdüğü için oranları tekrar kalibre ettim
                        Column(modifier = Modifier.matchParentSize()) {
                            // 1. Satır: Başvurular (Dikeyde resmin yaklaşık %42'si)
                            Row(modifier = Modifier.weight(1.05f).fillMaxWidth()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clickable { onNavigation("applications") }
                                )
                            }

                            // 2. Satır: Alt butonlar (Dikeyde resmin yaklaşık %53'ü)
                            Row(modifier = Modifier.weight(1.35f).fillMaxWidth()) {
                                // Çamaşır & Kurutma
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable { onNavigation("laundry") }
                                )
                                // Yemek Menüsü
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable { onNavigation("menu") }
                                )
                            }
                            // Görselin en altındaki gölge ve kavis payı (%5)
                            Spacer(modifier = Modifier.weight(0.12f))
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}