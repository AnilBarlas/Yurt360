package com.example.yurt360.user.mainScreen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.R
import com.example.yurt360.common.components.UserBottomNavigationBar
import com.example.yurt360.common.utils.OrangePrimary
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.*

// Renk Tanımları
val LightBackground = Color(0xFFF8F9FA)
val SelectedBlue = Color(0xFF8E99F3)
val LinePurple = Color(0xFFB0B7FF)

// UI Modeli
data class Event(
    val title: String,
    val subtitle: String,
    val time: String,
    val isSelected: Boolean = false
)

@Composable
fun CalendarScreen(
    onNavigate: (String) -> Unit,
    onBackClick: () -> Unit,
    viewModel: CalendarViewModel = viewModel() // ViewModel Enjekte Edildi
) {
    // ViewModel'den gelen State'i dinle
    val uiState by viewModel.uiState.collectAsState()

    val currentMonthName = uiState.selectedDate.month.getDisplayName(TextStyle.FULL, Locale("tr")).uppercase()

    Scaffold(
        bottomBar = {
            UserBottomNavigationBar(onNavigate = onNavigate)
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

            // 1. Üst Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.CenterStart)
                        .offset(y = (-35).dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.arrow), // Arrow ikonu kontrol edilmeli
                        contentDescription = "Geri",
                        tint = Color(0xFF2D2D2D),
                        modifier = Modifier.size(28.dp)
                    )
                }

                Text(
                    text = currentMonthName,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color(0xFF2D2D2D),
                    letterSpacing = 2.sp,
                    modifier = Modifier.align(Alignment.Center)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dekoratif Çizgi
            Box(
                modifier = Modifier
                    .width(150.dp)
                    .height(4.dp)
                    .clip(CircleShape)
                    .background(LinePurple)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // 2. Takvim Kartı
            Box(modifier = Modifier.padding(horizontal = 20.dp)) {
                CalendarGridCard(
                    selectedDate = uiState.selectedDate,
                    onDateClick = { newDate ->
                        viewModel.onDateSelected(newDate)
                    }
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 3. Today Paneli (Dinamik Veri ile)
            TodayUnifiedCard(
                events = uiState.events,
                isLoading = uiState.isLoading
            )

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun CalendarGridCard(
    selectedDate: LocalDate,
    onDateClick: (LocalDate) -> Unit
) {
    // Ayın günlerini hesapla
    val yearMonth = YearMonth.of(selectedDate.year, selectedDate.month)
    val daysInMonth = yearMonth.lengthOfMonth()
    val firstDayOfMonth = selectedDate.withDayOfMonth(1)

    // Basitlik için 1'den başlayıp gün sayısına kadar liste (Gerçek takvim hizalaması için dayOfWeek kullanılabilir)
    val calendarDays = (1..daysInMonth).chunked(7)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(10.dp, RoundedCornerShape(20.dp)),
        color = Color.White,
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            val dayLabels = listOf("PZT", "SAL", "ÇAR", "PER", "CUM", "CTS", "PAZ")

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                dayLabels.forEach { label ->
                    Text(label, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.Gray)
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            calendarDays.forEach { week ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    week.forEach { day ->
                        val currentDateInLoop = selectedDate.withDayOfMonth(day)
                        val isSelected = day == selectedDate.dayOfMonth

                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .then(
                                    if (isSelected) Modifier.border(4.dp, OrangePrimary, CircleShape)
                                    else Modifier
                                )
                                .clickable { onDateClick(currentDateInLoop) }, // Tıklanabilirlik eklendi
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = day.toString(),
                                fontSize = 14.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = Color.Black
                            )
                        }
                    }
                    // Satırı doldurmak için boşluk
                    if (week.size < 7) {
                        repeat(7 - week.size) { Spacer(modifier = Modifier.size(34.dp)) }
                    }
                }
            }
        }
    }
}

@Composable
fun TodayUnifiedCard(events: List<Event>, isLoading: Boolean) {
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
            Text(
                text = "Ajanda", // Başlık güncellendi
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isLoading) {
                // Yükleniyor Göstergesi
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = OrangePrimary)
                }
            } else if (events.isEmpty()) {
                // Boş Durum
                Text(
                    text = "Bu tarih için kayıtlı bir planınız yok.",
                    color = Color.Gray,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            } else {
                // Etkinlik Listesi
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