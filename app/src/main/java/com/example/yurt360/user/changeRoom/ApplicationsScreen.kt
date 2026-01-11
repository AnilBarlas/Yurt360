package com.example.yurt360.user.changeRoom

import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.common.components.UserBottomNavigationBar
import com.example.yurt360.common.utils.Geologica
import com.example.yurt360.model.ApplicationForm
import androidx.compose.foundation.background

enum class ActiveSection {
    NONE, CREATE, CURRENT, PAST
}

// To manage which form is currently visible
enum class ActiveForm {
    NONE, ROOM_CHANGE, COMPLAINT, SUGGESTION
}

@Composable
fun ApplicationsScreen(
    onNavigate: (String) -> Unit,
    viewModel: ApplicationsViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    var activeForm by remember { mutableStateOf(ActiveForm.NONE) }
    var activeSection by remember { mutableStateOf(ActiveSection.NONE) }

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

    // Wrapper for the Dropdown Box
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
                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                .padding(16.dp)
        ) {
            if (list.isEmpty()) {
                Text(emptyMessage, fontSize = 14.sp, color = Color.Gray)
            } else {
                list.forEach { app ->
                    // Use the new Item Composable here
                    ApplicationItem(app = app, isPastSection = isPastSection)

                    // Optional: Add space between items
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }

    when (activeForm) {
        ActiveForm.ROOM_CHANGE -> RoomChangeFormScreen(onNavigate, onBack = { activeForm = ActiveForm.NONE; viewModel.fetchUserApplications() })
        ActiveForm.COMPLAINT -> ComplaintFormScreen(onNavigate, onBack = { activeForm = ActiveForm.NONE; viewModel.fetchUserApplications() })
        ActiveForm.SUGGESTION -> SuggestionFormScreen(onNavigate, onBack = { activeForm = ActiveForm.NONE; viewModel.fetchUserApplications() })

        ActiveForm.NONE -> {
            Scaffold(
                bottomBar = { UserBottomNavigationBar(onNavigate = onNavigate) }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .animateContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(236.dp))
                    Text("BAŞVURULAR", modifier = Modifier.padding(horizontal = 12.dp), fontSize = 24.sp, fontWeight = FontWeight.Normal, fontFamily = Geologica, color = Color.DarkGray, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(42.dp))

                    // Buttons Logic (Same as before)
                    MenuButton("Başvuru Oluştur", Modifier.fillMaxWidth().padding(horizontal = 12.dp), getContainerColor(activeSection == ActiveSection.CREATE), getContentColor(activeSection == ActiveSection.CREATE)) {
                        activeSection = if (activeSection == ActiveSection.CREATE) ActiveSection.NONE else ActiveSection.CREATE
                        if (activeSection == ActiveSection.CREATE) closeAllDropdowns()
                    }

                    if (activeSection == ActiveSection.CREATE) {
                        Spacer(modifier = Modifier.height(16.dp))
                        val subMenuModifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                        MenuButton("Oda Değişim Talebi Formları", subMenuModifier) { activeForm = ActiveForm.ROOM_CHANGE }
                        Spacer(modifier = Modifier.height(10.dp))
                        MenuButton("Şikayet Formları", subMenuModifier) { activeForm = ActiveForm.COMPLAINT }
                        Spacer(modifier = Modifier.height(10.dp))
                        MenuButton("Öneri Formları", subMenuModifier) { activeForm = ActiveForm.SUGGESTION }
                    } else {
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth().padding(horizontal = 12.dp), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                            MenuButton("Güncel Başvurular", Modifier.weight(1f), getContainerColor(activeSection == ActiveSection.CURRENT), getContentColor(activeSection == ActiveSection.CURRENT)) {
                                activeSection = if (activeSection == ActiveSection.CURRENT) ActiveSection.NONE else ActiveSection.CURRENT
                                closeAllDropdowns()
                            }
                            MenuButton("Geçmiş Başvurular", Modifier.weight(1f), getContainerColor(activeSection == ActiveSection.PAST), getContentColor(activeSection == ActiveSection.PAST)) {
                                activeSection = if (activeSection == ActiveSection.PAST) ActiveSection.NONE else ActiveSection.PAST
                                closeAllDropdowns()
                            }
                        }

                        if (activeSection == ActiveSection.CURRENT || activeSection == ActiveSection.PAST) {
                            Spacer(modifier = Modifier.height(16.dp))
                            val subMenuModifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)
                            val isPast = activeSection == ActiveSection.PAST

                            // 1. Oda Değişimi
                            MenuButton("Oda Değişim Talebi Formları", subMenuModifier) {
                                showRoomChangeDropdown = !showRoomChangeDropdown
                                showComplaintDropdown = false; showSuggestionDropdown = false
                            }
                            if (showRoomChangeDropdown) {
                                val filtered = applications.filter { it.type == "Oda Değişimi" && (if (isPast) it.isApproved != null else it.isApproved == null) }
                                ApplicationDropdownContainer(filtered, if (isPast) "Geçmiş başvurunuz bulunmamaktadır." else "Bekleyen başvurunuz bulunmamaktadır.", isPast)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 2. Şikayet
                            MenuButton("Şikayet Formları", subMenuModifier) {
                                showComplaintDropdown = !showComplaintDropdown
                                showRoomChangeDropdown = false; showSuggestionDropdown = false
                            }
                            if (showComplaintDropdown) {
                                val filtered = applications.filter { it.type == "Şikayet" && (if (isPast) it.isApproved != null else it.isApproved == null) }
                                ApplicationDropdownContainer(filtered, if (isPast) "Geçmiş şikayetiniz bulunmamaktadır." else "Bekleyen şikayetiniz bulunmamaktadır.", isPast)
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            // 3. Öneri
                            MenuButton("Öneri Formları", subMenuModifier) {
                                showSuggestionDropdown = !showSuggestionDropdown
                                showRoomChangeDropdown = false; showComplaintDropdown = false
                            }
                            if (showSuggestionDropdown) {
                                val filtered = applications.filter { it.type == "Öneri" && (if (isPast) it.isApproved != null else it.isApproved == null) }
                                ApplicationDropdownContainer(filtered, if (isPast) "Geçmiş öneriniz bulunmamaktadır." else "Bekleyen öneriniz bulunmamaktadır.", isPast)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(50.dp))
                }
            }
        }
    }
}

// ROOM CHANGE FORM
@Composable
fun RoomChangeFormScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: RoomChangeViewModel = viewModel()

) {
    BackHandler { onBack() }

    var reasonText by remember { mutableStateOf("") }
    val submissionState by viewModel.submissionStatus.collectAsState()

    LaunchedEffect(submissionState) {
        if (submissionState is SubmissionState.Success) {
            onBack()
            viewModel.resetState()
        }
    }

    FormLayout(
        title = "ODA DEĞİŞİM TALEBİ FORMU",
        subTitle = "Oda Değişme Talebinizin Sebebini Belirtiniz",
        inputText = reasonText,
        onInputChanged = { reasonText = it },
        onSubmit = { viewModel.submitForm(reasonText, "Oda Değişimi") },
        submissionState = submissionState,
        onNavigate = onNavigate
    )
}

