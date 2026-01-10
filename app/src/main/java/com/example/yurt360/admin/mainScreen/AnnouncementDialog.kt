package com.example.yurt360.admin.mainScreen

import android.graphics.drawable.Icon
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.R

@Composable
fun AnnouncementDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(30.dp))
                .background(Color.White)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Duyuru Yayınla", fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))

            // Başlık Alanı
            androidx.compose.material3.OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Başlık") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(10.dp))

            // Açıklama Alanı
            androidx.compose.material3.OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Duyuru Detayı") },
                modifier = Modifier.fillMaxWidth().height(120.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Gönder Butonu
            androidx.compose.material3.IconButton(
                onClick = { if(title.isNotBlank()) onConfirm(title, description) },
                modifier = Modifier.align(Alignment.End).background(Color(0xFF7B7BFF), CircleShape)
            ) {
                Icon(
                    painterResource(id = R.drawable.arrowdowncircle),
                    contentDescription = null,
                    tint = Color.Unspecified
                )
            }
        }
    }
}