package com.example.yurt360.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.yurt360.R
import com.example.yurt360.common.utils.purple
import com.example.yurt360.common.utils.orangelinear

@Composable
fun CustomAdminBottomNavigationBar(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color.Transparent),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(brush = orangelinear)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onNavigate("home") }
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.Transparent, shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.home),
                            contentDescription = "home",
                            tint = White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = "Anasayfa",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(y = (-10).dp)
                    )
                }

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.clickable { onNavigate("profile") }
                ) {
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .background(Color.Transparent, shape = RoundedCornerShape(16.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.user),
                            contentDescription = "profile",
                            tint = White,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Text(
                        text = "Profil",
                        color = Color.White,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.offset(y = (-10).dp)
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-50).dp)
                .zIndex(1f)
                .size(100.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.verticalGradient(
                        0.5f to Color.Transparent,
                        0.5f to Color.White
                    )
                )
                .padding(6.dp)
                .clip(CircleShape)
                .background(purple)
                .clickable { onNavigate("calendar") },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.megafon),
                contentDescription = "Takvim",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
            )
        }
    }
}