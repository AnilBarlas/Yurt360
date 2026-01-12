package com.example.yurt360.user.changeRoom

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.common.model.ApplicationForm
import com.example.yurt360.user.changeRoom.formatDate

@Composable
fun ApplicationItem(
    app: ApplicationForm,
    isPastSection: Boolean
) {
    var isExpanded by remember { mutableStateOf(false) }
    val formattedDate = formatDate(app.createdAt)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.White, RoundedCornerShape(8.dp))
            .padding(12.dp)
            .animateContentSize()
    ) {
        // HEADER ROW
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT: Status
            if (isPastSection && app.isApproved != null) {
                // Past Applications: Approved/Denied
                Text(
                    text = if (app.isApproved == true) "Onaylandı" else "Reddedildi",
                    color = if (app.isApproved == true) Color(0xFF4CAF50) else Color(0xFFE53935), // Green vs Red
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            } else {
                // Current Applications: Pending
                Text(
                    text = "Beklemede",
                    color = Color(0xFFFFA000), // Orange for pending
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // RIGHT: Date
            Text(
                text = formattedDate,
                fontSize = 12.sp,
                color = Color.Gray,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // EXPANDABLE CONTENT
        if (isExpanded) {
            Text(
                text = app.message,
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        // TOGGLE BUTTON
        Text(
            text = if (isExpanded) "Başvuru mesajını gizle" else "Başvuru mesajını görüntüle",
            fontSize = 12.sp,
            color = Color(0xFF7E87E0),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clickable { isExpanded = !isExpanded }
        )
    }
}
