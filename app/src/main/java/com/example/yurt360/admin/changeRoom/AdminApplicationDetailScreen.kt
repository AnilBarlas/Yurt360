package com.example.yurt360.admin.changeRoom

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.common.model.ApplicationForm
import com.example.yurt360.common.utils.Geologica


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApplicationDetailScreen(
    application: ApplicationForm,
    onBack: () -> Unit
) {

    val titleText = when (application.type) {
        "Oda Değişimi" -> "ODA DEĞİŞİM TALEBİ FORMU"
        "Şikayet" -> "ŞİKAYET FORMU"
        "Öneri" -> "ÖNERİ FORMU"
        else -> "BAŞVURU FORMU"
    }

    val messageTitle = when (application.type) {
        "Oda Değişimi" -> "Oda Değiştirme Talebi Sebebi"
        "Şikayet" -> "Şikayet Detayı"
        "Öneri" -> "Öneri Detayı"
        else -> "Mesaj İçeriği"
    }

    // 2. Prepare Data
    val p = application.profile
    val fullRoomInfo = "${p?.location ?: ""} ${p?.roomNumber ?: ""}".trim()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Geri")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- TITLE ---
            Text(
                text = titleText,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = Geologica,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 16.dp, bottom = 32.dp)
            )

            // --- ROWS ---
            DetailRow(label = "Ad:", value = p?.firstName ?: "-")
            DetailRow(label = "Soyad:", value = p?.lastName ?: "-")
            DetailRow(label = "Öğrenci Numarası:", value = p?.studentNumber ?: "-")
            DetailRow(label = "Oda Numarası:", value = fullRoomInfo.ifEmpty { "-" })

            Spacer(modifier = Modifier.height(24.dp))

            // --- MESSAGE SECTION ---
            Text(
                text = messageTitle,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.DarkGray,
                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
            )

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 120.dp)
                    .shadow(2.dp, RoundedCornerShape(10.dp), clip = false),
                shape = RoundedCornerShape(10.dp),
                color = Color(0xFFF8F8F8) // Slightly gray box for message
            ) {
                Box(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = application.message,
                        fontSize = 15.sp,
                        color = Color.Black
                    )
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            DetailRow(label = "E-mail:", value = p?.email ?: "-")
            DetailRow(label = "Telefon Numarası:", value = p?.phone ?: "-")

            Spacer(modifier = Modifier.height(32.dp))
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Label
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Gray,
            modifier = Modifier.weight(0.4f)
        )

        // Value Box
        Surface(
            modifier = Modifier
                .weight(0.6f)
                .height(40.dp)
                .shadow(2.dp, RoundedCornerShape(10.dp), clip = false),
            shape = RoundedCornerShape(10.dp),
            color = Color.White
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.fillMaxSize().padding(horizontal = 12.dp)
            ) {
                Text(
                    text = value,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Black,
                    maxLines = 1
                )
            }
        }
    }
}