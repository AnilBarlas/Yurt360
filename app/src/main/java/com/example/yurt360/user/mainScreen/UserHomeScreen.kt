package com.example.yurt360.user.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.R
import com.example.yurt360.common.model.User

val LightGrayBackground = Color(0xFFF5F5F5)
val OrangePrimary = Color(0xFFFF8C42)
val TextGray = Color(0xFF4A4A4A)
val CardGray = Color(0xFFE0E0E0)

@Composable
fun UserHomeScreen(
    user: User,
    onMenuClick: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            Box(modifier = Modifier.height(220.dp).fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.bina),
                    contentDescription = "Yurt Binası",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_sort_by_size),
                    contentDescription = "Menu",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(32.dp)
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-20).dp)
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 0.dp, bottomEnd = 20.dp, bottomStart = 20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "DUYURULAR",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextGray
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    DuyuruItem("Ana Duyuru", "Altında duyurunun açıklaması her ne hakkında ise")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    DuyuruItem("Yemekhane Hakkında", "Yarın yemekhane tadilatta olacaktır.")
                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))
                    DuyuruItem("Kayıt Yenileme", "Son kayıt tarihi 30 Eylül.")
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val menuItems = listOf(
                "YEMEKHANE",
                "ÇALIŞMA ALANI",
                "ODA DEĞİŞİMİ",
                "ÇAMAŞIR YIKAMA"
            )

            menuItems.forEachIndexed { index, title ->
                LargeMenuCard(
                    title = title,
                    isImageRight = (index % 2 == 0),
                    onClick = { onMenuClick(title) }
                )
            }
        }

        CustomBottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
fun DuyuruItem(title: String, desc: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        Text(text = desc, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun LargeMenuCard(
    title: String,
    isImageRight: Boolean,
    onClick: () -> Unit
) {
    val shape = if (isImageRight) {
        RoundedCornerShape(topStart = 0.dp, bottomStart = 0.dp, topEnd = 50.dp, bottomEnd = 50.dp)
    } else {
        RoundedCornerShape(topStart = 50.dp, bottomStart = 50.dp, topEnd = 0.dp, bottomEnd = 0.dp)
    }

    val alignModifier = if (isImageRight) Modifier.padding(end = 16.dp, start = 0.dp) else Modifier.padding(start = 16.dp, end = 0.dp)

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .padding(vertical = 8.dp)
            .clickable { onClick() }
    ) {
        // Arka plan kartı
        Surface(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .fillMaxHeight()
                .align(if (isImageRight) Alignment.CenterStart else Alignment.CenterEnd)
                .then(alignModifier),
            color = CardGray,
            shape = shape
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!isImageRight) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color.Gray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("fotoğraf gelecek", fontSize = 10.sp)
                    }
                }

                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                }

                if (isImageRight) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(Color.Gray.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("fotoğraf gelecek", fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun CustomBottomNavigationBar(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp),
            color = OrangePrimary
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                NavIconItem(icon = Icons.Default.Home, label = "Ana Sayfa")

                Spacer(modifier = Modifier.width(60.dp))

                NavIconItem(icon = Icons.Default.Person, label = "Profil")
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = 10.dp)
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.White)
                .padding(6.dp)
                .clip(CircleShape)
                .background(OrangePrimary)
                .clickable { /* Takvim Tıklama */ },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Takvim",
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }
}

@Composable
fun NavIconItem(icon: ImageVector, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { /* Navigasyon */ }
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(Color.White, shape = RoundedCornerShape(10.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = OrangePrimary
            )
        }
        Text(text = label, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Bold)
    }
}