package com.example.yurt360.user.changeRoom

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.example.yurt360.common.utils.Geologica
import com.example.yurt360.common.components.CustomBottomNavigationBar

enum class ActiveSection {
    NONE, CREATE, CURRENT, PAST
}

@Composable
fun ApplicationsScreen(onNavigate: (String) -> Unit) {
    var activeSection by remember { mutableStateOf(ActiveSection.NONE) }

    fun getContainerColor(isActive: Boolean) = if (isActive) Color(0xFF7E87E0) else Color.White
    fun getContentColor(isActive: Boolean) = if (isActive) Color.White else Color.Black

    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(onNavigate = onNavigate)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                // REMOVED: .padding(horizontal = 12.dp) from here to fix shadow clipping
                .verticalScroll(rememberScrollState())
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(236.dp))

            // TITLE
            Text(
                text = "BAŞVURULAR",
                // ADDED: Padding here
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
                // ADDED: Padding here
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                containerColor = getContainerColor(activeSection == ActiveSection.CREATE),
                contentColor = getContentColor(activeSection == ActiveSection.CREATE)
            ) {
                activeSection = if (activeSection == ActiveSection.CREATE) ActiveSection.NONE else ActiveSection.CREATE
            }

            // LOGIC SPLIT
            if (activeSection == ActiveSection.CREATE) {
                Spacer(modifier = Modifier.height(16.dp))
                // ADDED: Padding to sub-menu modifier
                val subMenuModifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)

                MenuButton(text = "Oda Değişim Talebi Formları", modifier = subMenuModifier) {}
                Spacer(modifier = Modifier.height(10.dp))
                MenuButton(text = "Şikayet Formları", modifier = subMenuModifier) {}
                Spacer(modifier = Modifier.height(10.dp))
                MenuButton(text = "Öneri Formları", modifier = subMenuModifier) {}

            } else {
                Spacer(modifier = Modifier.height(16.dp))

                // "LIL BUTTONS" ROW
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        // ADDED: Padding here
                        .padding(horizontal = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MenuButton(
                        text = "Güncel Başvurular",
                        modifier = Modifier.weight(1f),
                        containerColor = getContainerColor(activeSection == ActiveSection.CURRENT),
                        contentColor = getContentColor(activeSection == ActiveSection.CURRENT)
                    ) {
                        activeSection = if (activeSection == ActiveSection.CURRENT) ActiveSection.NONE else ActiveSection.CURRENT
                    }

                    MenuButton(
                        text = "Geçmiş Başvurular",
                        modifier = Modifier.weight(1f),
                        containerColor = getContainerColor(activeSection == ActiveSection.PAST),
                        contentColor = getContentColor(activeSection == ActiveSection.PAST)
                    ) {
                        activeSection = if (activeSection == ActiveSection.PAST) ActiveSection.NONE else ActiveSection.PAST
                    }
                }

                // DROPDOWNS FOR LIL BUTTONS
                if (activeSection == ActiveSection.CURRENT) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // ADDED: Padding to sub-menu modifier
                    val subMenuModifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)

                    MenuButton(text = "Oda Değişim Talebi Formları", modifier = subMenuModifier) {}
                    Spacer(modifier = Modifier.height(10.dp))
                    MenuButton(text = "Şikayet Formları", modifier = subMenuModifier) {}
                    Spacer(modifier = Modifier.height(10.dp))
                    MenuButton(text = "Öneri Formları", modifier = subMenuModifier) {}
                }

                if (activeSection == ActiveSection.PAST) {
                    Spacer(modifier = Modifier.height(16.dp))
                    // ADDED: Padding to sub-menu modifier
                    val subMenuModifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp)

                    MenuButton(text = "Oda Değişim Talebi Formları", modifier = subMenuModifier) {}
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
            // shadow(clip = false) will now draw into the padding area provided by the modifier
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