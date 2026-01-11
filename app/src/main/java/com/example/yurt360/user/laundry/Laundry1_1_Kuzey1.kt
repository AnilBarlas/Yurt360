package com.example.yurt360.user.laundry

import android.util.Log
import androidx.compose.foundation.Image
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.yurt360.common.components.CustomBottomNavigationBar
import com.example.yurt360.data.api.SupabaseClient
import com.example.yurt360.R
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.serialization.Serializable

// Renkler (mor tonları, WorkSpace ile uyumlu olacak şekilde)
private val SelectedDayColor = Color(0xFF7E87E2) // mor ana
private val NonSelectedDayColor = Color(0xFFB0B7FF)
private val ThinBorderColor = Color(0x33504B4B).copy(alpha = 0.1f)
private val DayTextColor = Color.White

private val TopDefaultBg = Color(0xFFF0EAE1)

// Mor tonları
private val LightPurple = Color(0xFFB6BCFE)
private val MidPurple = Color(0xFFA4ABF3)
private val DarkPurple = Color(0xFF929AE9)
private val ActiveColor = SelectedDayColor
private val ReservedBorderColor = Color(0xFFBDBDBD)
private val DefaultHourTextColor = Color.Black

// DB tablo adı ve alan adı
private const val DB_TABLE = "laundryA_kuzey1_machines"
private const val DB_ID_COLUMN = "machine_id"

@Serializable
data class ReservationRow(
    val machine_id: Int,
    val status: String,
    val user_id: String? = null
)

@Serializable
data class AjandaInsert(
    val ref_id: Int,
    val ref_type: String,
    val date: String,   // format: "yyyy-MM-dd"
    val time: String,   // format: "HH:mm:ss"
    val user_id: String
)

/**
 * 5-gün görünümlü UI, ancak kullanılabilir/güncellenebilir günler: ortadaki gün (index 2), +1 (3), +2 (4).
 * - Görselde 5 gün gösterilir (soldaki iki gün kapalı/disabled)
 * - DB için 3 gün (today, today+1, today+2) kullanılır — machine_id 1..360
 * - Her gün: 12 saat × 10 makina = 120 satır
 * - Toplam satır sayısı: 3 × 120 = 360
 */
