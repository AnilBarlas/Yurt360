package com.example.yurt360.admin.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.yurt360.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnnouncementDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    val backgroundColor = Color(0xFFF5F5F5)

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .clip(RoundedCornerShape(50.dp))
                .background(backgroundColor)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Duyuru Yayınla", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(10.dp))

            // Başlık Alanı
            TextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Başlık") },
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
                    .background(Color.White, RoundedCornerShape(20.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(20.dp)
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Açıklama Alanı
            TextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Duyuru Detayı") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(20.dp))
                    .background(Color.White, RoundedCornerShape(20.dp)),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    disabledContainerColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
                shape = RoundedCornerShape(20.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Gönder Butonu
            IconButton(
                onClick = { if(title.isNotBlank()) onConfirm(title, description) },
                modifier = Modifier
                    .align(Alignment.End)
                    .size(50.dp)
            ) {
                Icon(
                    painterResource(id = R.drawable.arrowdowncircle),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(40.dp)
                )
            }
        }
    }
}