// COMPLAINT FORM (ŞİKAYET)
@Composable
fun ComplaintFormScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: RoomChangeViewModel = viewModel()
) {
    BackHandler { onBack() }

    var reasonText by remember { mutableStateOf("") }
    val submissionState by viewModel.submissionStatus.collectAsState()

    LaunchedEffect(submissionState) {
        if (submissionState is SubmissionState.Success) {
            onBack()
            viewModel.resetState()
        }
    }

    FormLayout(
        title = "ŞİKAYET FORMU",
        subTitle = "Şikayetinizin Sebebini Belirtiniz",
        inputText = reasonText,
        onInputChanged = { reasonText = it },
        onSubmit = { viewModel.submitForm(reasonText, "Şikayet") },
        submissionState = submissionState,
        onNavigate = onNavigate
    )
}

// SUGGESTION FORM (ÖNERİ)
@Composable
fun SuggestionFormScreen(
    onNavigate: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: RoomChangeViewModel = viewModel()
) {
    BackHandler { onBack() }

    var reasonText by remember { mutableStateOf("") }
    val submissionState by viewModel.submissionStatus.collectAsState()

    LaunchedEffect(submissionState) {
        if (submissionState is SubmissionState.Success) {
            onBack()
            viewModel.resetState()
        }
    }

    FormLayout(
        title = "ÖNERİ FORMU",
        subTitle = "Önerinizin Sebebini Belirtiniz",
        inputText = reasonText,
        onInputChanged = { reasonText = it },
        onSubmit = { viewModel.submitForm(reasonText, "Öneri") },
        submissionState = submissionState,
        onNavigate = onNavigate
    )
}

// REUSABLE FORM LAYOUT
@Composable
fun FormLayout(
    title: String,
    subTitle: String,
    inputText: String,
    onInputChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    submissionState: SubmissionState,
    onNavigate: (String) -> Unit
) {
    Scaffold(
        bottomBar = {
            UserBottomNavigationBar(onNavigate = onNavigate)
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
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                fontFamily = Geologica,
                color = Color(0xFF333333),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subTitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = Geologica,
                fontStyle = FontStyle.Italic,
                color = Color.Gray,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(350.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(12.dp)),
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            ) {
                TextField(
                    value = inputText,
                    onValueChange = onInputChanged,
                    modifier = Modifier.fillMaxSize(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        disabledContainerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = Color(0xFF7E87E0)
                    ),
                    textStyle = LocalTextStyle.current.copy(
                        fontFamily = Geologica,
                        fontSize = 14.sp
                    ),
                    enabled = submissionState !is SubmissionState.Loading
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Error Display
            if (submissionState is SubmissionState.Error) {
                Text(
                    text = (submissionState as SubmissionState.Error).message,
                    color = Color.Red,
                    fontSize = 12.sp,
                    fontFamily = Geologica
                )
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Submit Button
            Button(
                onClick = {
                    if (inputText.isNotBlank()) onSubmit()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(20.dp),
                        clip = false,
                        spotColor = Color(0xFF7E87E0)
                    ),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF9098E0)
                ),
                enabled = submissionState !is SubmissionState.Loading
            ) {
                if (submissionState is SubmissionState.Loading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Başvuru Oluştur",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = Geologica,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(40.dp))
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
        modifier = modifier
            .height(48.dp)
            .shadow(
                elevation = 8.dp,
                shape = buttonShape,
                clip = false,
                ambientColor = Color.Transparent,
                spotColor = Color.Black
            ),
        shape = buttonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = null
    ) {
        Text(
            text = text,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium,
            fontFamily = Geologica,
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}