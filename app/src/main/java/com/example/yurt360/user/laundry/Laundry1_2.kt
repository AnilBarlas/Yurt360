/*package com.example.yurt360.user.laundry

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
//import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.yurt360.common.components.CustomBottomNavigationBar
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

// Renkler
private val SelectedDayColor = Color(0xFF7E87E2)
private val NonSelectedDayColor = Color(0xFFB0B7FF)
private val ThinBorderColor = Color(0x33000000)
private val DayTextColor = Color.White

private val TopDefaultBg = Color(0xFFF0EAE1)

// Turuncu tonları
private val LightOrange = Color(0xFFFFD8A6)
private val MidOrange = Color(0xFFFFB378)
private val DarkOrange = Color(0xFFFF8C42)
private val ActiveColor = DarkOrange
private val ReservedBorderColor = Color(0xFFBDBDBD)
private val DefaultHourTextColor = Color.Black

@Composable
fun Laundry1_2() {
    val fixedSelectedDay = 2 // ortadaki kare sabit seçili

    var selectedHour by remember { mutableStateOf(0) }
    var activeTop by remember { mutableStateOf<Int?>(null) }

    var topStates by remember {
        mutableStateOf(List(5) { List(12) { List(10) { "available" } } })
    }

    // dialogState: null | "confirm_plan" | "created" | "removed"
    var dialogState by remember { mutableStateOf<String?>(null) }

    val zone = ZoneId.of("Europe/Istanbul")
    val today = remember { LocalDate.now(zone) }
    val days = remember(today) {
        (-2..2).map { offset ->
            val date = today.plusDays(offset.toLong())
            val shortEn = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH)
            val fullEn = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
            DayInfo(number = date.dayOfMonth, short = shortEn, full = fullEn)
        }
    }

    // Turuncu gradyan (dialog / gerekiyorsa)
    val orangeGradient = Brush.horizontalGradient(
        colorStops = arrayOf(
            0.0f to DarkOrange,
            0.6f to MidOrange,
            1.0f to LightOrange
        )
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.matchParentSize(),
            bottomBar = {
                CustomBottomNavigationBar(onNavigate = { /*...*/ })
            }
        ) { paddingVals ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingVals)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Üst gün kutuları (aynı)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Top
                    ) {
                        val baseWeight = 1f
                        val centerWeight = 1.20f
                        val centerOffsetUp = 14.dp

                        days.forEachIndexed { index, info ->
                            val weight = if (index == fixedSelectedDay) centerWeight else baseWeight
                            val scaleFactor = weight / baseWeight
                            val columnOffset = if (index == fixedSelectedDay) (-centerOffsetUp) else 0.dp

                            Column(
                                modifier = Modifier
                                    .weight(weight)
                                    .padding(horizontal = 6.dp)
                                    .offset(y = columnOffset),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val corner = 14.dp
                                val dayShape = RoundedCornerShape(corner)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(dayShape)
                                        .background(
                                            color = if (index == fixedSelectedDay) SelectedDayColor else NonSelectedDayColor,
                                            shape = dayShape
                                        )
                                        .border(width = 1.dp, color = ThinBorderColor, shape = dayShape)
                                        .zIndex(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val baseFont = if (index == fixedSelectedDay) 36f else 28f
                                    Text(
                                        text = info.number.toString(),
                                        color = DayTextColor,
                                        fontSize = (baseFont * scaleFactor).sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                val tabShape = RoundedCornerShape(
                                    topStart = 0.dp,
                                    topEnd = 0.dp,
                                    bottomStart = corner,
                                    bottomEnd = corner
                                )
                                val tabOffset = -(corner * 0.85f)

                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(2f)
                                        .offset(y = tabOffset)
                                        .clip(tabShape)
                                        .background(color = Color.White, shape = tabShape)
                                        .border(width = 1.dp, color = ThinBorderColor, shape = tabShape)
                                        .zIndex(0f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val baseTabFont = if (index == fixedSelectedDay) 18f else 16f
                                    Text(
                                        text = info.short,
                                        color = Color.Black,
                                        fontSize = (baseTabFont * scaleFactor).sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier
                                            .padding(horizontal = 6.dp)
                                            .offset(y = 6.dp)
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Saatlar (aynı)
                    val hoursList = generateHourlyList(9, 21)
                    Column {
                        for (row in 0..2) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                for (col in 0..3) {
                                    val idx = row * 4 + col
                                    HourBox(
                                        hour = hoursList[idx],
                                        isSelected = selectedHour == idx,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(48.dp)
                                    ) {
                                        selectedHour = idx
                                        activeTop = null
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Kurutma Makinesi Doluluğu: BEYAZ arkaplan, sağ/sol kırpma (daha dar)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.94f) // sağdan/soldan hafif kırp
                            .height(56.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(width = 1.dp, color = ThinBorderColor, shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                            .align(Alignment.CenterHorizontally),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Kurutma Makinesi Doluluğu",
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Büyük kutu: içeriğe göre sıkışık (wrapContentHeight) -> gereksiz boşluk yok
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight()
                            .border(
                                width = 1.dp,
                                color = ThinBorderColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.wrapContentWidth(),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 2 satır x 5 sütun, kutular ortalanmış
                            for (row in 0 until 2) {
                                Row(
                                    modifier = Modifier.wrapContentWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    topStates[fixedSelectedDay][selectedHour]
                                        .chunked(5)[row]
                                        .forEachIndexed { colIndex, state ->
                                            val topIndex = row * 5 + colIndex
                                            TopSquare(
                                                index = topIndex + 1,
                                                state = state,
                                                isSelected = (activeTop == topIndex)
                                            ) {
                                                activeTop = if (activeTop == topIndex) null else topIndex
                                            }
                                        }
                                }
                            }
                        }
                    }

                    // Legend (Available / Reserved / Selected) — büyük karenin hemen altında
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .offset(y = (-4).dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(color = TopDefaultBg, shape = CircleShape)
                                    .border(width = 1.dp, color = ThinBorderColor, shape = CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Available", color = Color.Black, fontSize = 14.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(color = Color.White, shape = CircleShape)
                                    .border(width = 1.dp, color = ReservedBorderColor, shape = CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Reserved", color = Color.Black, fontSize = 14.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(color = ActiveColor, shape = CircleShape)
                                    .border(width = 1.dp, color = ThinBorderColor, shape = CircleShape)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Selected", color = Color.Black, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // İki sabit turuncu buton (yan yana) - butonlar daha yukarıda görünüyor artık
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 0.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Rezervasyon Oluştur -> önce onay dialog'u aç
                        SolidButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            color = ActiveColor,
                            shape = RoundedCornerShape(16.dp),
                            enabled = true,
                            onClick = {
                                val top = activeTop
                                if (top == null) return@SolidButton
                                if (topStates[fixedSelectedDay][selectedHour][top] == "reserved") return@SolidButton

                                // açılacak: plan onayı
                                dialogState = "confirm_plan"
                            }
                        ) {
                            Text("Rezervasyon Oluştur", color = Color.White)
                        }

                        // Rezervasyonu Kaldır (aynı davranış)
                        SolidButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            color = ActiveColor,
                            shape = RoundedCornerShape(16.dp),
                            enabled = true,
                            onClick = {
                                val hasAnyReservation = topStates.any { day ->
                                    day.any { hourList ->
                                        hourList.any { it == "reserved" }
                                    }
                                }

                                if (!hasAnyReservation) {
                                    return@SolidButton
                                }

                                topStates = List(5) { List(12) { List(10) { "available" } } }
                                dialogState = "removed"
                                activeTop = null
                            }
                        ) {
                            Text("Rezervasyonu Kaldır", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // Overlay dialog(s)
        if (dialogState != null) {
            // ekran karartma
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000))
                    .clickable { /* clickleri tüket */ }
            )

            // Dialog içeriği
            when (dialogState) {
                "confirm_plan" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .wrapContentHeight()
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(12.dp))
                            .padding(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Rezervasyonunuz planlanmıştır. Onaylıyor musunuz?",
                                color = Color.Black,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                // Evet
                                ThinButton(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    onClick = {
                                        // yeniden kontrol: seçili top olmalı
                                        val top = activeTop
                                        if (top == null) {
                                            dialogState = null
                                            return@ThinButton
                                        }
                                        // create reservation
                                        topStates = topStates.toMutableList().apply {
                                            this[fixedSelectedDay] = this[fixedSelectedDay].toMutableList().apply {
                                                this[selectedHour] = this[selectedHour].toMutableList().apply {
                                                    this[top] = "reserved"
                                                }
                                            }
                                        }
                                        activeTop = null
                                        // bir sonraki dialogu aç
                                        dialogState = "created"
                                    }
                                ) {
                                    Text(text = "Evet")
                                }

                                // Hayır
                                ThinButton(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(44.dp),
                                    onClick = {
                                        // dialogu kapat, rezervasyon yapılmasın
                                        dialogState = null
                                    }
                                ) {
                                    Text(text = "Hayır")
                                }
                            }
                        }
                    }
                }

                "created" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .wrapContentHeight()
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(12.dp))
                            .padding(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Rezervasyonunuz oluşturulmuştur. Ajandanızdan teslim alma saatinizi kontrol edebilirsiniz.",
                                color = Color.Black,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            ThinButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                onClick = { dialogState = null }
                            ) {
                                Text("Devam Et")
                            }
                        }
                    }
                }

                "removed" -> {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp)
                            .wrapContentHeight()
                            .align(Alignment.Center)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.White)
                            .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(12.dp))
                            .padding(18.dp)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(
                                text = "Rezervasyonunuz kaldırılmıştır.",
                                color = Color.Black,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth()
                            )

                            ThinButton(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(44.dp),
                                onClick = { dialogState = null }
                            ) {
                                Text("Devam Et")
                            }
                        }
                    }
                }
            }
        }
    }
}*/