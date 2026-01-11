package com.example.yurt360.user.workSpace

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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.yurt360.common.components.UserBottomNavigationBar
import com.example.yurt360.data.api.SupabaseClient
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import io.ktor.client.network.sockets.ConnectTimeoutException
import kotlinx.coroutines.isActive
import com.example.yurt360.R
import com.example.yurt360.common.model.User
import kotlinx.serialization.Serializable

// Renkler
private val SelectedDayColor = Color(0xFF7E87E2) // RGB(126,135,226)
private val NonSelectedDayColor = Color(0xFFB0B7FF) // RGB(176,183,255)
private val ThinBorderColor = Color(0x33504B4B).copy(alpha = 0.1f) // yarı saydam siyah (ince stroke)
private val DayTextColor = Color.White

// TopSquare default arkaplan: RGB(240,234,225) -> #F0EAE1
private val TopDefaultBg = Color(0xFFF0EAE1)

// Diğer renkler
private val WorkSpaceOrangePrimary = Color(0xFF7E87E2)
private val ActiveColor = WorkSpaceOrangePrimary
private val ReservedBorderColor = Color(0xFFBDBDBD)
private val DefaultHourTextColor = Color.Black

@Serializable
data class ReservationRow5(
    val table_id: Int,
    val status: String,
    val user_id: String? = null
)

@Serializable
data class AjandaInsert5(
    val ref_id: Int,
    val ref_type: String,
    val date: String,
    val time: String,
    val user_id: String
)

