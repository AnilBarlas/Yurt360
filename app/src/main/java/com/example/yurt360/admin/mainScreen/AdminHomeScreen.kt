package com.example.yurt360.admin.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.window.Dialog
import com.example.yurt360.R
import com.example.yurt360.common.components.CustomBottomNavigationBar
import com.example.yurt360.common.model.Admin
import com.example.yurt360.user.mainScreen.AnnouncementViewModel

// Renk Tanımlar
val TextDark = Color(0xFF1F1F1F)
val GreenTabColor = Color(0xFFC8E6C9) // Duyuru Ekle sekmesi için açık yeşil
val GreenTabTextColor = Color(0xFF1B5E20) // Koyu yeşil yazı

data class MenuItemData(val title: String, val iconResId: Int)

@Composable
fun AdminHomeScreen(
    admin: Admin,
    onMenuClick: () -> Unit,
    onNavigation: (String) -> Unit = {},
    viewModel: AnnouncementViewModel = viewModel()
) {
    val context = LocalContext.current
    val announcementList = viewModel.announcements

    // Dialogun görünüp görünmediğini kontrol eden state
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.observeAnnouncements(context)
    }

    // --- DIALOG (POP-UP) ---
    if (showAddDialog) {
        AddAnnouncementDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, desc ->
                viewModel.addAnnouncement(title, desc) {
                    showAddDialog = false // Ekleme bitince kapat
                }
            }
        )
    }

    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(onNavigate = { route -> onNavigation(route) })
        },
        containerColor = Color.White
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) {
            // --- 1. Üst Görsel Alanı ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.loginscreen),
                    contentDescription = "Yurt Binası",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Menü İkonu
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_menu_sort_by_size),
                    contentDescription = "Menü",
                    tint = Color.White,
                    modifier = Modifier
                        .padding(top = 40.dp, start = 20.dp)
                        .size(32.dp)
                        .clickable { onMenuClick() }
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-50).dp)
            ) {
                // --- 2. SEKMELİ BAŞLIK YAPISI ---
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    // Bu satır iki butonu ekranın soluna ve sağına iter
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // SOL SEKME: DUYURULAR (Beyaz)
                    Surface(
                        color = Color.White,
                        shape = RoundedCornerShape(topEnd = 20.dp),
                        modifier = Modifier
                            .width(140.dp)
                            .height(45.dp)
                    ) {
                        Box(contentAlignment = Alignment.CenterStart) {
                            Text(
                                text = "DUYURULAR",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark,
                                modifier = Modifier.padding(start = 20.dp)
                            )
                        }
                    }

                    // SAĞ SEKME: DUYURU EKLE
                    Surface(
                        color = GreenTabColor,
                        shape = RoundedCornerShape(topStart = 20.dp),
                        modifier = Modifier
                            .width(140.dp)
                            .height(45.dp)
                            .clickable { showAddDialog = true } // Tıklanınca Dialog açılır
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text(
                                text = "DUYURU EKLE",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = GreenTabTextColor
                            )
                        }
                    }
                }

                // --- 3. Duyuru Listesi ---
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White)
                        .padding(top = 10.dp)
                ) {
                    if (announcementList.isEmpty()) {
                        Text(
                            "Duyurular yükleniyor...",
                            modifier = Modifier.padding(20.dp),
                            color = Color.Gray,
                            fontSize = 13.sp
                        )
                    } else {
                        announcementList.forEachIndexed { index, announcement ->
                            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                                DuyuruItem(
                                    title = announcement.title,
                                    desc = announcement.description
                                )
                            }
                            if (index < announcementList.size - 1) {
                                HorizontalDivider(
                                    color = Color.LightGray,
                                    thickness = 0.5.dp,
                                    modifier = Modifier
                                        .padding(vertical = 12.dp)
                                        .padding(start = 20.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // --- 4. Alt Menüler ---
                val menuList = listOf(
                    MenuItemData("YEMEKHANE", R.drawable.loginscreen),
                    MenuItemData("ÇALIŞMA ALANI", R.drawable.loginscreen),
                    MenuItemData("ODA DEĞİŞİMİ", R.drawable.loginscreen),
                    MenuItemData("ÇAMAŞIR YIKAMA", R.drawable.loginscreen)
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    menuList.forEachIndexed { index, item ->
                        val isImageLeft = (index % 2 == 0)
                        AlternatingMenuCard(
                            title = item.title,
                            resimId = item.iconResId,
                            isImageLeft = isImageLeft,
                            onClick = {
                                // --- DÜZELTİLEN KISIM BURASI ---
                                if (item.title == "YEMEKHANE") {
                                    onNavigation("menu") // AdminMenuScreen'e yönlendirir
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

// --- DUYURU EKLEME DİALOG COMPOSABLE ---
@Composable
fun AddAnnouncementDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(32.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0F0F0)),
            modifier = Modifier
                .fillMaxWidth()
                .height(350.dp)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "DUYURU YAYINLA",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // 1. Başlık Alanı
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        placeholder = { Text("Ana Duyuru (Başlık)", color = Color.Gray, fontSize = 14.sp) },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // 2. Açıklama Alanı
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        placeholder = { Text("Altında duyurunun açıklaması...", color = Color.Gray, fontSize = 14.sp) },

                        singleLine = false,
                        minLines = 3,
                        maxLines = 5,

                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }

                // 3. Gönder Butonu
                IconButton(
                    onClick = {
                        if (title.isNotEmpty() && description.isNotEmpty()) {
                            onConfirm(title, description)
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp)
                        .size(50.dp)
                        .background(Color(0xFF1E40AF), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_media_play),
                        contentDescription = "Yayınla",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun DuyuruItem(title: String, desc: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            fontWeight = FontWeight.SemiBold,
            fontSize = 15.sp,
            color = TextDark
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = desc,
            fontSize = 13.sp,
            color = Color.Gray,
            lineHeight = 16.sp
        )
    }
}

@Composable
fun AlternatingMenuCard(
    title: String,
    resimId: Int,
    isImageLeft: Boolean,
    onClick: () -> Unit
) {
    val cardShape = if (isImageLeft) {
        RoundedCornerShape(topStart = 12.dp, bottomStart = 12.dp, topEnd = 60.dp, bottomEnd = 12.dp)
    } else {
        RoundedCornerShape(topStart = 60.dp, bottomStart = 12.dp, topEnd = 12.dp, bottomEnd = 12.dp)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = cardShape
    ) {
        Row(modifier = Modifier.fillMaxSize()) {
            if (isImageLeft) {
                Image(
                    painter = painterResource(id = resimId), contentDescription = null,
                    contentScale = ContentScale.Crop, modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(topEnd = 100.dp))
                )
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White), contentAlignment = Alignment.Center) {
                    Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            } else {
                Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White), contentAlignment = Alignment.Center) {
                    Text(text = title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
                Image(
                    painter = painterResource(id = resimId), contentDescription = null,
                    contentScale = ContentScale.Crop, modifier = Modifier.weight(1f).fillMaxHeight().clip(RoundedCornerShape(topStart = 100.dp))
                )
            }
        }
    }
}