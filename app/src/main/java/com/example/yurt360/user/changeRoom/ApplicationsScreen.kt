package com.example.yurt360.user.changeRoom

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.style.TextOverflow
import com.example.yurt360.common.utils.Geologica
import com.example.yurt360.common.components.CustomBottomNavigationBar

@Composable
fun ApplicationsScreen(onNavigate: (String) -> Unit) {
    var isExpanded by remember { mutableStateOf(false) }

    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(onNavigate = onNavigate)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding) // Important: Handles bottom bar spacing
                .padding(15.dp)
                .animateContentSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // TITLE
            Text(
                text = "BAŞVURULAR",
                fontSize = 24.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = Geologica,
                color = Color.DarkGray,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(48.dp))

            // MAIN TOGGLE BUTTON
            MenuButton(
                text = "Başvuru Oluştur",
                modifier = Modifier.fillMaxWidth(),
                containerColor = if (isExpanded) Color(0xFF7E87E0) else Color.White,
                contentColor = if (isExpanded) Color.White else Color.Black
            ) {
                isExpanded = !isExpanded
            }

            Spacer(modifier = Modifier.height(16.dp))

            // CONDITIONAL CONTENT
            if (isExpanded) {
                val subMenuModifier = Modifier.fillMaxWidth()

                MenuButton(text = "Oda Değişim Talebi Formları", modifier = subMenuModifier) {}
                Spacer(modifier = Modifier.height(10.dp))

                MenuButton(text = "Şikayet Formları", modifier = subMenuModifier) {}
                Spacer(modifier = Modifier.height(10.dp))

                MenuButton(text = "Öneri Formları", modifier = subMenuModifier) {}

            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    MenuButton(
                        text = "Güncel Başvurular",
                        modifier = Modifier.weight(1f)
                    ) {
                        // Handle Current Applications click
                    }

                    MenuButton(
                        text = "Geçmiş Başvurular",
                        modifier = Modifier.weight(1f)
                    ) {
                        // Handle Past Applications click
                    }
                }
            }
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
            .height(60.dp)
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