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
import com.example.yurt360.common.components.CustomBottomNavigationBar
import com.example.yurt360.common.utils.Geologica

enum class ActiveSection {
    NONE, CREATE, CURRENT, PAST
}

// To manage which form is currently visible
enum class ActiveForm {
    NONE, ROOM_CHANGE, COMPLAINT, SUGGESTION
}

@Composable
fun ApplicationsScreen(onNavigate: (String) -> Unit) {
    var activeForm by remember { mutableStateOf(ActiveForm.NONE) }
    var activeSection by remember { mutableStateOf(ActiveSection.NONE) }

    fun getContainerColor(isActive: Boolean) = if (isActive) Color(0xFF7E87E0) else Color.White
    fun getContentColor(isActive: Boolean) = if (isActive) Color.White else Color.Black

    // Decide which screen to show
    when (activeForm) {
        ActiveForm.ROOM_CHANGE -> {
            RoomChangeFormScreen(
                onNavigate = onNavigate,
                onBack = { activeForm = ActiveForm.NONE }
            )
        }
        ActiveForm.COMPLAINT -> {
            ComplaintFormScreen(
                onNavigate = onNavigate,
                onBack = { activeForm = ActiveForm.NONE }
            )
        }
        ActiveForm.SUGGESTION -> {
            SuggestionFormScreen(
                onNavigate = onNavigate,
                onBack = { activeForm = ActiveForm.NONE }
            )
        }
        ActiveForm.NONE -> {
            Scaffold(
                bottomBar = {
                    CustomBottomNavigationBar(onNavigate = onNavigate)
                }
            ) { innerPadding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(rememberScrollState())
                        .animateContentSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Spacer(modifier = Modifier.height(236.dp))

                    // TITLE
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

                    // CREATE APPLICATION BUTTON
                    MenuButton(
                        text = "Başvuru Oluştur",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp),
                        containerColor = getContainerColor(activeSection == ActiveSection.CREATE),
                        contentColor = getContentColor(activeSection == ActiveSection.CREATE)
                    ) {
                        activeSection =
                            if (activeSection == ActiveSection.CREATE) ActiveSection.NONE else ActiveSection.CREATE
                    }

                    // LOGIC SPLIT
                    if (activeSection == ActiveSection.CREATE) {
                        Spacer(modifier = Modifier.height(16.dp))
                        val subMenuModifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)

                        MenuButton(
                            text = "Oda Değişim Talebi Formları",
                            modifier = subMenuModifier
                        ) {
                            activeForm = ActiveForm.ROOM_CHANGE
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        MenuButton(
                            text = "Şikayet Formları",
                            modifier = subMenuModifier
                        ) {
                            activeForm = ActiveForm.COMPLAINT
                        }

                        Spacer(modifier = Modifier.height(10.dp))
                        MenuButton(
                            text = "Öneri Formları",
                            modifier = subMenuModifier
                        ) {
                            activeForm = ActiveForm.SUGGESTION
                        }

                    } else {
                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            MenuButton(
                                text = "Güncel Başvurular",
                                modifier = Modifier.weight(1f),
                                containerColor = getContainerColor(activeSection == ActiveSection.CURRENT),
                                contentColor = getContentColor(activeSection == ActiveSection.CURRENT)
                            ) {
                                activeSection =
                                    if (activeSection == ActiveSection.CURRENT) ActiveSection.NONE else ActiveSection.CURRENT
                            }

                            MenuButton(
                                text = "Geçmiş Başvurular",
                                modifier = Modifier.weight(1f),
                                containerColor = getContainerColor(activeSection == ActiveSection.PAST),
                                contentColor = getContentColor(activeSection == ActiveSection.PAST)
                            ) {
                                activeSection =
                                    if (activeSection == ActiveSection.PAST) ActiveSection.NONE else ActiveSection.PAST
                            }
                        }

                        // DROPDOWNS FOR OTHER SECTIONS
                        if (activeSection == ActiveSection.CURRENT || activeSection == ActiveSection.PAST) {
                            Spacer(modifier = Modifier.height(16.dp))
                            val subMenuModifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp)

                            MenuButton(
                                text = "Oda Değişim Talebi Formları",
                                modifier = subMenuModifier
                            ) {}
                            Spacer(modifier = Modifier.height(10.dp))
                            MenuButton(text = "Şikayet Formları", modifier = subMenuModifier) {}
                            Spacer(modifier = Modifier.height(10.dp))
                            MenuButton(text = "Öneri Formları", modifier = subMenuModifier) {}
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
            CustomBottomNavigationBar(onNavigate = onNavigate)
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