package com.example.yurt360.user.refectory

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.common.model.Menu

// --- Renk Tanımları ---
val OrangePrimary = Color(0xFFF27A39)
val BackgroundColor = Color(0xFFF9F9F9)
val TextDarkGray = Color(0xFF333333)
val SelectedBlue = Color(0xFF7B85D8)

@Composable
fun MenuScreen(
    onNavigate: (String) -> Unit,
    viewModel: MenuViewModel = viewModel() // ViewModel buraya bağlandı
) {
    // ViewModel'den gelen verileri dinle (Observe)
    val menuList by viewModel.menuList.collectAsState()
    val selectedMenu by viewModel.selectedMenu.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            CustomBottomNavigationBar(onNavigate = onNavigate)
        }
    ) { innerPadding ->
        Column(modifier = Modifier.fillMaxSize().padding(innerPadding)) {

            // Geri Butonu
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Geri",
                    modifier = Modifier.size(32.dp).clickable { onNavigate("home") },
                    tint = Color.Black
                )
            }

            if (isLoading) {
                // Yükleniyor animasyonu
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    // 1. Üstteki Büyük Kart (Seçili Menü)
                    item {
                        selectedMenu?.let { menu ->
                            MenuCardItem(menu = menu)
                        } ?: Text(
                            "Henüz menü eklenmemiş.",
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // 2. Alttaki Tarih Listesi (Database'den gelen)
                    items(menuList) { menu ->
                        // Hangi menü seçiliyse o mavidir
                        val isSelected = (menu.id == selectedMenu?.id)
                        DateListItem(
                            menu = menu,
                            isSelected = isSelected,
                            onClick = { viewModel.selectMenu(menu) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MenuCardItem(menu: Menu) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 24.dp, end = 24.dp, bottom = 24.dp)
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 16.dp, bottomEnd = 16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp, bottomStart = 16.dp, bottomEnd = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 30.dp, horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "BUGÜNÜN MENÜSÜ", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextDarkGray)

            // Database'den gelen tarih
            Text(
                text = menu.date,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp).width(200.dp), thickness = 1.dp, color = Color.LightGray)

            // Database'den gelen yemekler
            Text(
                text = menu.foods,
                textAlign = TextAlign.Center,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
        }
    }
}

@Composable
fun DateListItem(menu: Menu, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .height(55.dp)
            .clickable { onClick() }, // Tıklama özelliği
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
            Text(
                text = menu.date, // Database'den gelen tarih
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) SelectedBlue else TextDarkGray
            )
        }
    }
}

// --- Bottom Navigation Kodları (Aynen korundu) ---
@Composable
fun CustomBottomNavigationBar(modifier: Modifier = Modifier, onNavigate: (String) -> Unit) {
    Box(modifier = modifier.fillMaxWidth().height(100.dp), contentAlignment = Alignment.BottomCenter) {
        Surface(modifier = Modifier.fillMaxWidth().height(70.dp), color = OrangePrimary) {
            Row(modifier = Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceAround) {
                NavIconItem(icon = Icons.Default.Home, label = "Ana Sayfa", onClick = { onNavigate("home") })
                Spacer(modifier = Modifier.width(60.dp))
                NavIconItem(icon = Icons.Default.Person, label = "Profil", onClick = { onNavigate("profile") })
            }
        }
        Box(
            modifier = Modifier.align(Alignment.TopCenter).offset(y = 10.dp).size(80.dp)
                .clip(CircleShape).background(Color.White).padding(6.dp)
                .clip(CircleShape).background(OrangePrimary)
                .clickable { onNavigate("calendar") },
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = Icons.Default.DateRange, contentDescription = "Takvim", tint = Color.White, modifier = Modifier.size(36.dp))
        }
    }
}

@Composable
fun NavIconItem(icon: ImageVector, label: String, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clickable { onClick() }) {
        Box(modifier = Modifier.size(40.dp).background(Color.White, shape = RoundedCornerShape(10.dp)), contentAlignment = Alignment.Center) {
            Icon(imageVector = icon, contentDescription = label, tint = OrangePrimary, modifier = Modifier.size(28.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMenuScreen() {
    // Preview için boş bir onNavigate veriyoruz
    MenuScreen(onNavigate = {})
}