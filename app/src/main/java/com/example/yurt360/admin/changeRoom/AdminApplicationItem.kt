package com.example.yurt360.admin.changeRoom

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.model.ApplicationForm
import com.example.yurt360.common.utils.Geologica // Make sure your font import is correct
import com.example.yurt360.user.changeRoom.formatDate

@Composable
fun AdminApplicationItem(
    app: ApplicationForm,
    isSelected: Boolean, // <--- ADDED THIS PARAMETER
    onClick: () -> Unit
) {
    val formattedDate = formatDate(app.createdAt)
    val studentName = "${app.profile?.firstName ?: "Bilinmeyen"} ${app.profile?.lastName ?: "Öğrenci"}"

    // CHANGE COLOR IF SELECTED
    val boxColor = if (isSelected) Color(0xFFD1E4FF) else Color.White

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(21.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. Student Name Box
            Surface(
                modifier = Modifier
                    .width(236.dp)
                    .height(47.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp), clip = false),
                shape = RoundedCornerShape(12.dp),
                color = boxColor // <--- USE THE DYNAMIC COLOR
            ) {
                Box(
                    contentAlignment = Alignment.CenterStart,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onClick() }
                        .padding(start = 12.dp)
                ) {
                    Text(
                        text = studentName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Geologica,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            // 2. Date Box
            Surface(
                modifier = Modifier
                    .width(111.dp)
                    .height(47.dp)
                    .shadow(elevation = 2.dp, shape = RoundedCornerShape(12.dp), clip = false),
                shape = RoundedCornerShape(12.dp),
                color = boxColor
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                        text = formattedDate,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = Geologica,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}
