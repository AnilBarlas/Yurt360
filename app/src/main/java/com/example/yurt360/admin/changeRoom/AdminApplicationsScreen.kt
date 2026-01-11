package com.example.yurt360.admin.changeRoom

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.common.components.CustomAdminBottomNavigationBar
import com.example.yurt360.common.utils.Geologica
import androidx.compose.foundation.clickable
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Text
import androidx.compose.ui.window.Dialog
import com.example.yurt360.common.model.ApplicationForm

enum class AdminActiveSection {
    NONE, CURRENT, PAST
}

@Composable
fun AdminApplicationsScreen(
    onNavigate: (String) -> Unit,
    viewModel: AdminApplicationsViewModel
) {
    var activeSection by remember { mutableStateOf(AdminActiveSection.NONE) }

    // Dropdown states
    var showRoomChangeDropdown by remember { mutableStateOf(false) }
    var showComplaintDropdown by remember { mutableStateOf(false) }
    var showSuggestionDropdown by remember { mutableStateOf(false) }

    val applications by viewModel.applications.collectAsState()

    fun closeAllDropdowns() {
        showRoomChangeDropdown = false
        showComplaintDropdown = false
        showSuggestionDropdown = false
    }

    fun getContainerColor(isActive: Boolean) = if (isActive) Color(0xFF7E87E0) else Color.White
    fun getContentColor(isActive: Boolean) = if (isActive) Color.White else Color.Black

    // Helper: Left-Aligned Button for Categories
    @Composable
    fun CategoryHeaderButton(text: String, onClick: () -> Unit) {
        val buttonShape = RoundedCornerShape(20.dp)
        Button(
            onClick = onClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .shadow(elevation = 8.dp, shape = buttonShape, clip = false),
            shape = buttonShape,
            colors = ButtonDefaults.buttonColors(containerColor = Color.White, contentColor = Color.Black),
            elevation = null
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.CenterStart // Align Left
            ) {
                Text(
                    text = text,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    fontFamily = Geologica,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }

    @Composable
    fun ApplicationDropdownContainer(
        list: List<ApplicationForm>,
        emptyMessage: String,
        isPastSection: Boolean
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .padding(top = 8.dp)
        ) {
            // Local state for the popup
            var showMatchDialog by remember { mutableStateOf(false) }

            if (list.isEmpty()) {
                Text(emptyMessage, fontSize = 14.sp, color = Color.Gray, modifier = Modifier.padding(16.dp))
            } else {
                // HEADER ROW
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(21.dp, Alignment.CenterHorizontally)
                ) {
                    Surface(
                        modifier = Modifier.width(236.dp).height(47.dp).shadow(4.dp, RoundedCornerShape(12.dp), false),
                        shape = RoundedCornerShape(12.dp), color = Color(0xFFE6E7FD)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Başvuran Öğrenci", fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = Geologica)
                        }
                    }
                    Surface(
                        modifier = Modifier.width(111.dp).height(47.dp).shadow(4.dp, RoundedCornerShape(12.dp), false),
                        shape = RoundedCornerShape(12.dp), color = Color(0xFFE6E7FD)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Text("Tarih", fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = Geologica)
                        }
                    }
                }

                // LIST ITEMS
                list.forEach { app ->
                    val isSelected = viewModel.selectedForMatching.contains(app)

                    AdminApplicationItem(
                        app = app,
                        isSelected = isSelected,
                        onClick = {
                            if (viewModel.isSelectionMode) {
                                viewModel.toggleSelection(app)
                            } else {
                                viewModel.selectedApplication = app
                                onNavigate("admin_application_detail")
                            }
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // --- CHECK: IS THIS A ROOM CHANGE LIST? ---
                val isRoomChangeList = list.firstOrNull()?.type == "Oda Değişimi"

                if (isRoomChangeList) {
                    // --- SEÇ BUTTON (Only for Room Change) ---
                    Spacer(modifier = Modifier.height(20.dp))

                    val buttonColor = if (viewModel.isSelectionMode) Color(0xFFAEB5FC) else Color.White
                    val buttonTextColor = Color.Black

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        Surface(
                            modifier = Modifier
                                .width(70.dp)
                                .height(47.dp)
                                .shadow(2.dp, RoundedCornerShape(12.dp), clip = false),
                            shape = RoundedCornerShape(12.dp),
                            color = buttonColor
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clickable {
                                        if (!viewModel.isSelectionMode) {
                                            viewModel.toggleSelectionMode()
                                        } else {
                                            if (viewModel.selectedForMatching.size == 2) {
                                                showMatchDialog = true
                                            } else {
                                                viewModel.toggleSelectionMode()
                                            }
                                        }
                                    }
                            ) {
                                Text(
                                    text = "Seç",
                                    fontSize = 15.sp,
                                    fontFamily = Geologica,
                                    color = buttonTextColor
                                )
                            }
                        }
                    }

                    // --- CONFIRMATION DIALOG ---
                    if (showMatchDialog) {
                        Dialog(onDismissRequest = { showMatchDialog = false }) {
                            Surface(
                                modifier = Modifier
                                    .width(380.dp)
                                    .height(202.dp)
                                    .shadow(8.dp, RoundedCornerShape(60.dp), clip = false),
                                shape = RoundedCornerShape(60.dp),
                                color = Color(0xFFFFFDFD).copy(alpha = 0.92f)
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 24.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.Center
                                ) {
                                    Text(
                                        text = "Seçtiğiniz başvurular eşleşecektir.\nOnaylıyor musunuz?",
                                        fontSize = 15.sp,
                                        fontFamily = Geologica,
                                        color = Color.Black,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )

                                    Spacer(modifier = Modifier.height(24.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.Center
                                    ) {
                                        // 1. YES (Evet)
                                        Surface(
                                            modifier = Modifier
                                                .width(137.dp)
                                                .height(54.dp)
                                                .shadow(4.dp, RoundedCornerShape(18.dp), clip = false)
                                                .clickable {
                                                    showMatchDialog = false
                                                    viewModel.toggleSelectionMode()
                                                    // TODO: Add database logic here
                                                },
                                            shape = RoundedCornerShape(18.dp),
                                            color = Color(0xFFFFFFFF)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "Evet",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = Geologica,
                                                    color = Color(0xFF0056D2) // Keeping blue text
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(16.dp))

                                        // 2. NO (Hayır)
                                        Surface(
                                            modifier = Modifier
                                                .width(137.dp)
                                                .height(54.dp)
                                                .shadow(4.dp, RoundedCornerShape(18.dp), clip = false)
                                                .clickable { showMatchDialog = false },
                                            shape = RoundedCornerShape(18.dp),
                                            color = Color(0xFFFFFFFF)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = "Hayır",
                                                    fontSize = 16.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    fontFamily = Geologica,
                                                    color = Color.Gray // Keeping gray text
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    Scaffold(
        bottomBar = { CustomAdminBottomNavigationBar(onNavigate = onNavigate) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = if (activeSection == AdminActiveSection.NONE) Arrangement.Center else Arrangement.Top
        ) {
            if (activeSection != AdminActiveSection.NONE) {
                Spacer(modifier = Modifier.height(100.dp))
            }

            Text(
                text = "BAŞVURULAR",
                modifier = Modifier.padding(horizontal = 12.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = Geologica,
                color = Color.DarkGray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(42.dp))

            // TOP BUTTONS (Güncel / Geçmiş)
            if (activeSection == AdminActiveSection.NONE) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    MenuButton("Güncel Başvurular", Modifier.width(181.dp), getContainerColor(false), getContentColor(false)) {
                        activeSection = AdminActiveSection.CURRENT; closeAllDropdowns()
                    }
                    MenuButton("Geçmiş Başvurular", Modifier.width(181.dp), getContainerColor(false), getContentColor(false)) {
                        activeSection = AdminActiveSection.PAST; closeAllDropdowns()
                    }
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MenuButton("Güncel Başvurular", Modifier.weight(1f), getContainerColor(activeSection == AdminActiveSection.CURRENT), getContentColor(activeSection == AdminActiveSection.CURRENT)) {
                        activeSection = if (activeSection == AdminActiveSection.CURRENT) AdminActiveSection.NONE else AdminActiveSection.CURRENT
                        closeAllDropdowns()
                    }
                    MenuButton("Geçmiş Başvurular", Modifier.weight(1f), getContainerColor(activeSection == AdminActiveSection.PAST), getContentColor(activeSection == AdminActiveSection.PAST)) {
                        activeSection = if (activeSection == AdminActiveSection.PAST) AdminActiveSection.NONE else AdminActiveSection.PAST
                        closeAllDropdowns()
                    }
                }
            }

            // --- CATEGORY SECTIONS ---
            if (activeSection == AdminActiveSection.CURRENT || activeSection == AdminActiveSection.PAST) {
                Spacer(modifier = Modifier.height(16.dp))
                val isPast = activeSection == AdminActiveSection.PAST

                // Logic: Check if any category is open to decide visibility
                val isAnyCategoryOpen = showRoomChangeDropdown || showComplaintDropdown || showSuggestionDropdown

                // Oda Değişimi
                if (!isAnyCategoryOpen || showRoomChangeDropdown) {
                    Box(Modifier.padding(horizontal = 12.dp)) {
                        CategoryHeaderButton("Oda Değişim Talebi Formları") {
                            showRoomChangeDropdown = !showRoomChangeDropdown
                            // No need to set others false here because they are hidden anyway
                        }
                    }
                    if (showRoomChangeDropdown) {
                        val filtered = applications.filter { it.type == "Oda Değişimi" && (if (isPast) it.isApproved != null else it.isApproved == null) }
                        ApplicationDropdownContainer(filtered, "Bu kategoride başvuru bulunmamaktadır.", isPast)
                    }
                }

                if (!showRoomChangeDropdown) Spacer(modifier = Modifier.height(10.dp))

                // Şikayet
                if (!isAnyCategoryOpen || showComplaintDropdown) {
                    Box(Modifier.padding(horizontal = 12.dp)) {
                        CategoryHeaderButton("Şikayet Formları") {
                            showComplaintDropdown = !showComplaintDropdown
                        }
                    }
                    if (showComplaintDropdown) {
                        val filtered = applications.filter { it.type == "Şikayet" && (if (isPast) it.isApproved != null else it.isApproved == null) }
                        ApplicationDropdownContainer(filtered, "Bu kategoride başvuru bulunmamaktadır.", isPast)
                    }
                }

                if (!showComplaintDropdown) Spacer(modifier = Modifier.height(10.dp))

                // Öneri
                if (!isAnyCategoryOpen || showSuggestionDropdown) {
                    Box(Modifier.padding(horizontal = 12.dp)) {
                        CategoryHeaderButton("Öneri Formları") {
                            showSuggestionDropdown = !showSuggestionDropdown
                        }
                    }
                    if (showSuggestionDropdown) {
                        val filtered = applications.filter { it.type == "Öneri" && (if (isPast) it.isApproved != null else it.isApproved == null) }
                        ApplicationDropdownContainer(filtered, "Bu kategoride başvuru bulunmamaktadır.", isPast)
                    }
                }
            }
            Spacer(modifier = Modifier.height(50.dp))
        }
    }
}

@Composable
fun MenuButton(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    contentColor: Color = Color.Black,
    onClick: () -> Unit
) {
    val buttonShape = RoundedCornerShape(20.dp)
    Button(
        onClick = onClick,
        modifier = modifier.height(48.dp).shadow(8.dp, buttonShape, clip = false),
        shape = buttonShape,
        colors = ButtonDefaults.buttonColors(containerColor = containerColor, contentColor = contentColor),
        elevation = null
    ) {
        Text(text, fontSize = 15.sp, fontWeight = FontWeight.Medium, fontFamily = Geologica, maxLines = 1, overflow = TextOverflow.Ellipsis)
    }
}