@Composable
fun WorkSpace5_Erkek( onNavigateHome: () -> Unit = {}, onNavigation: (String) -> Unit, user: User ) {

    val currentUserID by rememberUpdatedState(user.id)

    val client = SupabaseClient.client
    val scope = rememberCoroutineScope()

    var selectedDay by remember { mutableStateOf(2) }

    var selectedHour by remember { mutableStateOf(0) }
    var activeTop by remember { mutableStateOf<Int?>(null) }

    var topStates = remember {
        mutableStateListOf(
            *(List(5) {
                mutableStateListOf(
                    *(List(12) {
                        mutableStateListOf(*(List(20) { "available" }.toTypedArray()))
                    }.toTypedArray())
                )
            }.toTypedArray())
        )
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
            DayInfo5(number = date.dayOfMonth, short = shortEn, full = fullEn)
        }
    }

    // mor tonları
    val lightPurple = Color(0xFFB6BCFE) // RGB(182,188,254)
    val darkPurple = Color(0xFF929AE9)  // RGB(146,154,233)
    val midPurple = Color(0xFFA4ABF3)   // ara ton

    // Root container: Box ile Scaffold'ı sarıyoruz, böylece overlay (dialog) Scaffold'ın üstünde (BottomBar dahil) gözükecek.
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.matchParentSize(),
            bottomBar = {
                UserBottomNavigationBar(
                    onNavigate = onNavigation
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
                    // --- Top-left back arrow ---
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 15.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow_),
                            contentDescription = "Geri",
                            modifier = Modifier
                                .size(36.dp)
                                .padding(4.dp)
                                .clickable { onNavigateHome() },
                            contentScale = ContentScale.Fit
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

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
                            val weight = if (index == selectedDay) centerWeight else baseWeight
                            val scaleFactor = weight / baseWeight

                            // Eğer ortadaki sütunsa tüm column'u yukarı kaydır (kutu+sekme birlikte)
                            val columnOffset = if (index == selectedDay) (-centerOffsetUp) else 0.dp

                            val isSelectable = index in 2..4

                            Column(
                                modifier = Modifier
                                    .weight(weight)
                                    .padding(horizontal = 6.dp)
                                    .offset(y = columnOffset)
                                    .clickable(enabled = isSelectable) {
                                        if (isSelectable) {
                                            selectedDay = index
                                            activeTop = null
                                        }
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                val corner = 14.dp
                                val dayShape = RoundedCornerShape(corner)

                                // --- Gün kutusu (buraya .background(...) eklenecek) ---
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .aspectRatio(1f)
                                        .clip(dayShape)
                                        .background(
                                            color = when {
                                                index == selectedDay -> SelectedDayColor
                                                index in 2..4 -> NonSelectedDayColor.copy(alpha = 0.85f) // seçilebilir ama seçili değil
                                                else -> TopDefaultBg.copy(alpha = 0.6f) // seçilemez günler
                                            },
                                            shape = dayShape
                                        )
                                        .border(width = 1.dp, color = ThinBorderColor, shape = dayShape)
                                        .zIndex(1f),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val baseFont = if (index == selectedDay) 36f else 28f
                                    Text(
                                        text = info.number.toString(),
                                        color = DayTextColor,
                                        fontSize = (baseFont * scaleFactor).sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        textAlign = TextAlign.Center
                                    )
                                }

                                // --- Alt sekme (mevcut kod burada kalabilir; benzer görsel mantık uygulanabilir) ---
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
                                    val baseTabFont = if (index == selectedDay) 18f else 16f
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

                    Spacer(modifier = Modifier.height(8.dp))

                    // Saatler
                    val hoursList = generateHourlyList5(9, 21)
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
                                    HourBox5(
                                        hour = hoursList[idx],
                                        isSelected = selectedHour == idx,
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(36.dp)
                                    ) {
                                        selectedHour = idx
                                        activeTop = null
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }

                    fun reserveRoom(selectedHour: Int, activeTop: Int, dayIndex: Int) {
                        val roomId = getGlobalTableId5(dayIndex, selectedHour, activeTop) // 1..720

                        scope.launch {
                            try {
                                client.auth.refreshCurrentSession()
                                val response = client.from("workSpace_erkekyurdu_reservations")
                                    .update(mapOf(
                                        "user_id" to currentUserID,
                                        "status" to "reserved"
                                    )) {
                                        select()
                                        filter {
                                            eq("table_id", roomId)
                                            eq("status", "Available")
                                        }
                                    }

                                if (response.data != null && response.data.isNotEmpty()) {
                                    topStates[dayIndex][selectedHour][activeTop] = "reserved"
                                    // --- AJANDA'YA EKLEME (Serializable sınıf kullanılarak; user_id zorunlu) ---
                                    try {
                                        // Oturumdan user id al (zorunlu)
                                        //val currentUser = client.auth.retrieveUserForCurrentSession(updateSession = true)
                                        val userId = currentUserID
                                        if (userId == null) {
                                            Log.e("AjandaInsert5", "Kullanıcı oturumu bulunamadı — ajanda'ya insert yapılmadı.")
                                        } else {
                                            // Tarih: cihaz local bugünü alıp dayIndex'e göre hesapla
                                            val localToday = LocalDate.now()
                                            val reservationDate = localToday.plusDays((dayIndex - 2).toLong())

                                            // date formatı: "yyyy-MM-dd"
                                            val dateFormatter = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.getDefault())
                                            val dateString = reservationDate.format(dateFormatter)

                                            // time: seçilen kutuya göre 24h format "HH:mm:ss"
                                            val hour24 = 9 + selectedHour
                                            val timeString24 = String.format("%02d:00:00", hour24)

                                            // Ajanda nesnesi (Serializable)
                                            val ajandaRow = AjandaInsert5(
                                                ref_id = roomId,
                                                ref_type = "workSpace_erkekyurdu_reservations",
                                                date = dateString,
                                                time = timeString24,
                                                user_id = userId
                                            )

                                            // Supabase insert (bazı wrapperlar tek obje, bazıları liste bekler — her iki varyanta örnek:)
                                            // Tek obje:
                                            val insertResponse = client.from("ajanda").insert(ajandaRow) { select() }

                                            // Eğer wrapper hata verirse şu versiyonu dene:
                                            // val insertResponse = client.from("ajanda").insert(listOf(ajandaRow)) { select() }

                                            Log.d("AjandaInsert5", "Inserted ajanda row: ${insertResponse.data}")
                                        }
                                    } catch (ex: Exception) {
                                        Log.e("AjandaInsert5", "Ajanda insert failed: ${ex.localizedMessage}", ex)
                                    }
                                    // --- /AJANDA'YA EKLEME ---
                                }
                            } catch (e: Exception) {
                                Log.e("Supabase", "Exception reserveRoom: ${e.localizedMessage}")
                            }
                        }
                    }

                    fun updateReservedStatusFromList(list: List<ReservationRow5>) {
                        // önce tüm kutuları available yap
                        topStates.forEachIndexed { dayIndex, hours ->
                            hours.forEachIndexed { hourIndex, tops ->
                                tops.forEachIndexed { topIndex, _ ->
                                    topStates[dayIndex][hourIndex][topIndex] = "available"
                                }
                            }
                        }

                        // DB'den gelen reserved durumlarını uygula
                        list.forEach { row ->
                            val tableId = row.table_id
                            if (tableId <= 0) return@forEach
                            val (dayIndex, hourIndex, topIndex) = parseTableIdToDayHourTop5(tableId)
                            // güvenlik: indeks sınırlarını kontrol et
                            if (dayIndex in 0 until topStates.size &&
                                hourIndex in 0 until topStates[dayIndex].size &&
                                topIndex in 0 until topStates[dayIndex][hourIndex].size) {
                                topStates[dayIndex][hourIndex][topIndex] = "reserved"
                            }
                        }
                    }

                    fun startPolling(scope: CoroutineScope) {
                        scope.launch {
                            var backoff = 2000L
                            val maxBackoff = 60_000L
                            val refreshInterval = 10 * 60 * 1000L // 10 dakika (ms)
                            var lastRefreshTime = 0L

                            while (isActive) {
                                Log.d("Polling", "Polling db for reserved status… (backoff=${backoff}ms)")

                                try {
                                    val now = System.currentTimeMillis()

                                    // Sadece gerekliyse session refresh et (ör. 10dk'da bir)
                                    if (now - lastRefreshTime > refreshInterval) {
                                        try {
                                            client.auth.refreshCurrentSession()
                                            lastRefreshTime = System.currentTimeMillis()
                                            Log.d("Polling", "Session refreshed (periodic).")
                                        } catch (refreshEx: Exception) {
                                            // Refresh başarısızsa logla ama hemen pes etme; DB sorgusunu yine dene
                                            Log.w("Polling", "Periodic refresh failed: ${refreshEx.localizedMessage}", refreshEx)
                                            // buradan devam ederek DB'yi deneyeceğiz; DB çağrısı da 401 dönerse o zaman tekrar refresh denenir
                                        }
                                    }

                                    // DB sorgusunu yap
                                    val response = client
                                        .from("workSpace_erkekyurdu_reservations")
                                        .select {
                                            filter {
                                                eq("status", "reserved")
                                            }
                                        }
                                        .decodeList<ReservationRow5>()

                                    Log.d("PollingQuery", "Reserved rows: $response")
                                    updateReservedStatusFromList(response)

                                    // başarılı -> backoff resetle
                                    backoff = 2000L

                                } catch (e: Exception) {
                                    // Eğer hata auth/token ile ilgili görünüyorsa (401 veya refresh token hatası),
                                    // önce refresh deneyelim, sonra isteği yeniden dene (bir kere).
                                    Log.e("Polling", "Polling error (first catch): ${e.localizedMessage}", e)

                                    // Eğer network connect timeout ise backoff'u artır
                                    if (e is ConnectTimeoutException) {
                                        backoff = (backoff * 2).coerceAtMost(maxBackoff)
                                    } else {
                                        // Bazı client kütüphaneleri auth hatalarını farklı exception'larda sarar
                                        // Burada genel bir yeniden deneme mantığı uyguluyoruz:
                                        try {
                                            Log.d("Polling", "Attempting on-demand session refresh due to error...")
                                            client.auth.refreshCurrentSession()
                                            lastRefreshTime = System.currentTimeMillis()

                                            // Refresh başarılıysa hemen DB çağrısını tekrar dene
                                            try {
                                                val retryResponse = client
                                                    .from("workSpace_erkekyurdu_reservations")
                                                    .select {
                                                        filter { eq("status", "reserved") }
                                                    }
                                                    .decodeList<ReservationRow5>()

                                                Log.d("PollingQuery", "Reserved rows (after refresh): $retryResponse")
                                                updateReservedStatusFromList(retryResponse)

                                                // başarılı -> backoff reset
                                                backoff = 2000L
                                                continue // döngünün başına dön
                                            } catch (innerEx: Exception) {
                                                Log.e("Polling", "Retry after refresh failed: ${innerEx.localizedMessage}", innerEx)
                                                backoff = (backoff * 2).coerceAtMost(maxBackoff)
                                            }

                                        } catch (refreshEx: Exception) {
                                            Log.e("Polling", "On-demand refresh failed: ${refreshEx.localizedMessage}", refreshEx)
                                            backoff = (backoff * 2).coerceAtMost(maxBackoff)
                                        }
                                    }
                                }

                                delay(backoff)
                            }
                        }
                    }

                    LaunchedEffect(Unit) {
                        startPolling(scope)
                    }


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
                            .padding(16.dp, vertical = 40.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            for (row in 0 until 4) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    topStates[selectedDay][selectedHour]
                                        .chunked(5)[row]
                                        .forEachIndexed { colIndex, state ->
                                            val topIndex = row * 5 + colIndex
                                            TopSquare5(
                                                index = topIndex + 1,
                                                state = state,  // "reserved" ya da "available" durumu burada kontrol edilecek
                                                isSelected = (activeTop == topIndex) && state != "reserved"
                                            ) {
                                                if (state != "reserved") {
                                                    activeTop = if (activeTop == topIndex) null else topIndex
                                                }
                                            }
                                        }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }

                    // Legend (kutulara yakın)
                    Spacer(modifier = Modifier.height(6.dp))
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

                        GradientButton5(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            gradient = leftGradient,
                            shape = RoundedCornerShape(24.dp),
                            enabled = activeTop != null,  // Eğer bir kutu seçili değilse buton pasif olmalı
                            onClick = {
                                if (activeTop != null) {  // Eğer bir kutu seçiliyse işlemi başlat
                                    val top = activeTop
                                    if (top != null && topStates[selectedDay][selectedHour!!][top] != "reserved") {
                                        scope.launch {
                                            reserveRoom(selectedHour!!, top, selectedDay)  // Burada veritabanı güncelleniyor
                                        }
                                    }

                                    // Rezervasyonu oluştur ve UI'ı güncelle
                                    confirmationType = "created"
                                    activeTop = null  // Seçili kutu null'a dönüyor
                                }
                            }
                        ) {
                            Text("Rezervasyon Oluştur", color = Color.White, fontSize = 16.sp)
                        }

                        val rightGradient = Brush.horizontalGradient(
                            colorStops = arrayOf(
                                0.0f to lightPurple,
                                0.4f to midPurple,
                                1.0f to darkPurple
                            )
                        )


                        GradientButton5(
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            gradient = rightGradient,
                            shape = RoundedCornerShape(24.dp),
                            enabled = true,
                            onClick = {
                                scope.launch {
                                    try {
                                        // 1) Kullanıcıyı Supabase'den çek
                                        //val user = client.auth.retrieveUserForCurrentSession(updateSession = true)
                                        val userId = currentUserID

                                        if (userId == null) {
                                            Log.e("SupabaseAuth", "Kullanıcı oturumu bulunamadı")
                                            return@launch
                                        }

                                        // 2) DB güncelle: user_id null ve status "Available"
                                        val response = client.from("workSpace_erkekyurdu_reservations")
                                            .update(mapOf("user_id" to null, "status" to "Available")) {
                                                filter { eq("user_id", userId) }
                                            }

                                        Log.d("SupabaseUpdate", "Rezervasyonlar kaldırıldı: ${response.data}")

                                        // 3) Eğer update başarılıysa ya da her durumda ajanda'dan user'a ait satırları sil
                                        try {
                                            val deleteResponse = client.from("ajanda")
                                                .delete {
                                                    filter {
                                                        eq("ref_type", "workSpace_erkekyurdu_reservations")
                                                        eq("user_id", userId)
                                                    }
                                                }

                                            Log.d("AjandaDelete", "Ajanda'dan silme tamamlandı: ${deleteResponse.data}")
                                        } catch (delEx: Exception) {
                                            Log.e("AjandaDelete", "Ajanda silme hatası: ${delEx.localizedMessage}", delEx)
                                        }

                                        // 4) UI state güncelle
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

                                        confirmationType = "removed"
                                        activeTop = null

                                    } catch (e: Exception) {
                                        Log.e("Supabase", "Rezervasyon kaldırma hatası: ${e.localizedMessage}")
                                    }
                                }
                            }
                        ) {
                            Text("Rezervasyonu Kaldır", color = Color.White, fontSize = 16.sp)
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

                    GradientButton5(
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
private data class DayInfo5(val number: Int, val short: String, val full: String)

/**
 * HourBox: seçili ise turuncu dolu, yazı beyaz. Değilse beyaz zemin + ince border.
 */
@Composable
fun HourBox5(hour: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    val bg = if (isSelected) WorkSpaceOrangePrimary else Color.White
    val textColor = if (isSelected) Color.White else DefaultHourTextColor
    val borderColor = if (isSelected) WorkSpaceOrangePrimary else ThinBorderColor

    Box(
        modifier = modifier
            .border(width = 1.dp, color = borderColor, shape = shape)
            .background(color = bg, shape = shape)
            .clickable { onClick() }
            .padding(vertical = 5.dp),
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
fun TopSquare5(index: Int, state: String, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)

    // Duruma göre renkler ve border'ı ayarla
    val (bgColor, borderColor, textColor) = when {
        state == "reserved" -> Triple(Color.White, ReservedBorderColor, Color(0xFF757575))  // Reserved durumu: gri
        isSelected -> Triple(ActiveColor, Color.Transparent, Color.White)  // Seçilen kutu: turuncu, çizgi yok
        else -> Triple(TopDefaultBg, Color.Transparent, Color.Black)  // Diğer durumlarda çizgi yok
    }

    // "reserved" kutu tıklanamaz olmalı
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

/** Basit gradient buton — Box şeklinde, köşeli; içerik olarak Compose alır
 *  enabled parametresi eklendi: enabled==false ise clickable eklenmez (pasif görünüş için opacity düşürüldü)
 */
@Composable
fun GradientButton5(
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

    val alpha = 1f

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
private data class Quad5<A, B, C, D>(val a: A, val b: B, val c: C, val d: D)

fun generateHourlyList5(startHour: Int, endHour: Int): List<String> {
    return (startHour until endHour).map { hour ->
        val amPm = if (hour < 12) "AM" else "PM"
        val hour12 = if (hour % 12 == 0) 12 else hour % 12
        String.format("%02d:00 %s", hour12, amPm)
    }
}

fun getLocalRowRangeForHour5(hourIndex: Int): Pair<Int, Int> {
    val start = hourIndex * TOPS_PER_HOUR + 1
    val end = (hourIndex + 1) * TOPS_PER_HOUR
    return start to end
}

// Her gün blok büyüklüğü: 12 saat * 20 oda = 240
private const val ROWS_PER_DAY = 240
private const val TOPS_PER_HOUR = 20
private const val HOURS_PER_DAY = 12

// dayIndex: 0..4 (bizim kullanımda gerçek rezervasyon için 2,3,4)
fun dayBlockStart5(dayIndex: Int): Int {
    // Orta gün (index 2) -> offset 0
    // index 2 -> 0, index 3 -> 240, index 4 -> 480
    return when (dayIndex) {
        2 -> 0
        3 -> ROWS_PER_DAY
        4 -> ROWS_PER_DAY * 2
        else -> 0 // diğer günler için 0 kullan veya farklı davran
    }
}

// Lokal (gün içi) saat indexinden (0..11) ve topIndex(0..19) => global table_id
fun getGlobalTableId5(dayIndex: Int, hourIndex: Int, topIndex: Int): Int {
    val dayOffset = dayBlockStart5(dayIndex)
    return dayOffset + hourIndex * TOPS_PER_HOUR + topIndex + 1  // table_id 1-based
}

// table_id -> (dayIndex, hourIndex, topIndex)
fun parseTableIdToDayHourTop5(tableId: Int): Triple<Int, Int, Int> {
    // determine which day block
    val zeroBased = tableId - 1
    val dayBlock = zeroBased / ROWS_PER_DAY  // 0,1,2
    val dayIndex = 2 + dayBlock // maps 0->2, 1->3, 2->4

    val withinDay = zeroBased % ROWS_PER_DAY
    val hourIndex = withinDay / TOPS_PER_HOUR
    val topIndex = withinDay % TOPS_PER_HOUR

    return Triple(dayIndex, hourIndex, topIndex)
}


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
fun WorkSpaceRes5(
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

                    RoomBox5(
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

    }
}

@Composable
fun RoomBox5(roomId: Int, state: String, isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    val bgColor = when {
        state == "reserved" && !isSelected -> Color.Gray
        isSelected -> Color.Blue
        else -> Color.LightGray
    }
    val textColor = if (state == "reserved" && !isSelected) Color.White else Color.Black

    Box(
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
    }
}
