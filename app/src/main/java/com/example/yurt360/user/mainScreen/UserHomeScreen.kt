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

val TextGray = Color(0xFF4A4A4A)
val CardGray = Color(0xFFE0E0E0)
val LightGrayBackground = Color(0xFFF5F5F5)

@Composable
fun UserHomeScreen(
    user: User,
    onMenuClick: (String) -> Unit = {},
    // Navigasyon tıklamalarını dışarı aktarmak için yeni parametre
    onNavigation: (String) -> Unit = {}
) {

    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(
                onNavigate = { route ->
                    // Alt bardan gelen 'home', 'profile' gibi istekleri yukarı ilet
                    onNavigation(route)
                }
            )
        },
        containerColor = LightGrayBackground
    ) { innerPadding ->
        // ... (İçerik kodları orijinal haliyle aynı) ...
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp)
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

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-24).dp)
                    .padding(horizontal = 16.dp)
                    .shadow(8.dp, RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "DUYURULAR",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    DuyuruItem("Ana Duyuru", "Altında duyurunun açıklaması her ne hakkında ise")
                    Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))

                    DuyuruItem("Yemekhane Hakkında", "Yarın öğle yemeğinde menü değişikliği vardır.")
                    Divider(color = Color.LightGray, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))

                    DuyuruItem("Kayıt Yenileme", "Son ödeme tarihi 30 Eylül.")
                }
            }

            Column(
                modifier = Modifier
                    .offset(y = (-10).dp)
                    .padding(bottom = 20.dp)
            ) {
                val menuItems = listOf(
                    "YEMEKHANE", "ÇALIŞMA ALANI", "ODA DEĞİŞİMİ", "ÇAMAŞIR YIKAMA"
                )

                menuItems.forEachIndexed { index, title ->
                    LargeMenuCard(
                        title = title,
                        isImageRight = (index % 2 == 0),
                        onClick = { onMenuClick(title) }
                    )
                }
            }
        }
    }
}

// ... (DuyuruItem ve LargeMenuCard orijinal haliyle aynı) ...
@Composable
fun DuyuruItem(title: String, desc: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Color.Black)
        Text(text = desc, fontSize = 12.sp, color = Color.Gray, lineHeight = 14.sp)
    }
}

@Composable
fun LargeMenuCard(title: String, isImageRight: Boolean, onClick: () -> Unit) {
    val shape = if (isImageRight) RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 50.dp, bottomEnd = 50.dp)
    else RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp, topEnd = 0.dp, bottomEnd = 0.dp)

    val alignModifier = if (isImageRight) Modifier.padding(end = 16.dp) else Modifier.padding(start = 16.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .fillMaxHeight()
                .align(if (isImageRight) Alignment.CenterStart else Alignment.CenterEnd)
                .then(alignModifier),
            color = CardGray,
            shape = shape,
            shadowElevation = 4.dp
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isImageRight) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.Gray.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        Text("fotoğraf gelecek", fontSize = 10.sp, color = Color.DarkGray)
                    }
                }
                Box(modifier = Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    Text(text = title, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Color.Black)
                }
                if (isImageRight) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.Gray.copy(alpha = 0.3f)), contentAlignment = Alignment.Center) {
                        Text("fotoğraf gelecek", fontSize = 10.sp, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}