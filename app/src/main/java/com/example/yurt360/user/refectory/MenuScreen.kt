package com.example.yurt360.user.refectory
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.common.components.UserBottomNavigationBar // Ortak bileşen import edildi
import com.example.yurt360.common.model.Menu

// --- Renk Tanımları ---
private val OrangePrimary = Color(0xFFF27A39)
private val BackgroundColor = Color(0xFFF9F9F9)
private val TextDarkGray = Color(0xFF333333)
private val SelectedBlue = Color(0xFF7B85D8)

@Composable
fun MenuScreen(
    onNavigate: (String) -> Unit,
    viewModel: MenuViewModel = viewModel()
) {
    val menuList by viewModel.menuList.collectAsState()
    val selectedMenu by viewModel.selectedMenu.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            UserBottomNavigationBar(onNavigate = onNavigate)
        }
    ) { innerPadding ->

        Column(modifier = Modifier
            .fillMaxSize()
            .padding(
                top = innerPadding.calculateTopPadding(),
                bottom = innerPadding.calculateBottomPadding()
            )
        ) {

            // Geri Butonu
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Geri",
                modifier = Modifier
                    .size(32.dp)
                    .clickable {
                        onNavigate("home")
                    },
                tint = Color.Black
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 20.dp)
                ) {
                    item {
                        selectedMenu?.let { menu ->
                            MenuCardItem(menu = menu)
                        } ?: Text(
                            "Henüz menü eklenmemiş.",
                            modifier = Modifier.fillMaxWidth().padding(20.dp),
                            textAlign = TextAlign.Center
                        )
                    }

                    // 2. Alttaki Tarih Listesi
                    items(menuList) { menu ->
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

            Text(
                text = menu.date,
                fontSize = 16.sp,
                fontStyle = FontStyle.Italic,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            // Divider yerine HorizontalDivider kullanıldı (M3 uyumu)
            HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp).width(200.dp), thickness = 1.dp, color = Color.LightGray)

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
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp), contentAlignment = Alignment.CenterStart) {
            Text(
                text = menu.date,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) SelectedBlue else TextDarkGray
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewMenuScreen() {
    MenuScreen(onNavigate = {})
}