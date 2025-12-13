package com.example.yurt360.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CustomBottomNavigationBar(
    modifier: Modifier = Modifier,
    onNavigate: (String) -> Unit
) {
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
                NavIconItem(
                    icon = Icons.Default.Home,
                    label = "Ana Sayfa",
                    onClick = { onNavigate("home") }
                )

                Spacer(modifier = Modifier.width(60.dp))

                NavIconItem(
                    icon = Icons.Default.Person,
                    label = "Profil",
                    onClick = { onNavigate("profile") }
                )
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
                .clickable { onNavigate("calendar") },
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
fun NavIconItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
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