package com.example.yurt360.user.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.R
import com.example.yurt360.common.components.UserBottomNavigationBar
import com.example.yurt360.common.utils.purpleLinear

@Composable
fun UserSettingsScreen(
    userLocation: String,
    isAdmin: Boolean = false,
    onMenuClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {

        Image(
            painter = painterResource(id = R.drawable.profilebackground),
            contentDescription = null,
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
        )

        IconButton(
            onClick = { onMenuClick() },
            modifier = Modifier.padding(top = 40.dp, start = 10.dp)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.sidebar),
                contentDescription = "Menü",
                tint = Color.Unspecified,
                modifier = Modifier.size(36.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 240.dp)
                .background(Color.White)
                .padding(horizontal = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.dil),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(20.dp))
                Surface(
                    modifier = Modifier
                        .width(250.dp)
                        .height(60.dp)
                        .clickable { /* Dil seçimi */ },
                    shape = RoundedCornerShape(30.dp),
                    shadowElevation = 2.dp
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(brush = purpleLinear),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Dil", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                            Text("Türkçe", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
            SectionHeader(text = "HESAP")
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(20.dp))
                Surface(
                    modifier = Modifier
                        .width(250.dp)
                        .height(60.dp)
                        .clickable { onNavigate("user_update_password") },
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Parola değiştir", fontSize = 16.sp, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(75.dp))
            SectionHeader(text = "YURT")
            Spacer(modifier = Modifier.height(12.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.door),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = Color.Black
                )
                Spacer(modifier = Modifier.width(20.dp))
                Surface(
                    modifier = Modifier
                        .width(250.dp)
                        .height(60.dp)
                        .clickable { /* Yurt detay */ },
                    color = Color.White,
                    shape = RoundedCornerShape(12.dp),
                    shadowElevation = 2.dp
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(userLocation, fontSize = 16.sp, color = Color.Black)
                    }
                }
            }
        }

        UserBottomNavigationBar(
            modifier = Modifier.align(Alignment.BottomCenter),
            onNavigate = onNavigate
        )
    }
}

@Composable
fun SectionHeader(text: String) {
    Text(
        text = text,
        fontSize = 15.sp,
        fontWeight = FontWeight.Medium,
        color = Color.Black,
        letterSpacing = 1.sp
    )
}