@Composable
fun Laundry1_1_Kuzey1(onNavigateHome: () -> Unit = {}) {
    val client = SupabaseClient.client
    val scope = rememberCoroutineScope()

    // Gün listesi: -2..+2 (5 kutu şeklinde gösterilecek)
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

    // Seçili gün: görselde orta olan (index 2) varsayılan; kullanıcı 2..4 arasında geçiş yapabilir
    var selectedDay by remember { mutableStateOf(2) } // 0..4, yalnızca 2..4 seçilebilir

    // Seçili saat ve top
    var selectedHour by remember { mutableStateOf(0) }
    var activeTop by remember { mutableStateOf<Int?>(null) }

    // topStates: 3 gün × 12 saat × 10 makina
    val topStates = remember {
        mutableStateListOf(
            *(List(3) {
                mutableStateListOf(
                    *(List(12) {
                        mutableStateListOf(*(List(10) { "available" }.toTypedArray()))
                    }.toTypedArray())
                )
            }.toTypedArray())
        )
    }

    var dialogState by remember { mutableStateOf<String?>(null) }

    val purpleGradient = Brush.horizontalGradient(
        colorStops = arrayOf(
            0.0f to DarkPurple,
            0.6f to MidPurple,
            1.0f to LightPurple
        )
    )

    // --- DB Sync helpers ---
    fun updateReservedStatusFromList(list: List<ReservationRow>) {
        // önce hepsini available yap
        topStates.forEachIndexed { dayIndex, hours ->
            hours.forEachIndexed { hourIndex, tops ->
                tops.forEachIndexed { topIndex, _ ->
                    topStates[dayIndex][hourIndex][topIndex] = "available"
                }
            }
        }

        list.forEach { row ->
            val machineId = row.machine_id
            if (machineId <= 0) return@forEach
            val (dayIndex, hourIndex, topIndex) = parseMachineIdToDayHourTop(machineId)
            if (dayIndex in 0 until topStates.size &&
                hourIndex in 0 until topStates[dayIndex].size &&
                topIndex in 0 until topStates[dayIndex][hourIndex].size
            ) {
                topStates[dayIndex][hourIndex][topIndex] = "reserved"
            }
        }
    }

    fun startPolling(scope: CoroutineScope) {
        scope.launch {
            var backoff = 2000L
            val maxBackoff = 60_000L
            val refreshInterval = 10 * 60 * 1000L // 10 dakika
            var lastRefreshTime = 0L

            while (isActive) {
                Log.d("LaundryPolling", "Polling db for reserved status… (backoff=${backoff}ms)")
                try {
                    val now = System.currentTimeMillis()
                    if (now - lastRefreshTime > refreshInterval) {
                        try {
                            client.auth.refreshCurrentSession()
                            lastRefreshTime = System.currentTimeMillis()
                            Log.d("LaundryPolling", "Session refreshed (periodic).")
                        } catch (refreshEx: Exception) {
                            Log.w("LaundryPolling", "Periodic refresh failed: ${refreshEx.localizedMessage}", refreshEx)
                        }
                    }

                    val response = client
                        .from(DB_TABLE)
                        .select {
                            filter { eq("status", "reserved") }
                        }
                        .decodeList<ReservationRow>()

                    Log.d("LaundryPollingQuery", "Reserved rows: $response")
                    updateReservedStatusFromList(response)

                    backoff = 2000L
                } catch (e: Exception) {
                    Log.e("LaundryPolling", "Polling error: ${e.localizedMessage}", e)
                    if (e is ConnectTimeoutException) {
                        backoff = (backoff * 2).coerceAtMost(maxBackoff)
                    } else {
                        try {
                            Log.d("LaundryPolling", "Attempting on-demand session refresh due to error...")
                            client.auth.refreshCurrentSession()
                            lastRefreshTime = System.currentTimeMillis()

                            val retryResponse = client
                                .from(DB_TABLE)
                                .select {
                                    filter { eq("status", "reserved") }
                                }
                                .decodeList<ReservationRow>()

                            Log.d("LaundryPollingQuery", "Reserved rows (after refresh): $retryResponse")
                            updateReservedStatusFromList(retryResponse)

                            backoff = 2000L
                            continue
                        } catch (innerEx: Exception) {
                            Log.e("LaundryPolling", "Retry after refresh failed: ${innerEx.localizedMessage}", innerEx)
                            backoff = (backoff * 2).coerceAtMost(maxBackoff)
                        }
                    }
                }

                delay(backoff)
            }
        }
    }

    LaunchedEffect(Unit) { startPolling(scope) }

    // Rezervasyon yapma (DB'ye yazma)
    fun reserveMachine(dayIndex: Int, hourIndex: Int, topIndex: Int) {
        val machineId = getGlobalTableId(dayIndex, hourIndex, topIndex)
        scope.launch {
            try {
                client.auth.refreshCurrentSession()

                val user = client.auth.retrieveUserForCurrentSession(updateSession = true)
                val userId = user?.id
                if (userId == null) {
                    Log.e("LaundryReserve", "User session not found")
                    return@launch
                }

                val response = client.from(DB_TABLE)
                    .update(mapOf("user_id" to userId, "status" to "reserved")) {
                        select()
                        filter {
                            eq(DB_ID_COLUMN, machineId)
                            eq("status", "Available")
                        }
                    }

                Log.d("LaundryReserve", "Update response: ${response.data}")
                if (response.data != null && response.data.isNotEmpty()) {
                    topStates[dayIndex][hourIndex][topIndex] = "reserved"
                    // --- AJANDA'YA EKLEME ---
                    try {
                        // userId zaten fonksiyon başında alındı
                        val userIdLocal = userId // veya mevcut kullanımdaki değişken adı

                        // tarih: today değişkeni Laundry1_1 içinde tanımlı (LocalDate.now(zone))
                        // burada dayIndex 0..2 -> today, today+1, today+2
                        val reservationDate = today.plusDays(dayIndex.toLong())

                        // ISO format: yyyy-MM-dd (DB date tipine uygun)
                        val dateString = java.time.format.DateTimeFormatter.ISO_LOCAL_DATE.format(reservationDate)
                        // veya explicit: java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault()).format(reservationDate)

                        // time: hourIndex 0..11 -> saat = 9 + hourIndex
                        val hour24 = 9 + hourIndex
                        val timeString24 = String.format("%02d:00:00", hour24) // ör: "17:00:00"

                        // Ajanda nesnesi (Serializable)
                        val ajandaRow = AjandaInsert(
                            ref_id = machineId,
                            ref_type = DB_TABLE,
                            date = dateString,
                            time = timeString24,
                            user_id = userIdLocal
                        )

                        // insert (bazı wrapperlar tek obje, bazıları liste bekleyebilir; ikisini de test et)
                        val insertResponse = try {
                            client.from("ajanda").insert(ajandaRow) { select() }
                        } catch (e: Exception) {
                            // eğer tek obje hata verirse, liste olarak dene
                            client.from("ajanda").insert(listOf(ajandaRow)) { select() }
                        }

                        Log.d("AjandaInsert", "Inserted ajanda row: ${insertResponse.data}")
                    } catch (ex: Exception) {
                        Log.e("AjandaInsert", "Ajanda insert failed: ${ex.localizedMessage}", ex)
                    }
                    // --- /AJANDA'YA EKLEME ---
                }
            } catch (e: Exception) {
                Log.e("LaundryReserve", "Exception reserveMachine: ${e.localizedMessage}", e)
            }
        }
    }

    // Kullanıcının rezervasyonlarını kaldırma
    fun removeUserReservations() {
        scope.launch {
            try {
                val user = client.auth.retrieveUserForCurrentSession(updateSession = true)
                val userId = user?.id
                if (userId == null) {
                    Log.e("LaundryRemove", "User session not found")
                    return@launch
                }

                val response = client.from(DB_TABLE)
                    .update(mapOf("user_id" to null, "status" to "Available")) {
                        filter { eq("user_id", userId) }
                    }

                Log.d("LaundryRemove", "Removed reservations: ${response.data}")

                // --- AJANDA'DAN SİLME ---
                try {
                    val deleteResponse = client.from("ajanda")
                        .delete {
                            filter {
                                eq("ref_type", DB_TABLE)
                                eq("user_id", userId)
                            }
                        }
                    Log.d("AjandaDelete", "Ajanda'dan silme tamamlandı: ${deleteResponse.data}")
                } catch (delEx: Exception) {
                    Log.e("AjandaDelete", "Ajanda silme hatası: ${delEx.localizedMessage}", delEx)
                }
                // --- /AJANDA'DAN SİLME ---

                // UI anlık temizleme
                topStates.forEachIndexed { d, hours ->
                    hours.forEachIndexed { h, tops ->
                        tops.forEachIndexed { t, _ ->
                            topStates[d][h][t] = "available"
                        }
                    }
                }

            } catch (e: Exception) {
                Log.e("LaundryRemove", "Exception removeUserReservations: ${e.localizedMessage}", e)
            }
        }
    }

    // --- UI ---
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(modifier = Modifier.matchParentSize(), bottomBar = {
            CustomBottomNavigationBar(onNavigate = { /*...*/ })
        }) { paddingVals ->
            Box(modifier = Modifier.fillMaxSize().padding(paddingVals).padding(16.dp)) {
                Column(modifier = Modifier.fillMaxSize()) {
                    // --- Top-left back arrow ---
                    Row(modifier = Modifier.fillMaxWidth().padding(bottom = 15.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.Start) {
                        Image(painter = painterResource(id = R.drawable.arrow_), contentDescription = "Geri", modifier = Modifier.size(36.dp).padding(4.dp).clickable { onNavigateHome() }, contentScale = ContentScale.Fit)
                    }

                    // Üst 5 gün (soldaki 2 gün disabled)
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.Top) {
                        val baseWeight = 1f
                        val centerWeight = 1.20f
                        val centerOffsetUp = 14.dp

                        days.forEachIndexed { index, info ->
                            val weight = if (index == selectedDay) centerWeight else baseWeight
                            val scaleFactor = weight / baseWeight
                            val columnOffset = if (index == selectedDay) (-centerOffsetUp) else 0.dp

                            // soldaki iki gün disabled: index 0,1 disabled; sadece 2..4 seçilebilir
                            val isSelectable = index in 2..4

                            Column(modifier = Modifier.weight(weight).padding(horizontal = 6.dp).offset(y = columnOffset)
                                .clickable(enabled = isSelectable) {
                                    if (isSelectable) {
                                        selectedDay = index
                                        activeTop = null
                                    }
                                }, horizontalAlignment = Alignment.CenterHorizontally) {
                                val corner = 14.dp
                                val dayShape = RoundedCornerShape(corner)

                                Box(modifier = Modifier.fillMaxWidth().aspectRatio(1f).clip(dayShape)
                                    .background(
                                        color = when {
                                            index == selectedDay -> SelectedDayColor
                                            index in 2..4 -> NonSelectedDayColor.copy(alpha = 0.85f)
                                            else -> TopDefaultBg.copy(alpha = 0.6f)
                                        }, shape = dayShape
                                    ).border(width = 1.dp, color = ThinBorderColor, shape = dayShape).zIndex(1f), contentAlignment = Alignment.Center) {
                                    val baseFont = if (index == selectedDay) 36f else 28f
                                    Text(text = info.number.toString(), color = DayTextColor, fontSize = (baseFont * scaleFactor).sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
                                }

                                val tabShape = RoundedCornerShape(topStart = 0.dp, topEnd = 0.dp, bottomStart = corner, bottomEnd = corner)
                                val tabOffset = -(corner * 0.85f)

                                Box(modifier = Modifier.fillMaxWidth().aspectRatio(2f).offset(y = tabOffset).clip(tabShape).background(color = Color.White, shape = tabShape).border(width = 1.dp, color = ThinBorderColor, shape = tabShape).zIndex(0f), contentAlignment = Alignment.Center) {
                                    val baseTabFont = if (index == selectedDay) 18f else 16f
                                    Text(text = info.short, color = Color.Black, fontSize = (baseTabFont * scaleFactor).sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 6.dp).offset(y = 6.dp))
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Saatler
                    val hoursList = generateHourlyList(9, 21)
                    Column {
                        for (row in 0..2) {
                            Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                for (col in 0..3) {
                                    val idx = row * 4 + col
                                    HourBox(hour = hoursList[idx], isSelected = selectedHour == idx, modifier = Modifier.weight(1f).height(42.dp)) {
                                        selectedHour = idx
                                        activeTop = null
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Başlık
                    Box(modifier = Modifier.fillMaxWidth(0.94f).height(56.dp).clip(RoundedCornerShape(12.dp)).background(Color.White).border(width = 1.dp, color = ThinBorderColor, shape = RoundedCornerShape(12.dp)).padding(12.dp).align(Alignment.CenterHorizontally), contentAlignment = Alignment.Center) {
                        Text(text = "Çamaşır Makinesi Doluluğu", color = Color.Black, fontSize = 16.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Büyük kutu: 2 satır x 5 sütun -> 10 makina (gün mapping: selectedDay 2..4 maps to topStates index 0..2)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()  // Kutunun genişliği ekranın tamamını alacak şekilde ayarlanır
                            .wrapContentHeight()  // İçeriğe göre yüksekliği ayarlayacak
                            .border(width = 1.dp, color = ThinBorderColor, shape = RoundedCornerShape(12.dp))  // Kutunun kenarlığı
                            .background(color = Color.White, shape = RoundedCornerShape(12.dp))
                            .padding(12.dp), // Kutunun iç kenarındaki padding
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(180.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val dayIndex = (selectedDay - 2).coerceIn(0, 2) // maps 2->0, 3->1, 4->2

                            for (row in 0 until 2) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth() // Row genişliği ekranı kaplasın
                                        .weight(1f),  // Row içindeki kutuları eşit şekilde yayar
                                    horizontalArrangement = Arrangement.SpaceEvenly, // Kutuları eşit şekilde yayar
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    topStates[dayIndex][selectedHour].chunked(5)[row].forEachIndexed { colIndex, state ->
                                        val topIndex = row * 5 + colIndex
                                        TopSquare(
                                            index = topIndex + 1,
                                            state = state,
                                            isSelected = (activeTop == topIndex)
                                        ) {
                                            if (state != "reserved") {
                                                activeTop = if (activeTop == topIndex) null else topIndex
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Legend
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).offset(y = (-4).dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(color = TopDefaultBg, shape = CircleShape).border(width = 1.dp, color = ThinBorderColor, shape = CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Available", color = Color.Black, fontSize = 14.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(color = Color.White, shape = CircleShape).border(width = 1.dp, color = ReservedBorderColor, shape = CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Reserved", color = Color.Black, fontSize = 14.sp)
                        }

                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(end = 8.dp)) {
                            Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(color = ActiveColor, shape = CircleShape).border(width = 1.dp, color = ThinBorderColor, shape = CircleShape))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Selected", color = Color.Black, fontSize = 14.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // İki buton: Rezervasyon Oluştur / Kaldır
                    Row(modifier = Modifier.fillMaxWidth().padding(horizontal = 0.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        SolidButton(modifier = Modifier.weight(1f).height(36.dp), color = ActiveColor, shape = RoundedCornerShape(24.dp), enabled = true, onClick = {
                            val top = activeTop
                            if (top == null) return@SolidButton
                            val dayIndex = (selectedDay - 2).coerceIn(0, 2)
                            if (topStates[dayIndex][selectedHour][top] == "reserved") return@SolidButton
                            dialogState = "confirm_plan"
                        }) { Text("Rezervasyon Oluştur", color = Color.White, fontSize = 16.sp) }

                        SolidButton(modifier = Modifier.weight(1f).height(36.dp), color = ActiveColor, shape = RoundedCornerShape(24.dp), enabled = true, onClick = {
                            val hasAnyReservation = topStates.any { day -> day.any { hourList -> hourList.any { it == "reserved" } } }
                            if (!hasAnyReservation) return@SolidButton
                            removeUserReservations()
                            dialogState = "removed"
                            activeTop = null
                        }) { Text("Rezervasyonu Kaldır", color = Color.White, fontSize = 16.sp) }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }

        // Dialog overlay
        if (dialogState != null) {
            Box(modifier = Modifier.fillMaxSize().background(Color(0x80000000)).clickable { /* tüket */ })

            when (dialogState) {
                "confirm_plan" -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).wrapContentHeight().align(Alignment.Center).clip(RoundedCornerShape(12.dp)).background(Color.White).border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(12.dp)).padding(18.dp)) {
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(text = "Rezervasyonunuz planlanmıştır. Onaylıyor musunuz?", color = Color.Black, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                                ThinButton(modifier = Modifier.weight(1f).height(44.dp), onClick = {
                                    val top = activeTop
                                    if (top == null) { dialogState = null; return@ThinButton }
                                    val dayIndex = (selectedDay - 2).coerceIn(0, 2)
                                    reserveMachine(dayIndex, selectedHour, top)
                                    activeTop = null
                                    dialogState = "created"
                                }) { Text("Evet") }

                                ThinButton(modifier = Modifier.weight(1f).height(44.dp), onClick = { dialogState = null }) { Text("Hayır") }
                            }
                        }
                    }
                }

                "created" -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).wrapContentHeight().align(Alignment.Center).clip(RoundedCornerShape(12.dp)).background(Color.White).border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(12.dp)).padding(18.dp)) {
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(text = "Rezervasyonunuz oluşturulmuştur. Ajandanızdan teslim alma saatinizi kontrol edebilirsiniz.", color = Color.Black, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            ThinButton(modifier = Modifier.fillMaxWidth().height(44.dp), onClick = { dialogState = null }) { Text("Devam Et") }
                        }
                    }
                }

                "removed" -> {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp).wrapContentHeight().align(Alignment.Center).clip(RoundedCornerShape(12.dp)).background(Color.White).border(width = 1.dp, color = Color.Black, shape = RoundedCornerShape(12.dp)).padding(18.dp)) {
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text(text = "Rezervasyonunuz kaldırılmıştır.", color = Color.Black, fontSize = 16.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                            ThinButton(modifier = Modifier.fillMaxWidth().height(44.dp), onClick = { dialogState = null }) { Text("Devam Et") }
                        }
                    }
                }
            }
        }
    }
}

/* Yardımcılar */

private data class DayInfo(val number: Int, val short: String, val full: String)

@Composable
fun HourBox(hour: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    val bg = if (isSelected) ActiveColor else Color.White
    val textColor = if (isSelected) Color.White else DefaultHourTextColor
    val borderColor = if (isSelected) ActiveColor else ThinBorderColor

    Box(modifier = modifier.border(width = 1.dp, color = borderColor, shape = shape).background(color = bg, shape = shape).clickable { onClick() }.padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
        Text(text = hour, color = textColor, fontSize = 14.sp, textAlign = TextAlign.Center)
    }
}

@Composable
fun TopSquare(index: Int, state: String, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)

    // Duruma göre renkler ve border'ı ayarla
    val (bgColor, borderColor, textColor) = when {
        state == "reserved" -> Triple(Color.White, ReservedBorderColor, Color(0xFF757575))  // Reserved durumu: gri kenarlık
        isSelected -> Triple(ActiveColor, Color.Transparent, Color.White)  // Seçilen kutu: turuncu, çizgi yok
        else -> Triple(TopDefaultBg, Color.Transparent, Color.Black)  // Diğer durumlar: çizgi yok
    }

    Box(
        modifier = Modifier
            .width(48.dp)
            .height(36.dp)
            .border(width = 1.dp, color = borderColor, shape = shape)  // Border sadece reserved durumunda olacak
            .background(color = bgColor, shape = shape)
            .clickable(enabled = state != "reserved") {  // Eğer state "reserved" ise, kutu tıklanamaz
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = index.toString(),
            color = textColor,
            fontSize = 14.sp
        )
    }
}

@Composable
fun GradientButton(modifier: Modifier = Modifier, gradient: Brush, shape: Shape = RoundedCornerShape(12.dp), enabled: Boolean = true, onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    val clickableModifier = if (enabled) Modifier.clickable { onClick() } else Modifier
    val alpha = if (enabled) 1f else 0.5f

    Box(modifier = modifier.clip(shape).background(brush = gradient, shape = shape).then(clickableModifier).alpha(alpha), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.padding(vertical = 8.dp), content = content)
    }
}

@Composable
fun SolidButton(modifier: Modifier = Modifier, color: Color, shape: Shape = RoundedCornerShape(12.dp), enabled: Boolean = true, onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    val clickableModifier = if (enabled) Modifier.clickable { onClick() } else Modifier
    val alpha = if (enabled) 1f else 0.5f
    Box(modifier = modifier.clip(shape).background(color = color, shape = shape).then(clickableModifier).alpha(alpha), contentAlignment = Alignment.Center) {
        Box(modifier = Modifier.padding(vertical = 8.dp), content = content)
    }
}

@Composable
fun ThinButton(modifier: Modifier = Modifier, onClick: () -> Unit, content: @Composable BoxScope.() -> Unit) {
    val shape = RoundedCornerShape(10.dp)
    Box(modifier = modifier.clip(shape).background(Color.White, shape = shape).border(width = 1.dp, color = Color.Black, shape = shape).clickable { onClick() }.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
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

// --- Machine id mapping ---
private const val TOPS_PER_HOUR = 10
private const val HOURS_PER_DAY = 12
private const val ROWS_PER_DAY = HOURS_PER_DAY * TOPS_PER_HOUR // 120

fun getGlobalTableId(dayIndex: Int, hourIndex: Int, topIndex: Int): Int {
    // dayIndex: 0..2
    val dayOffset = dayIndex * ROWS_PER_DAY
    return dayOffset + hourIndex * TOPS_PER_HOUR + topIndex + 1 // 1-based (machine_id)
}

fun parseMachineIdToDayHourTop(machineId: Int): Triple<Int, Int, Int> {
    val zeroBased = machineId - 1
    val dayBlock = zeroBased / ROWS_PER_DAY // 0..2
    val dayIndex = dayBlock

    val withinDay = zeroBased % ROWS_PER_DAY
    val hourIndex = withinDay / TOPS_PER_HOUR
    val topIndex = withinDay % TOPS_PER_HOUR

    return Triple(dayIndex, hourIndex, topIndex)
}
