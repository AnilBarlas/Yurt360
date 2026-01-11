package com.example.yurt360.admin.mainScreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.R
import com.example.yurt360.common.components.CustomAdminBottomNavigationBar
import com.example.yurt360.user.mainScreen.ContactInfoRow

@Composable
fun AdminAboutUsScreen(
    onMenuClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            CustomAdminBottomNavigationBar(onNavigate = onNavigate)
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .background(Color.White)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.profilebackground),
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            translationY = 80f
                        }
                )

                IconButton(
                    onClick = onMenuClick,
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(start = 10.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.sidebar),
                        contentDescription = "Menu",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(36.dp)
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
                        text = "Yurt7tepe, Yeditepe Üniversitesi öğrencilerinin yurt ve kampüs yaşamında karşılaştıkları günlük operasyonel ihtiyaçları tek bir mobil platformda birleştirmeyi hedefleyen kullanıcı odaklı bir uygulamadır. (Yönetici Paneli)",
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = Color.DarkGray
                    )
                }
            }

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .offset(y = (-10).dp),
                shape = RoundedCornerShape(topStart = 80.dp, topEnd = 80.dp),
                color = Color.White,
                border = BorderStroke(0.5.dp, Color.LightGray.copy(alpha = 0.3f))
            ) {
                Column(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                        .padding(top = 24.dp, bottom = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "BİZİMLE İLETİŞİME GEÇİN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    ContactInfoRow(painterResource(R.drawable.world), "Web Sitesi", "yeditepe.edu.tr")
                    ContactInfoRow(painterResource(R.drawable.phone), "Telefon", "(0216) 578 00 00")
                    ContactInfoRow(painterResource(R.drawable.fax), "Faks", "(0216) 578 02 99")
                    ContactInfoRow(painterResource(R.drawable.letter), "E-Posta", "info@yeditepe.edu.tr")

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }
}