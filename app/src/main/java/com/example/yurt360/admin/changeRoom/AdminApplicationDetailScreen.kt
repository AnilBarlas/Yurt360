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
import com.example.yurt360.common.components.CustomAdminBottomNavigationBar
import com.example.yurt360.common.utils.Geologica
import com.example.yurt360.model.ApplicationForm

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminApplicationDetailScreen(
    application: ApplicationForm,
    onBack: () -> Unit,
    onNavigate: (String) -> Unit
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

    // Prepare Data
    val p = application.profile
    val fullRoomInfo = "${p?.location ?: ""} ${p?.roomNumber ?: ""}".trim()

    Scaffold(
        containerColor = Color(0xFFF8F9FA), // App Background
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
        },
        // If you have a global bottom bar, ensure this screen doesn't cover it
        // or add it here: bottomBar = { YourBottomBar() }
        bottomBar = { CustomAdminBottomNavigationBar(onNavigate = onNavigate) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // --- BOX 1: OUTER BOX (Covers Title + Inner Box) ---
            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp) // 12dp Outer Padding
                    .shadow(4.dp, RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp), clip = false),
                shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                color = Color.White
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // --- BIG TITLE ---
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = titleText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = Geologica,
                            color = Color.DarkGray
                        )
                    }

                    // --- BOX 2: INNER BOX (Covers Details) ---
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 11.dp) // UPDATED: 11dp padding relative to Outer Box
                            .shadow(4.dp, RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp), clip = false),
                        shape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp),
                        color = Color.White
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 24.dp)
                                .verticalScroll(rememberScrollState()),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Spacer(modifier = Modifier.height(32.dp))

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
                                fontFamily = Geologica,
                                color = Color(0xFF7E87E2),
                                modifier = Modifier.align(Alignment.Start).padding(bottom = 8.dp)
                            )

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .defaultMinSize(minHeight = 120.dp)
                                    .shadow(2.dp, RoundedCornerShape(10.dp), clip = false),
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFFF8F8F8)
                            ) {
                                Box(modifier = Modifier.padding(16.dp)) {
                                    Text(
                                        text = application.message,
                                        fontSize = 15.sp,
                                        fontFamily = Geologica,
                                        color = Color.Black
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(24.dp))

                            // --- EXTRA INFO ---
                            DetailRow(label = "E-mail:", value = p?.email ?: "-")
                            DetailRow(label = "Telefon Numarası:", value = p?.phone ?: "-")

                            Spacer(modifier = Modifier.height(32.dp))
                            Spacer(modifier = Modifier.height(32.dp))
                        }
                    }
                }
            }
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
            fontFamily = Geologica,
            color = Color(0xFF7E87E2),
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
                    fontFamily = Geologica,
                    color = Color.Black,
                    maxLines = 1
                )
            }
        }
    }
}