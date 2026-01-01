/*package com.example.yurt360.user.laundry

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
import android.util.Log
import com.example.yurt360.data.api.SupabaseClient
import com.example.yurt360.data.api.SupabaseClient.client
import com.example.yurt360.user.laundry.ReservationRow
import com.example.yurt360.user.laundry.getRowRangeForHour
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

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

@kotlinx.serialization.Serializable
data class ReservationRow(
    val machine_id: Int,
    val status: String,
    val user_id: String? = null
)

@Composable
fun Laundry1() {
    val client = SupabaseClient.client
    val scope = rememberCoroutineScope()

    val fixedSelectedDay = 2 // ortadaki kare sabit seçili

    var selectedHour by remember { mutableStateOf(0) }
    var activeTop by remember { mutableStateOf<Int?>(null) }

    var topStates = remember {
        mutableStateListOf(
            *(List(5) {
                mutableStateListOf(
                    *(List(12) {
                        mutableStateListOf(*(List(10) { "available" }.toTypedArray()))
                    }.toTypedArray())
                )
            }.toTypedArray())
        )
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

                    // Saatler (aynı)
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

                    fun laundryRoom(selectedHour: Int, activeTop: Int) {
                        val (startRow, endRow) = getRowRangeForHour(selectedHour)
                        val machineIndex = startRow + activeTop

                        scope.launch {
                            try {
                                val session = client.auth.refreshCurrentSession()
                                val response = client.from("laundryA_kuzey1_machines")
                                    .update(mapOf(
                                        "user_id" to "efffe411-62fa-4e11-ad42-07aadda51165",
                                        "status" to "reserved"
                                    )) {
                                        select()
                                        filter {
                                            eq("machine_id", machineIndex)
                                            eq("status", "Available")
                                        }
                                    }

                                Log.d("SupabaseUpdate", "response.data=${response.data}")

                                if (response.data != null && response.data.isNotEmpty()) {
                                    // nested state listesini doğrudan güncelle
                                    topStates[fixedSelectedDay][selectedHour][activeTop] = "reserved"

                                    Log.d("ComposeUpdate", "Top ${machineIndex} UI’ya reserved olarak yansıdı.")
                                }

                            } catch (e: Exception) {
                                Log.e("Supabase", "Exception reserveRoom: ${e.localizedMessage}")
                            }
                        }
                    }

                    fun updateLaundryStatusFromList(list: List<ReservationRow>) {
                        // önce tüm kutuları available yap
                        topStates.forEachIndexed { dayIndex, hours ->
                            hours.forEachIndexed { hourIndex, tops ->
                                tops.forEachIndexed { topIndex, _ ->
                                    topStates[dayIndex][hourIndex][topIndex] = "available"
                                }
                            }
                        }

                        // sonra DB’den gelen "reserved" durumlarını uygula
                        list.forEach { row ->
                            val machineId = row.machine_id
                            val hourIndex = (machineId - 1) / 20
                            val topIndex = (machineId - 1) % 20
                            topStates[fixedSelectedDay][hourIndex][topIndex] = "reserved"
                        }
                    }

                    fun startPolling(scope: CoroutineScope) {
                        scope.launch {
                            while (true) {
                                Log.d("Polling", "Polling db for reserved status…")

                                try {
                                    val response = client
                                        .from("laundryA_kuzey1_machines")
                                        .select {
                                            filter {
                                                eq("status", "reserved")
                                            }
                                        }
                                        .decodeList<com.example.yurt360.user.laundry.ReservationRow>()

                                    Log.d("PollingQuery", "Reserved rows: $response")

                                    // UI state güncelle
                                    updateLaundryStatusFromList(response)

                                } catch (e: Exception) {
                                    Log.e("Polling", "Error in polling: ${e.localizedMessage}")
                                }

                                delay(2000L)
                            }
                        }
                    }

                    val scope = rememberCoroutineScope()
                    LaunchedEffect(Unit) {
                        startPolling(scope)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Çamaşır Makinesi Doluluğu: BEYAZ arkaplan, sağ/sol kırpma (daha dar)
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
                            text = "Çamaşır Makinesi Doluluğu",
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

                    // "Kurutmaya Devam Et" -> SABİT turuncu (SolidButton) ve ekran ortasında
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        SolidButton(
                            modifier = Modifier
                                .width(260.dp)
                                .height(48.dp),
                            color = ActiveColor,
                            shape = RoundedCornerShape(14.dp),
                            enabled = true,
                            onClick = { /* Kurutmaya devam et action */ }
                        ) {
                            // kalınlık kaldırıldı; diğer iki butonla aynı
                            Text("Kurutmaya Devam Et", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // İki sabit turuncu buton (yan yana)
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
                                if (activeTop != null) {  // Eğer bir kutu seçiliyse işlemi başlat
                                    val top = activeTop
                                    if (top != null && topStates[fixedSelectedDay][selectedHour!!][top] != "reserved") {
                                        scope.launch {
                                            laundryRoom(
                                                selectedHour!!,
                                                top
                                            )  // Burada veritabanı güncelleniyor
                                        }
                                    }
                                    // açılacak: plan onayı
                                    dialogState = "confirm_plan"
                                    activeTop = null
                                }
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
                                scope.launch {
                                    try {
                                        // 1) Kullanıcıyı Supabase'den çek
                                        val user = client.auth.retrieveUserForCurrentSession(updateSession = true)
                                        val userId = user?.id

                                        if (userId == null) {
                                            Log.e("SupabaseAuth", "Kullanıcı oturumu bulunamadı")
                                            return@launch
                                        }

                                        // 2) DB güncelle: user_id null ve status "Available"
                                        val response = client.from("workSpace_kuzey1_reservations")
                                            .update(mapOf("user_id" to null, "status" to "Available")) {
                                                filter { eq("user_id", userId) }
                                            }

                                        Log.d("SupabaseUpdate", "Rezervasyonlar kaldırıldı: ${response.data}")

                                        // 3) UI state güncelle
                                        topStates.clear()

                                        topStates.addAll(
                                            List(5) {
                                                mutableStateListOf(
                                                    *(List(12) {
                                                        mutableStateListOf(
                                                            *(List(20) { "available" }.toTypedArray())
                                                        )
                                                    }.toTypedArray())
                                                )
                                            }
                                        )

                                        dialogState = "removed"
                                        activeTop = null

                                    } catch (e: Exception) {
                                        Log.e("Supabase", "Rezervasyon kaldırma hatası: ${e.localizedMessage}")
                                    }
                                }
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
}

/** Yardımcılar ve bileşenler (WorkSpace1 ile uyumlu) */

private data class DayInfo(val number: Int, val short: String, val full: String)

@Composable
fun HourBox(hour: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    val bg = if (isSelected) ActiveColor else Color.White
    val textColor = if (isSelected) Color.White else DefaultHourTextColor
    val borderColor = if (isSelected) ActiveColor else ThinBorderColor

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

@Composable
fun TopSquare(index: Int, state: String, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)

    val (bgColor, borderColor, textColor) = when {
        state == "reserved" && !isSelected -> Triple(Color.White, ReservedBorderColor, Color(0xFF757575))
        isSelected -> Triple(ActiveColor, ThinBorderColor, Color.White)
        else -> Triple(TopDefaultBg, ThinBorderColor, Color.Black)
    }

    Box(
        modifier = Modifier
            .width(56.dp)
            .height(42.dp)
            .border(width = 1.dp, color = borderColor, shape = shape)
            .background(color = bgColor, shape = shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = index.toString(),
            color = textColor,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}

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
        Box(modifier = Modifier.padding(vertical = 8.dp), content = content)
    }
}

@Composable
fun SolidButton(
    modifier: Modifier = Modifier,
    color: Color,
    shape: Shape = RoundedCornerShape(12.dp),
    enabled: Boolean = true,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val clickableModifier = if (enabled) Modifier.clickable { onClick() } else Modifier
    val alpha = if (enabled) 1f else 0.5f
    Box(
        modifier = modifier
            .clip(shape)
            .background(color = color, shape = shape)
            .then(clickableModifier)
            .alpha(alpha),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.padding(vertical = 8.dp), content = content)
    }
}

/** İnce (white bg, black border) buton - dialog'daki Evet/Hayır/Devam Et için */
@Composable
fun ThinButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit
) {
    val shape = RoundedCornerShape(10.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(Color.White, shape = shape)
            .border(width = 1.dp, color = Color.Black, shape = shape)
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Box(modifier = Modifier.padding(horizontal = 4.dp), content = content)
    }
}

fun generateHourlyList(startHour: Int, endHour: Int): List<String> {
    return (startHour until endHour).map { hour ->
        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        String.format("%02d:00 %s", hour12, amPm)
    }
}

fun getRowRangeForHour(selectedHour: Int): Pair<Int, Int> {
    return when (selectedHour) {
        0 -> 1 to 10
        1 -> 11 to 20
        2 -> 21 to 30
        3 -> 31 to 40
        4 -> 41 to 50
        5 -> 51 to 60
        6 -> 61 to 70
        7 -> 71 to 80
        8 -> 81 to 90
        9 -> 91 to 100
        10 -> 101 to 110
        11 -> 111 to 120
        else -> 121 to 130
    }
}*/