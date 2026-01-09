package com.example.yurt360.user.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.R
import com.example.yurt360.common.components.CustomBottomNavigationBar
import com.example.yurt360.common.model.User

// --- RENK TANIMLARI ---
private val TextDarkColor = Color(0xFF1F1F1F)

@Composable
fun UserHomeScreen(
    user: User,
    onMenuClick: () -> Unit, // EKLENDİ: MainActivity'den gelen menü tıklaması
    onNavigation: (String) -> Unit, // EKLENDİ: Sayfa geçişlerini MainActivity'e bildirir
    viewModel: AnnouncementViewModel = viewModel()
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.observeAnnouncements(context)
    }

    Scaffold(
        bottomBar = {
            // Bottom bar tıklanırsa MainActivity'e haber ver
            CustomBottomNavigationBar(onNavigate = { route ->
                onNavigation(route)
            })
        },
        containerColor = Color.White
    ) { innerPadding ->

        Box(modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {
            HomeScreenContent(
                viewModel = viewModel,
                onMenuClick = onMenuClick,
                onNavigateToMenu = { onNavigation("refectory") } // Yemekhane kartına basınca
            )
        }
    }
}

// --- ANA SAYFA İÇERİĞİ ---
@Composable
fun HomeScreenContent(
    viewModel: AnnouncementViewModel,
    onMenuClick: () -> Unit,
    onNavigateToMenu: () -> Unit
) {
    val announcementList = viewModel.announcements

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Üst Resim ve Menü İkonu
        Box(modifier = Modifier.fillMaxWidth().height(240.dp)) {
            Image(
                painter = painterResource(id = R.drawable.bina),
                contentDescription = "Yurt",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Hamburger Menü İkonu (Sol Üst)
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menü",
                tint = Color.White,
                modifier = Modifier
                    .padding(top = 48.dp, start = 24.dp)
                    .size(32.dp)
                    .clickable { onMenuClick() } // Tıklanınca menüyü açar
            )
        }

        // İçerik Alanı
        Column(modifier = Modifier.fillMaxWidth().offset(y = (-50).dp)) {
            // Başlık
            Surface(
                color = Color.White,
                shape = RoundedCornerShape(topEnd = 20.dp),
                modifier = Modifier.wrapContentWidth()
            ) {
                Text(
                    text = "DUYURULAR",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextDarkColor,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                )
            }

            // Duyuru Listesi
            Column(modifier = Modifier.fillMaxWidth().background(Color.White).padding(top = 10.dp)) {
                if (announcementList.isEmpty()) {
                    Text("Duyurular yükleniyor...", modifier = Modifier.padding(20.dp), color = Color.Gray)
                } else {
                    announcementList.forEach { announcement ->
                        DuyuruItem(announcement.title, announcement.description)
                        HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp, horizontal = 20.dp), thickness = 0.5.dp)
                    }
                }
            }

            // Menü Kartları
            val items = listOf("YEMEKHANE", "ÇALIŞMA ALANI", "ODA DEĞİŞİMİ", "ÇAMAŞIR YIKAMA")
            items.forEachIndexed { index, title ->
                AlternatingMenuCard(
                    title = title,
                    isTextOnRight = (index % 2 == 0),
                    onClick = {

                        if (title == "YEMEKHANE") {
                            onNavigateToMenu()
                        }
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
fun DuyuruItem(title: String, desc: String) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextDarkColor)
        Text(text = desc, fontSize = 13.sp, color = Color.Gray)
    }
}

@Composable
fun AlternatingMenuCard(title: String, isTextOnRight: Boolean, onClick: () -> Unit) {
    val dynamicShape = if (isTextOnRight) RoundedCornerShape(topEnd = 90.dp) else RoundedCornerShape(topStart = 90.dp)
    Box(modifier = Modifier.fillMaxWidth().height(160.dp).clickable { onClick() }) {
        Row {
            if (isTextOnRight) CardImage(dynamicShape)
            Box(
                modifier = Modifier.weight(1.2f).fillMaxHeight().shadow(8.dp, dynamicShape).background(Color.White, dynamicShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
            if (!isTextOnRight) CardImage(dynamicShape)
        }
    }
}

@Composable
fun RowScope.CardImage(shape: RoundedCornerShape) {
    Image(
        painter = painterResource(id = R.drawable.bina),
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .clip(shape)
    )
}