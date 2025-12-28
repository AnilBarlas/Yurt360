package com.example.yurt360.common.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*
import com.example.yurt360.common.utils.OrangePrimary
import com.example.yurt360.common.utils.purpleLinear

// Renk Tanımları
val LightBackground = Color(0xFFF8F9FA)
val SelectedBlue = Color(0xFF8E99F3)
val LinePurple = Color(0xFFB0B7FF)

// Etkinlik Veri Modeli
data class Event(
    val title: String,
    val subtitle: String,
    val time: String,
    val isSelected: Boolean = false
)

@Composable
fun CalendarScreen(onNavigate: (String) -> Unit) {
    val currentDate = LocalDate.now()
    val monthName = currentDate.month.getDisplayName(TextStyle.FULL, Locale("tr")).uppercase()

    val todayEvents = listOf(
        Event("Library", "Reservation", "09:00"),
        Event("Laundry Room", "Reservation to Pick Up", "13:00", isSelected = true),
        Event("Food", "What the Food!", "16:00"),
        Event("Room Meet", "Reservation", "18:00"),
        Event("Food", "What the Food!", "20:00")
    )

    Scaffold(
        bottomBar = {
            CustomBottomNavigationBar(onNavigate = onNavigate)
        },
        containerColor = LightBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // 1. Ay İsmi (Padding buraya özel)
            Text(
                text = monthName,
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2D2D2D),
                letterSpacing = 2.sp,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Çizgi (Sabit genişlik: Tue-Fri arası görünüm)
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(LinePurple)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Takvim Kartı (Padding buraya özel)
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                CalendarGridCard(currentDate.dayOfMonth)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Today Paneli (Padding YOK - Kenarlara değer)
            TodayUnifiedCard(events = todayEvents)

            // Listenin en altına boşluk
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun TodayUnifiedCard(events: List<Event>) {
    // Şekil: Üst köşeler 90dp, altlar 0dp
    val specialShape = RoundedCornerShape(
        topStart = 90.dp,
        topEnd = 90.dp,
        bottomEnd = 0.dp,
        bottomStart = 0.dp
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .shadow(
                elevation = 15.dp,
                shape = specialShape,
                spotColor = Color.Black.copy(alpha = 0.25f)
            ),
        color = Color.White,
        shape = specialShape
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // Başlık: Ortalanmış
            Text(
                text = "Today",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(), // Tüm genişliği kapla
                textAlign = TextAlign.Center        // Yazıyı ortala
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (events.isEmpty()) {
                Text(
                    text = "Bugün için bir plan bulunmuyor.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                events.forEachIndexed { index, event ->
                    ActivityItemRow(event)
                    if (index < events.size - 1) {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityItemRow(event: Event) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(2.5.dp, OrangePrimary, CircleShape)
                .background(
                    if (event.isSelected) OrangePrimary else Color.Transparent,
                    CircleShape
                )
        )

        Spacer(modifier = Modifier.width(16.dp))

        Surface(
            modifier = Modifier.weight(1f),
            color = if (event.isSelected) SelectedBlue else Color(0xFFF3F4F6),
            shape = RoundedCornerShape(18.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = event.title,
                        fontWeight = FontWeight.Bold,
                        color = if (event.isSelected) Color.White else Color.Black
                    )
                    Text(
                        text = event.subtitle,
                        fontSize = 12.sp,
                        color = if (event.isSelected) Color.White.copy(alpha = 0.8f) else Color.Gray
                    )
                }
                Text(
                    text = event.time,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (event.isSelected) Color.White else Color.DarkGray
                )
            }
        }
    }
}

@Composable
fun CalendarGridCard(todayDay: Int) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val dayLabels = listOf("MON", "TUE", "WED", "THU", "FRI", "SAT", "SUN")
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                dayLabels.forEach { label ->
                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            val calendarDays = (1..31).chunked(7)
            calendarDays.forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { day ->
                        val isToday = day == todayDay
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .then(
                                    if (isToday) Modifier.border(
                                        4.dp, //Halka kalınlığı
                                        OrangePrimary,
                                        CircleShape
                                    )
                                    else Modifier
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                fontSize = 14.sp,
                                fontWeight = if (isToday) FontWeight.Bold else FontWeight.Normal,
                                color = Color.Black // Yazı rengi
                            )
                        }
                    }
                    if (week.size < 7) {
                        repeat(7 - week.size) { Spacer(modifier = Modifier.size(34.dp)) }
                    }
                }
            }
        }
    }
}