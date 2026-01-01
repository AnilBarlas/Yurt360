package com.example.yurt360.user.workSpace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Parametre alan workspace rezervasyon bileşeni.
 *
 * @param selectedHour Seçili saat indeksi
 * @param topStates 12 saat × 20 oda durumu listesi ("available" / "reserved")
 * @param activeTop Şu an seçili oda indeksi (null ise seçili yok)
 * @param onSelect Oda seçimi callback (index)
 * @param onReserve Rezervasyon yap callback (index)
 */
@Composable
fun WorkSpaceRes3(
    selectedHour: Int,
    topStates: List<List<String>>,
    activeTop: Int?,
    onSelect: (Int) -> Unit,
    onReserve: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {

        Spacer(modifier = Modifier.height(8.dp))

        // Odalar 4 satır × 5 sütun
        for (row in 0 until 4) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                for (col in 0 until 5) {
                    val index = row * 5 + col
                    val state = topStates.getOrNull(selectedHour)?.getOrNull(index) ?: "available"

                    RoomBox(
                        roomId = index + 1,
                        state = state,
                        isSelected = activeTop == index
                    ) {
                        onSelect(index)
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Spacer(modifier = Modifier.height(12.dp))

        /*Button(
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            onClick = {
                if (activeTop != null) {
                    onReserve(activeTop)
                }
            },
            enabled = activeTop != null
        ) {
            Text(
                text = "Rezervasyon Yap",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }*/
    }
}

@Composable
fun RoomBox3(roomId: Int, state: String, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    val bgColor = when {
        state == "reserved" && !isSelected -> Color.Gray
        isSelected -> Color.Blue
        else -> Color.LightGray
    }
    val textColor = if (state == "reserved" && !isSelected) Color.White else Color.Black

    /*Box(
        modifier = Modifier
            .width(48.dp)
            .height(36.dp)
            .border(1.dp, Color.Black, shape)
            .background(bgColor, shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = roomId.toString(),
            color = textColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
    }*/
}
