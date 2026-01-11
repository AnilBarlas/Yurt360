package com.example.yurt360.admin.refectory

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.R
import com.example.yurt360.common.model.Menu
import com.example.yurt360.user.refectory.AdminMenuViewModel
import com.example.yurt360.common.components.CustomAdminBottomNavigationBar

// --- RENKLER ---
private val OrangePrimary = Color(0xFFF27A39)
private val BackgroundColor = Color(0xFFF9F9F9)
private val TextDarkGray = Color(0xFF333333) // Siyah/Koyu Gri (Kapalı liste için)
private val LightBlueIcon = Color(0xFF7B85D8) // Mavimsi (Seçili olan ve İkonlar için)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminMenuScreen(
    onNavigateBack: () -> Unit,
    viewModel: AdminMenuViewModel = viewModel()
) {
    val menuList by viewModel.menuList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val context = LocalContext.current

    var expandedMenuId by remember { mutableStateOf<Long?>(null) }
    var editedFoods by remember { mutableStateOf("") }

    Scaffold(
        containerColor = BackgroundColor,
        bottomBar = {
            CustomAdminBottomNavigationBar(
                onNavigate = { route ->
                    when (route) {
                        "home" -> onNavigateBack()
                        "calendar" -> {
                            Toast.makeText(context, "Duyuru Paneli", Toast.LENGTH_SHORT).show()
                        }
                        "profile" -> { /* Profil */ }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {

            // --- ÜST KISIM ---
            Box(modifier = Modifier.fillMaxWidth()) {
                UserStyleTopHeader()

                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier
                        .padding(top = 40.dp, start = 16.dp)
                        .align(Alignment.TopStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Geri",
                        tint = TextDarkGray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- LİSTE ---
            if (menuList.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Henüz menü eklenmemiş.", color = Color.Gray)
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(menuList) { menu ->
                        if (menu.id == expandedMenuId) {
                            // --- SEÇİLİ (Expanded) ---
                            AdminMenuItemExpanded(
                                menu = menu,
                                currentFoods = editedFoods,
                                onFoodsChange = { editedFoods = it },
                                onSave = {
                                    viewModel.addMenu(menu.date, editedFoods) {
                                        Toast.makeText(context, "Menü Güncellendi", Toast.LENGTH_SHORT).show()
                                        expandedMenuId = null
                                    }
                                },
                                onDelete = {
                                    viewModel.deleteMenu(menu.id)
                                    Toast.makeText(context, "Menü Silindi", Toast.LENGTH_SHORT).show()
                                    expandedMenuId = null
                                },
                                onCollapse = { expandedMenuId = null }
                            )
                        } else {
                            // --- SEÇİLİ DEĞİL (Collapsed) ---
                            AdminMenuItemCollapsed(
                                menu = menu,
                                onExpand = {
                                    expandedMenuId = menu.id
                                    editedFoods = menu.foods
                                }
                            )
                        }
                    }
                    item { Spacer(modifier = Modifier.height(20.dp)) }
                }
            }
        }
    }
}

// --- BİLEŞENLER ---

@Composable
fun UserStyleTopHeader() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .shadow(elevation = 10.dp, shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp))
            .background(Color.White, shape = RoundedCornerShape(bottomStart = 50.dp, bottomEnd = 50.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = "BUGÜNÜN MENÜSÜ",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkGray
            )
            Text(
                text = "Yönetim Paneli",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(modifier = Modifier.width(200.dp), thickness = 1.dp, color = Color.LightGray)
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Menüleri düzenlemek için\nlütfen listeden bir tarih seçiniz.",
                fontSize = 14.sp,
                color = TextDarkGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
        }
    }
}

@Composable
fun AdminMenuItemCollapsed(menu: Menu, onExpand: () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpand() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // DEĞİŞİKLİK BURADA: Seçili olmadığı için renk TextDarkGray (Siyah)
            Text(
                text = menu.date,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextDarkGray
            )

            Icon(
                painter = painterResource(id = R.drawable.edit_icon),
                contentDescription = "Düzenle",
                tint = LightBlueIcon,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
fun AdminMenuItemExpanded(
    menu: Menu,
    currentFoods: String,
    onFoodsChange: (String) -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onCollapse: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // DEĞİŞİKLİK BURADA: Seçili olduğu için renk LightBlueIcon (Mavi)
                Text(
                    text = menu.date,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = LightBlueIcon
                )
                IconButton(onClick = onCollapse, modifier = Modifier.size(24.dp)) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Kapat", tint = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = currentFoods,
                onValueChange = onFoodsChange,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = OrangePrimary,
                    unfocusedBorderColor = Color.LightGray,
                    focusedContainerColor = Color(0xFFFAFAFA),
                    unfocusedContainerColor = Color(0xFFFAFAFA)
                ),
                textStyle = LocalTextStyle.current.copy(fontSize = 15.sp, lineHeight = 22.sp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onDelete,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sil", color = Color.Red)
                }
                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.weight(2f)
                ) {
                    Text("Menüyü Kaydet", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}