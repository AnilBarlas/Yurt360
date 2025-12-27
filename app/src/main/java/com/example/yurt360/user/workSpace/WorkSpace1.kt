package com.example.yurt360.user.workSpace

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
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
private val SelectedDayColor = Color(0xFF7E87E2) // RGB(126,135,226)
private val NonSelectedDayColor = Color(0xFFB0B7FF) // RGB(176,183,255)
private val ThinBorderColor = Color(0x33000000) // yarı saydam siyah (ince stroke)
private val DayTextColor = Color.White

// TopSquare default arkaplan: RGB(240,234,225) -> #F0EAE1
private val TopDefaultBg = Color(0xFFF0EAE1)

// Diğer renkler
private val WorkSpaceOrangePrimary = Color(0xFFFF8C42)
private val ActiveColor = WorkSpaceOrangePrimary
private val ReservedBorderColor = Color(0xFFBDBDBD)
private val DefaultHourTextColor = Color.Black

@Composable
fun WorkSpace1() {
    val fixedSelectedDay = 2 // ortadaki kare sabit seçili

    var selectedHour by remember { mutableStateOf(0) }
    var activeTop by remember { mutableStateOf<Int?>(null) }

    var topStates by remember {
        mutableStateOf(List(5) { List(12) { List(20) { "available" } } })
    }

    // confirmationType: null | "created" | "removed"
    var confirmationType by remember { mutableStateOf<String?>(null) }

    // Dinamik 5 günlük info (orta = bugün)
    val zone = ZoneId.of("Europe/Istanbul")
    val today = remember { LocalDate.now(zone) }
    val days = remember(today) {
        (-2..2).map { offset ->
            val date = today.plusDays(offset.toLong())
            val shortEn = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.ENGLISH) // Mon, Tue...
            val fullEn = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH) // Monday...
            DayInfo(number = date.dayOfMonth, short = shortEn, full = fullEn)
        }
    }

    // Kullanıcının verdiği mor tonları
    val lightPurple = Color(0xFFB6BCFE) // RGB(182,188,254)
    val darkPurple = Color(0xFF929AE9)  // RGB(146,154,233)
    val midPurple = Color(0xFFA4ABF3)   // ara ton

    // Root container: Box ile Scaffold'ı sarıyoruz, böylece overlay (dialog) Scaffold'ın üstünde (BottomBar dahil) gözükecek.
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.matchParentSize(),
            bottomBar = {
                CustomBottomNavigationBar(
                    onNavigate = { route ->
                        when (route) {
                            "home" -> { /* navController.navigate("home") */ }
                            "calendar" -> { /* navController.navigate("calendar") */ }
                            "profile" -> { /* navController.navigate("profile") */ }
                        }
                    }
                )
            }
        ) { paddingVals ->
            // İçerik
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingVals)
                    .padding(16.dp)
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // --- Üst: 5 kare (tam kare) + alt sekmeler ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 4.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        verticalAlignment = Alignment.Top
                    ) {
                        val baseWeight = 1f
                        val centerWeight = 1.20f // ortadaki daha belirgin geniş
                        val centerOffsetUp = 14.dp // (varsa) ortadaki sütunu yukarı kaydırma miktarı

                        days.forEachIndexed { index, info ->
                            val weight = if (index == fixedSelectedDay) centerWeight else baseWeight
                            val scaleFactor = weight / baseWeight

                            // Eğer ortadaki sütunsa tüm column'u yukarı kaydır (kutu+sekme birlikte)
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

                                // Gün kutusu
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

                                // Alt sekme
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

                    // Saatler
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

                    Spacer(modifier = Modifier.height(16.dp))

                    // Top'ları saran kutu
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = ThinBorderColor,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            for (row in 0 until 4) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
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
                                                // artık reserved da seçilebilsin
                                                activeTop = if (activeTop == topIndex) null else topIndex
                                            }
                                        }
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                            }
                        }
                    }

                    // Legend (kutulara yakın)
                    Spacer(modifier = Modifier.height(4.dp))
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
                            Spacer(modifier = Modifier.width(4.dp))
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
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Reserved", color = Color.Black, fontSize = 14.sp)
                        }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(color = ActiveColor, shape = CircleShape)
                                    .border(width = 1.dp, color = ThinBorderColor, shape = CircleShape)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Selected", color = Color.Black, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Butonlar (gradient'ler)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        val leftGradient = Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0.0f to darkPurple,
                                0.6f to midPurple,
                                1.0f to lightPurple
                            )
                        )

                        // "Rezervasyon Oluştur" her zaman aktif (görünürde de aktif)
                        GradientButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            gradient = leftGradient,
                            shape = RoundedCornerShape(16.dp),
                            enabled = true,
                            onClick = {
                                // eğer seçili top yoksa veya seçili zaten reserved ise hiçbir şey yapma
                                val top = activeTop
                                if (top == null) return@GradientButton
                                if (topStates[fixedSelectedDay][selectedHour][top] == "reserved") return@GradientButton

                                // rezervasyon işlemi
                                topStates = topStates.toMutableList().apply {
                                    this[fixedSelectedDay] = this[fixedSelectedDay].toMutableList().apply {
                                        this[selectedHour] = this[selectedHour].toMutableList().apply {
                                            this[top] = "reserved"
                                        }
                                    }
                                }
                                confirmationType = "created"
                                activeTop = null
                            }
                        ) {
                            Text("Rezervasyon Oluştur", color = Color.White)
                        }

                        val rightGradient = Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0.0f to lightPurple,
                                0.4f to midPurple,
                                1.0f to darkPurple
                            )
                        )

                        // "Rezervasyonu Kaldır" artık her zaman basılabilir görünüyor,
                        // ama içte: sistemde hiç "reserved" yoksa hiçbir şey yapmaz.
                        GradientButton(
                            modifier = Modifier
                                .weight(1f)
                                .height(50.dp),
                            gradient = rightGradient,
                            shape = RoundedCornerShape(16.dp),
                            enabled = true, // görsel olarak aktif
                            onClick = {
                                // önce sistemde en az bir rezervasyon var mı kontrol et
                                val hasAnyReservation = topStates.any { day ->
                                    day.any { hourList ->
                                        hourList.any { it == "reserved" }
                                    }
                                }

                                if (!hasAnyReservation) {
                                    // hiç rezervasyon yok: hiçbir şey yapma (hiçbir mesaj gösterme)
                                    return@GradientButton
                                }

                                // rezervasyonlar varsa hepsini kaldır
                                topStates = List(5) {
                                    List(12) { List(20) { "available" } }
                                }
                                confirmationType = "removed"
                                activeTop = null
                            }
                        ) {
                            Text("Rezervasyonu Kaldır", color = Color.White)
                        }
                    }
                }
            } // içerik Box sonu
        } // Scaffold sonu

        // Overlay dialog — Scaffold'ın üstünde (bu yüzden BottomNavigationBar dahil tüm ekran kapanır)
        if (confirmationType != null) {
            // karartma (tüm ekranı kaplar) ve tıklamaları tüketir
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0x80000000))
                    .clickable { /* arka plan tıklamaları tüketilsin */ }
            )

            // Dialog kutusu: genişlik olarak ekranın yatay padding'li genişliğini alır,
            // yükseklik olarak saat kutularının altından top'ların ortasına kadar inecek şekilde yaklaşık bir değer verildi.
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .height(220.dp)
                    .align(Alignment.Center)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(12.dp))
                    .padding(18.dp)
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    // Metin (farklı içerik: created vs removed)
                    if (confirmationType == "created") {
                        Text(
                            buildAnnotatedString {
                                append("Rezervasyonunuz oluşturulmuştur. ")
                                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                    append("Ajandanızdan")
                                }
                                append(" rezervasyon saatinizi kontrol edebilirsiniz.")
                            },
                            color = Color.Black,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .wrapContentHeight(Alignment.CenterVertically)
                        )
                    } else {
                        // removed
                        Text(
                            text = "Rezervasyonunuz kaldırılmıştır.",
                            color = Color.Black,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f)
                                .wrapContentHeight(Alignment.CenterVertically)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val dialogButtonGradient = Brush.horizontalGradient(
                        colorStops = arrayOf(
                            0.0f to darkPurple,
                            0.6f to midPurple,
                            1.0f to lightPurple
                        )
                    )

                    GradientButton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        gradient = dialogButtonGradient,
                        shape = RoundedCornerShape(12.dp),
                        onClick = {
                            // dialogu kapat
                            confirmationType = null
                        }
                    ) {
                        Text("Devam Et", color = Color.White)
                    }
                }
            }
        }
    } // root Box sonu
}

