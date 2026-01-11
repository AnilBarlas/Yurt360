package com.example.yurt360.admin.changeRoom

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.model.ApplicationForm
import com.example.yurt360.user.changeRoom.formatDate

@Composable
fun AdminApplicationItem(
    app: ApplicationForm,
    onClick: () -> Unit
) {
    val formattedDate = formatDate(app.createdAt)
    val studentName = "${app.profile?.firstName ?: "Bilinmeyen"} ${app.profile?.lastName ?: "Öğrenci"}"

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Student Name Box
            Surface(
                modifier = Modifier
                    .width(236.dp)
                    .height(40.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(10.dp), clip = false),
                shape = RoundedCornerShape(10.dp),
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = studentName, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            // 2. Date Box
            Surface(
                modifier = Modifier
                    .width(111.dp)
                    .height(40.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(10.dp), clip = false),
                shape = RoundedCornerShape(10.dp),
                color = Color.White
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(text = formattedDate, fontSize = 13.sp, fontWeight = FontWeight.Normal, color = Color.Black)
                }
            }
        }
        // Removed the "Expanded" section completely
    }
}