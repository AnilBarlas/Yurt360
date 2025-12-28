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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.yurt360.common.utils.OrangePrimary
import com.example.yurt360.common.utils.purple
import com.example.yurt360.common.utils.orangelinear


@Composable
fun CustomBottomNavigationBar(
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
                // Home İkonu
                NavIconItem(
                    icon = Icons.Default.Home,
                    label = "home",
                    onClick = { onNavigate("home") }
                )

                // Profil İkonu
                NavIconItem(
                    icon = Icons.Default.Person,
                    label = "profile",
                    onClick = { onNavigate("profile") }
                )
            }
        }

        // Takvim Butonu
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
                imageVector = Icons.Default.DateRange,
                contentDescription = "Takvim",
                tint = Color.White,
                modifier = Modifier.size(50.dp)
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
                .size(60.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = OrangePrimary,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}