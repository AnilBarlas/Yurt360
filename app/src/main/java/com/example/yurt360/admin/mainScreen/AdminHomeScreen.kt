package com.example.yurt360.admin.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
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

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {
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
                        .height(200.dp)
                        .clip(RoundedCornerShape(45.dp))
                        .background(purpleLinear)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.duyuru_arkaplan),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f,
                        modifier = Modifier
                            .matchParentSize()
                            .scale(1.1f)
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
                                    .offset(y = (-15).dp)
                                    .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 28.dp, vertical = 0.dp)
                            ) {
                                Text("Duyurular", color = Color.White, fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.height(15.dp))

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

                        // Megafon ve Duyuru Ekle Butonu Konteynırı
                        Box(
                            modifier = Modifier.fillMaxHeight(),
                            contentAlignment = Alignment.BottomEnd // Sağ alt köşeye hizalar
                        ) {
                            // Megafon Görseli
                            Image(
                                painter = painterResource(id = R.drawable.megafon),
                                contentDescription = null,
                                colorFilter = ColorFilter.tint(Color.White),
                                contentScale = ContentScale.Fit,
                                modifier = Modifier
                                    .size(75.dp)
                                    .offset(x = (-10).dp, y = (-15).dp)
                            )

                            // Admin Özel Buton (İnce, Uzun ve Beyaz Çerçeveli)
                            Box(
                                modifier = Modifier
                                    .offset(x = -5.dp, y = 15.dp) // Görseldeki gibi köşeye tam oturması için
                                    .clip(RoundedCornerShape(25.dp)) // Kenarları iyice yuvarlatıldı
                                    .background(Color(0xFFF95604))
                                    .border(
                                        width = 1.dp,
                                        color = Color.White,
                                        shape = RoundedCornerShape(25.dp)
                                    )
                                    .clickable { onNavigation("add_announcement") }
                                    .padding(horizontal = 20.dp, vertical = 1.dp) // Dikey padding azaltıldı (ince), yatay artırıldı (uzun)
                            ) {
                                Text(
                                    "DUYURU EKLE",
                                    color = Color.Black, // Yazı rengi orijinal haline sadık kalındı
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
                                        .clickable { onNavigation("admin_applications") }
                                )
                            }

                            // 2. Satır: Alt butonlar (Dikeyde resmin yaklaşık %53'ü)
                            Row(modifier = Modifier.weight(1.35f).fillMaxWidth()) {
                                // Çamaşır & Kurutma
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable { onNavigation("admin_laundrymain") }
                                )
                                // Yemek Menüsü
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .fillMaxHeight()
                                        .clickable { onNavigation("admin_menu") }
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

