package com.example.yurt360.user.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.R
import com.example.yurt360.common.components.CustomBottomNavigationBar
import com.example.yurt360.common.model.User

// Renk Tanımları
val TextDark = Color(0xFF1F1F1F)
val BackgroundColor = Color.White
val MenuCardColor = Color.White

@Composable
fun UserHomeScreen(
    user: User,
    onMenuClick: (String) -> Unit = {},
    onNavigation: (String) -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(
                onNavigate = { route -> onNavigation(route) }
            )
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // --- 1. Üst Görsel Alanı ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.bina),
                    contentDescription = "Yurt Binası",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_sort_by_size),
                    contentDescription = "Menü",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(top = 40.dp, start = 20.dp)
                        .size(32.dp)
                        .clickable { }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp)
            ) {

                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(topEnd = 20.dp),
                    modifier = Modifier.wrapContentWidth() // Sadece yazı kadar yer kaplar
                ) {
                    Text(
                        text = "DUYURULAR",
                        fontSize = 14.sp, // Daha kibar yazı boyutu
                        fontWeight = FontWeight.Bold,
                        color = TextDark,
                        modifier = Modifier
                            .padding(horizontal = 20.dp, vertical = 8.dp) // Daha kompakt boşluklar
                    )
                }

                // --- B) DUYURU LİSTESİ (GÖVDE) ---
                // Tam genişlikte beyaz zemin
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 10.dp)
                ) {
                    repeat(3) { index ->
                        Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                            DuyuruItem("Ana Duyuru", "Altında duyurunun açıklaması her ne hakkında ise")
                        }

                        if (index < 2) {
                            Divider(
                                color = Color.LightGray,
                                thickness = 0.5.dp,
                                modifier = Modifier
                                    .padding(vertical = 12.dp)
                                    .padding(start = 20.dp)
                            )
                        }
                    }

                    // Listenin altı ile kartlar arasında biraz boşluk
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // --- 3. Menü Kartları ---
                val menuItems = listOf(
                    "YEMEKHANE", "ÇALIŞMA ALANI", "ODA DEĞİŞİMİ", "ÇAMAŞIR YIKAMA"
                )

                menuItems.forEachIndexed { index, title ->
                    val isTextOnRight = (index % 2 == 0)

                    AlternatingMenuCard(
                        title = title,
                        isTextOnRight = isTextOnRight,
                        onClick = { onMenuClick(title) }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun AlternatingMenuCard(
    title: String,
    isTextOnRight: Boolean,
    onClick: () -> Unit
) {
    val cardHeight = 162.dp

    // Kavis Şekli
    val dynamicShape = if (isTextOnRight) {
        RoundedCornerShape(topEnd = 90.dp)
    } else {
        RoundedCornerShape(topStart = 90.dp)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(cardHeight)
            .clickable { onClick() }
    ) {
        Row(modifier = Modifier.fillMaxSize()) {

            // --- SOL TARAFTAKİ ALAN (Resim soldaysa) ---
            if (isTextOnRight) {
                Image(
                    painter = painterResource(id = R.drawable.bina),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(dynamicShape)
                )
            }

            // --- YAZI KUTUSU (ORTAK) ---
            Box(
                modifier = Modifier
                    .weight(1.2f)
                    .fillMaxHeight()
                    .shadow(elevation = 33.8.dp, shape = dynamicShape)
                    .background(color = MenuCardColor, shape = dynamicShape)
                    .clip(dynamicShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // --- SAĞ TARAFTAKİ ALAN (Resim sağdaysa) ---
            if (!isTextOnRight) {
                Image(
                    painter = painterResource(id = R.drawable.bina),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(dynamicShape)
                )
            }
        }
    }
}

@Composable
fun DuyuruItem(title: String, desc: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = TextDark)
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = desc, fontSize = 13.sp, color = Color.Gray, lineHeight = 16.sp)
    }
}