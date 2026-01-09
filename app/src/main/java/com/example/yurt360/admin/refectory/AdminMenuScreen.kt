package com.example.yurt360.admin.refectory

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.common.model.Menu
import com.example.yurt360.user.refectory.AdminMenuViewModel

// --- RENK TANIMLARI (User Tarafıyla Uyumlu) ---
private val OrangePrimary = Color(0xFFF27A39)
private val BackgroundColor = Color(0xFFF9F9F9)
private val TextDark = Color(0xFF333333)
private val RedDelete = Color(0xFFE53935)
private val RedBackground = Color(0xFFFFEBEE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminMenuViewModel = viewModel()
) {
    val menuList by viewModel.menuList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    // Input state
    var dateInput by remember { mutableStateOf("") }
    var foodsInput by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Menü Yönetimi",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Geri",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = OrangePrimary
                )
            )
        },
        containerColor = BackgroundColor
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            // --- 1. EKLEME BÖLÜMÜ (Üst Turuncu Alan ve Kart) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(OrangePrimary)
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .padding(bottom = 20.dp) // Alt kavis efekti için boşluk
            ) {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = OrangePrimary)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Yeni Menü Oluştur",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextDark
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Tarih Input
                        OutlinedTextField(
                            value = dateInput,
                            onValueChange = { dateInput = it },
                            label = { Text("Tarih (Örn: 14 Ekim Pazartesi)") },
                            leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = OrangePrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangePrimary,
                                focusedLabelColor = OrangePrimary,
                                cursorColor = OrangePrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Yemekler Input
                        OutlinedTextField(
                            value = foodsInput,
                            onValueChange = { foodsInput = it },
                            label = { Text("Yemek Listesi") },
                            leadingIcon = { Icon(Icons.Default.Edit, contentDescription = null, tint = OrangePrimary) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = OrangePrimary,
                                focusedLabelColor = OrangePrimary,
                                cursorColor = OrangePrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Ekle Butonu
                        Button(
                            onClick = {
                                if (dateInput.isNotBlank() && foodsInput.isNotBlank()) {
                                    viewModel.addMenu(dateInput, foodsInput) {
                                        Toast.makeText(context, "Menü Başarıyla Eklendi", Toast.LENGTH_SHORT).show()
                                        dateInput = ""
                                        foodsInput = ""
                                    }
                                } else {
                                    Toast.makeText(context, "Lütfen tarih ve yemek giriniz", Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                            shape = RoundedCornerShape(12.dp),
                            enabled = !isLoading
                        ) {
                            if (isLoading) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                            } else {
                                Text("LİSTEYE EKLE", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // --- 2. LİSTE BAŞLIĞI ---
            Text(
                text = "EKLİ OLAN MENÜLER",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Gray,
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 8.dp)
            )

            // --- 3. MENÜ LİSTESİ ---
            if (menuList.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Henüz hiç menü eklenmemiş.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(menuList) { menu ->
                        AdminMenuCardItem(
                            menu = menu,
                            onDelete = {
                                viewModel.deleteMenu(menu.id)
                                Toast.makeText(context, "Menü Silindi", Toast.LENGTH_SHORT).show()
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(50.dp)) } // Liste sonu boşluk
                }
            }
        }
    }
}

@Composable
fun AdminMenuCardItem(
    menu: Menu,
    onDelete: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Sol Çizgi (Estetik vurgu)
            Box(
                modifier = Modifier
                    .width(4.dp)
                    .height(60.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(OrangePrimary)
            )

            Spacer(modifier = Modifier.width(16.dp))

            // Orta Kısım: Tarih ve Yemekler
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = menu.date,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = TextDark
                )

                HorizontalDivider(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .width(40.dp),
                    thickness = 2.dp,
                    color = Color.LightGray.copy(alpha = 0.5f)
                )

                Text(
                    text = menu.foods,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    lineHeight = 20.sp
                )
            }

            // Sağ Kısım: Silme Butonu
            IconButton(
                onClick = onDelete,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .background(RedBackground, CircleShape) // Kırmızımsı hafif arka plan
                    .size(44.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Sil",
                    tint = RedDelete
                )
            }
        }
    }
}