/** Küçük holder */
private data class DayInfo(val number: Int, val short: String, val full: String)

/**
 * HourBox: seçili ise turuncu dolu, yazı beyaz. Değilse beyaz zemin + ince border.
 */
@Composable
fun HourBox(hour: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    val bg = if (isSelected) WorkSpaceOrangePrimary else Color.White
    val textColor = if (isSelected) Color.White else DefaultHourTextColor
    val borderColor = if (isSelected) WorkSpaceOrangePrimary else ThinBorderColor

    Box(
        modifier = modifier
            .border(width = 1.dp, color = borderColor, shape = shape)
            .background(color = bg, shape = shape)
            .clickable { onClick() }
            .padding(vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = hour,
            color = textColor,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}

/**
 * TopSquare: daha basık görünmesi için height < width.
 * - default: yeni rgb #F0EAE1 arkaplan, siyah sayı
 * - selected: turuncu background, beyaz sayı
 * - reserved: beyaz background, kenar gri, sayı gri (ancak şimdi reserved da seçilebilir)
 */
@Composable
fun TopSquare(index: Int, state: String, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)

    // clickableEnabled artık her durum için true; seçim isSelected ile gösterilir
    val (bgColor, borderColor, textColor) = when {
        state == "reserved" && !isSelected -> Triple(Color.White, ReservedBorderColor, Color(0xFF757575))
        isSelected -> Triple(ActiveColor, ThinBorderColor, Color.White)
        else -> Triple(TopDefaultBg, ThinBorderColor, Color.Black)
    }

    Box(
        modifier = Modifier
            .width(48.dp)
            .height(36.dp)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .background(color = bgColor, shape = shape)
            .clickable { onClick() }, // artık reserved da seçilebilir
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = index.toString(),
            color = textColor,
            fontSize = 14.sp
        )
    }
}

/** Basit gradient buton — Box şeklinde, köşeli; içerik olarak Compose alır
 *  enabled parametresi eklendi: enabled==false ise clickable eklenmez (pasif görünüş için opacity düşürüldü)
 */
@Composable
fun GradientButton(
    modifier: Modifier = Modifier,
    gradient: Brush,
    shape: Shape = RoundedCornerShape(12.dp),
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (enabled) {
        Modifier.clickable { onClick() }
    } else {
        Modifier
    }

    val alpha = if (enabled) 1f else 0.5f

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush = gradient, shape = shape)
            .then(clickableModifier)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.padding(vertical = 10.dp), content = content)
    }
}

/** küçük yardımcı sınıf */
private data class Quad<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

fun generateHourlyList(startHour: Int, endHour: Int): List<String> {
    return (startHour until endHour).map { hour ->
        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        String.format("%02d:00 %s", hour12, amPm)
    }
}
