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

@Composable
fun ApplicationsScreen() {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // TITLE
        Text(
            text = "BAŞVURULAR",
            fontSize = 24.sp,
            fontWeight = FontWeight.Normal,
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
            // SHOW DROPDOWN MENU
            val subMenuModifier = Modifier.fillMaxWidth()

            MenuButton(text = "Alt Başvuru 1", modifier = subMenuModifier) {}
            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(text = "Alt Başvuru 2", modifier = subMenuModifier) {}
            Spacer(modifier = Modifier.height(16.dp))

            MenuButton(text = "Alt Başvuru 3", modifier = subMenuModifier) {}

        } else {
            // SHOW ORIGINAL BOTTOM BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
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

@Composable
fun MenuButton(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = Color.White,
    contentColor: Color = Color.Black,
    onClick: () -> Unit
) {
    val buttonShape = RoundedCornerShape(20.dp)

    // Using Button instead of ElevatedButton + explicit shadow modifier ensures
    // the shadow shape matches the button shape perfectly.
    Button(
        onClick = onClick,
        modifier = modifier
            .height(60.dp)
            .shadow(elevation = 4.dp, shape = buttonShape), // Shadow with explicit shape
        shape = buttonShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor
        ),
        elevation = null // Disable default elevation to rely on the shadow modifier
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ApplicationsScreenPreview() {
    MaterialTheme {
        ApplicationsScreen()
    }
}