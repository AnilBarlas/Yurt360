package com.example.yurt360.user.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.R
import com.example.yurt360.common.components.CustomBottomNavigationBar
import com.example.yurt360.common.model.User
import com.example.yurt360.common.utils.OrangePrimary

@Composable
fun UserHomeScreen(
    user: User,
    viewModel: AnnouncementViewModel,
    onMenuClick: () -> Unit,
    onNavigation: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(
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
                modifier = Modifier
                .padding(horizontal = 20.dp),
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Profil Resmi
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_launcher_foreground),
                            contentDescription = null,
                            tint = Color.White
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    // İsim ve Hoşgeldin Yazısı
                    Column {
                        Text(
                            text = "Hoş Geldin!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "${user.name} ${user.surname}",
                            fontSize = 15.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))

                // --- 2. DUYURU KARTI ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFFB39DDB),
                                    Color(0xFF7986CB)
                                )
                            )
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .padding(horizontal = 12.dp, vertical = 4.dp)
                        ) {
                            Text("Duyurular", color = Color.White, fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Yurt Başvuruları",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Text(
                            text = "2025-2026 dönemi kayıtları başladı.\nSon gün 15 Eylül!",
                            fontSize = 14.sp,
                            color = Color.White.copy(alpha = 0.9f),
                            lineHeight = 20.sp
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .offset(x = (-20).dp, y = 20.dp)
                            .size(80.dp)
                            .graphicsLayer { rotationZ = -20f }
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // --- 3. HIZLI ERİŞİM BAŞLIĞI ---
                Text(
                    text = "Hızlı Erişim",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- 4. HIZLI ERİŞİM GRID ---
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mainscreenbutton),
                        contentDescription = "Hızlı Erişim Menüsü",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )

                    Column(modifier = Modifier.matchParentSize()) {
                        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigation("study_area") })
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigation("laundry") })
                        }
                        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigation("applications") })
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigation("menu") })
                        }
                    }
                }
            }


            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}