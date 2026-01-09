package com.example.yurt360.common.utils

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush

val OrangePrimary = Color(0xFFF95604)

val Orange = Color(0xFFFF8838)

val purple = Color(0xFF8B93E5)

val orangelinear = Brush.verticalGradient(
    colors = listOf(
        Color(0xFFF95604), // ÜST (Koyu / Bitiş Rengi)
        Color(0xFFFF8838) // Bitiş Rengi
    )
)

val purpleLinear= Brush.verticalGradient(
    colorStops = arrayOf(
        0.0f to Color(0xFFBEC0DE),  // %0 Başlangıç
        0.31f to Color(0xFF9DA3E0), // %31 Ara Geçiş
        1.0f to Color(0xFF7E87E2)   // %100 Bitiş
    )
)