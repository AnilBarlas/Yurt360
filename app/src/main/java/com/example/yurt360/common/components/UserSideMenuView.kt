package com.example.yurt360.common.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.yurt360.common.model.User
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asAndroidPath
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb

@Composable
fun SideMenuView(
    isOpen: Boolean,
    user: User?,
    onClose: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
        AnimatedVisibility(
            visible = isOpen,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .clickable { onClose() }
            )
        }

        // Menü İçeriği
        AnimatedVisibility(
            visible = isOpen,
            enter = slideInHorizontally(initialOffsetX = { -it }),
            exit = slideOutHorizontally(targetOffsetX = { -it })
        ) {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(0.85f),
                color = Color.White,
                shape = RoundedCornerShape(topEnd = 0.dp, bottomEnd = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // 1. Üst Kısım: Geri Butonu ve Profil
                    Row(
                        modifier = Modifier
                            .padding(top = 40.dp, start = 20.dp)
                            .clickable { onClose() },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = null, modifier = Modifier.size(30.dp))
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color(0xFFF5F5F5)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = user?.name?.take(1)?.uppercase() ?: "?",
                                fontSize = 36.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Gray
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "${user?.name ?: ""} ${user?.surname ?: ""}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(30.dp))

                    // 2. Menü Linkleri
                    val menuItems = listOf("Profil", "Hakkımızda", "Ayarlar")

                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(0.dp)
                    ) {
                        menuItems.forEachIndexed { index, item ->
                            MenuRowItem(
                                title = item,
                                isAlternatingLeft = (index % 2 == 0)
                            ) {
                                when (item) {
                                    "Profil" -> onNavigate("profile")
                                    "Hakkımızda" -> onNavigate("about_us")
                                    "Ayarlar" -> onNavigate("settings")
                                }
                                onClose()
                            }

                            // Kartlar arası boşluk
                            Spacer(modifier = Modifier.height(12.dp))
                        }
                    }

                    Spacer(modifier = Modifier.weight(1f))

                    // 3. Alt Linkler
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            "Parola Güncelle",
                            color = Color(0xFFFF9800),
                            modifier = Modifier.clickable {
                                onNavigate("update_password")
                                onClose()
                            }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            "Çıkış Yap",
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.clickable { onLogout() }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuRowItem(
    title: String,
    isAlternatingLeft: Boolean,
    onClick: () -> Unit
) {
    val shape = if (isAlternatingLeft) {
        RoundedCornerShape(topStart = 0.dp, topEnd = 100.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
    } else {
        RoundedCornerShape(topStart = 100.dp, topEnd = 0.dp, bottomEnd = 0.dp, bottomStart = 0.dp)
    }

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(130.dp)
            .clickable { onClick() }
            .drawBehind {

                val shadowColor = Color.Black.copy(alpha = 0.15f).toArgb()
                val transparentColor = Color.Transparent.toArgb()

                val paint = Paint().asFrameworkPaint().apply {
                    color = transparentColor
                    setShadowLayer(25f, 0f, -10f, shadowColor)
                }

                drawIntoCanvas { canvas ->
                    val path = Path().apply {
                        if (isAlternatingLeft) {
                            moveTo(0f, size.height)
                            lineTo(0f, 0f)
                            lineTo(size.width - 100.dp.toPx(), 0f)
                            // Sağ üst kavis
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    size.width - 200.dp.toPx(), 0f, size.width, 200.dp.toPx()
                                ),
                                startAngleDegrees = 270f,
                                sweepAngleDegrees = 90f,
                                forceMoveTo = false
                            )
                        } else {
                            moveTo(size.width, size.height)
                            lineTo(size.width, 0f)
                            lineTo(100.dp.toPx(), 0f)
                            // Sol üst kavis
                            arcTo(
                                rect = androidx.compose.ui.geometry.Rect(
                                    0f, 0f, 200.dp.toPx(), 200.dp.toPx()
                                ),
                                startAngleDegrees = 270f,
                                sweepAngleDegrees = -90f,
                                forceMoveTo = false
                            )
                        }
                    }
                    canvas.nativeCanvas.drawPath(path.asAndroidPath(), paint)
                }
            },
        shape = shape,
        color = Color.White,
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 40.dp),
            contentAlignment = if (isAlternatingLeft) Alignment.CenterStart else Alignment.CenterEnd
        ) {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}