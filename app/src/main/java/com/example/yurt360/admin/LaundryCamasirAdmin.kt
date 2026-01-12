package com.example.yurt360.admin.laundry

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.shape.RoundedCornerShape
import com.example.yurt360.R
import com.example.yurt360.common.components.UserBottomNavigationBar
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.postgrest.from
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.serialization.json.contentOrNull
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

@Serializable
data class AjandaInsert(
    val ref_id: Int,
    val ref_type: String,
    val date: String,
    val time: String,
    val user_id: String
)

data class DisplayAjanda(
    val ref_id: Int,
    val ref_type: String,
    val date: String,
    val displayName: String
)

private const val DB_TABLE_AJANDA = "ajanda"
private const val DB_TABLE_USERS = "users"

@Composable
fun LaundryCamasirAdmin(onNavigateHome: () -> Unit = {}) {

    val purple = Color(0xFF8A92E1)
    val lightPurple = Color(0xFFEFEFFE)
    val cornerRadius = 12.dp
    val ThinBorderColor = Color.LightGray.copy(alpha = 0.4f)

    // Supabase client
    val client = SupabaseClient.client

    // UI state
    val ajandaDisplayList = remember { mutableStateListOf<DisplayAjanda>() }
    var isLoading by remember { mutableStateOf(false) }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    val allowedRefTypes = setOf(
        "laundryA_kuzey1_machines",
        "laundryA_kuzey2_machines",
        "laundryA_meydan_machines",
        "laundryA_altguney_machines",
        "laundryA_erkekyurdu_machines"
    )

    fun formatDateToDDMMYYYY(dateStr: String): String {
        return try {
            val parsed = LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE)
            parsed.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"))
        } catch (ex: DateTimeParseException) {
            dateStr
        }
    }

    suspend fun loadUsersMapById(): Map<String, String> {
        return try {
            val jsonList: List<JsonObject> = client
                .from(DB_TABLE_USERS)
                .select()
                .decodeList<JsonObject>() ?: emptyList()

            jsonList.mapNotNull { jo ->
                val rawId = jo["id"]?.let { je ->
                    try {
                        (je as JsonPrimitive).content
                    } catch (_: Exception) {
                        // fallback
                        je.toString().trim('"')
                    }
                }?.trim()

                if (rawId.isNullOrBlank()) return@mapNotNull null

                // name / surname olası anahtarlar
                val name = jo["name"]?.jsonPrimitive?.contentOrNull
                    ?: jo["first_name"]?.jsonPrimitive?.contentOrNull
                    ?: jo["given_name"]?.jsonPrimitive?.contentOrNull
                val surname = jo["surname"]?.jsonPrimitive?.contentOrNull
                    ?: jo["last_name"]?.jsonPrimitive?.contentOrNull

                val fullName = listOfNotNull(name?.trim(), surname?.trim()).joinToString(" ").trim()
                rawId to if (fullName.isNotEmpty()) fullName else rawId
            }.toMap()
        } catch (e: Exception) {
            Log.e("AjandaDebug", "Failed to load users as JsonObject: ${e.localizedMessage}", e)
            emptyMap()
        }
    }

    suspend fun loadAjandaWithUsers() {
        try {
            isLoading = true
            errorMsg = null

            val ajandaResp = try {
                client
                    .from(DB_TABLE_AJANDA)
                    .select()
                    .decodeList<AjandaInsert>() ?: emptyList()
            } catch (e: Exception) {
                Log.e("AjandaFetch", "ajanda fetch failed: ${e.localizedMessage}", e)
                throw e
            }

            val filteredAjanda = ajandaResp.filter { it.ref_type in allowedRefTypes }

            if (filteredAjanda.isEmpty()) {
                ajandaDisplayList.clear()
                isLoading = false
                return
            }

            val usersMap = loadUsersMapById()
            Log.d("AjandaDebug", "Users map keys (sample): ${usersMap.keys.take(10)}")

            val displayList = filteredAjanda.map { a ->
                val rawId = a.user_id.trim()
                val exact = usersMap[rawId]
                val displayName = when {
                    exact != null -> exact
                    else -> {
                        // case-insensitive search
                        val ci = usersMap.entries.find { it.key.equals(rawId, ignoreCase = true) }?.value
                        ci ?: usersMap.entries.find { it.key.trim().equals(rawId.trim(), ignoreCase = true) }?.value ?: rawId
                    }
                }

                DisplayAjanda(
                    ref_id = a.ref_id,
                    ref_type = a.ref_type,
                    date = a.date,
                    displayName = displayName
                )
            }

            ajandaDisplayList.clear()
            ajandaDisplayList.addAll(displayList)

            Log.d("AjandaLoad", "Ajanda loaded: ajanda=${ajandaResp.size}, filtered=${filteredAjanda.size}, display=${displayList.size}")

        } catch (e: Exception) {
            Log.e("AjandaLoad", "Error loading ajanda+users: ${e.localizedMessage}", e)
            errorMsg = e.localizedMessage ?: "Bilinmeyen hata"
        } finally {
            isLoading = false
        }
    }

    fun startAjandaPolling(scope: CoroutineScope, intervalMs: Long = 60_000L) {
        scope.launch {
            loadAjandaWithUsers()
            while (isActive) {
                delay(intervalMs)
                loadAjandaWithUsers()
            }
        }
    }

    LaunchedEffect(Unit) {
        startAjandaPolling(scope, intervalMs = 60_000L)
    }

    // UI
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(modifier = Modifier.matchParentSize(), bottomBar = {
            UserBottomNavigationBar(onNavigate = { /*...*/ })
        }) { paddingVals ->

            val extraBottomPadding = 88.dp

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingVals)
                    .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = extraBottomPadding)
            ) {
                Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

                    // --- Top-left back arrow ---
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                        verticalAlignment = Alignment.Top,
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.arrow_),
                            contentDescription = "Geri",
                            modifier = Modifier.size(36.dp).padding(4.dp)
                                .clickable { onNavigateHome() },
                            contentScale = ContentScale.Fit
                        )
                    }

                    // --- Başlık ---
                    Text(
                        text = "Rezervasyonlar",
                        fontSize = 30.sp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textAlign = TextAlign.Center,
                        color = Color.Black
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(36))
                            .background(purple),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Güncel Rezervasyonlar",
                            color = Color.White,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .clip(RoundedCornerShape(36))
                            .background(Color.White)
                            .border(width = 1.dp, color = ThinBorderColor, shape = RoundedCornerShape(cornerRadius)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Çamaşır Makinesi Rezervasyonları",
                            color = Color.Black,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .weight(2f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(cornerRadius))
                                .background(lightPurple)
                                .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(cornerRadius)),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = "Başvuran Öğrenci",
                                color = Color.Black,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(start = 12.dp)
                            )
                        }

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .clip(RoundedCornerShape(cornerRadius))
                                .background(lightPurple)
                                .border(width = 1.dp, color = Color.White, shape = RoundedCornerShape(cornerRadius)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Tarih",
                                color = Color.Black,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Yüklenme / hata göstergeleri
                    if (isLoading) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
                            CircularProgressIndicator()
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    if (errorMsg != null) {
                        Text(text = "Hata: $errorMsg", color = Color.Red)
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    // --- Ajanda listesi (LazyColumn) ---
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth(),
                        content = {
                            items(ajandaDisplayList) { row ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    // Sol kutu - Ad Soyad
                                    Box(
                                        modifier = Modifier
                                            .weight(2f)
                                            .height(56.dp)
                                            .clip(RoundedCornerShape(cornerRadius))
                                            .background(Color.White)
                                            .border(width = 1.dp, color = ThinBorderColor, shape = RoundedCornerShape(cornerRadius)),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = row.displayName,
                                            color = Color.Black,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(start = 12.dp)
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .height(56.dp)
                                            .clip(RoundedCornerShape(cornerRadius))
                                            .background(Color.White)
                                            .border(width = 1.dp, color = ThinBorderColor, shape = RoundedCornerShape(cornerRadius)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        val formatted = formatDateToDDMMYYYY(row.date)
                                        Text(
                                            text = formatted,
                                            color = Color.Black,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                            }
                        },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(bottom = 96.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Manuel yenile
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Text(
                            text = "Yenile",
                            modifier = Modifier
                                .clickable {
                                    scope.launch { loadAjandaWithUsers() }
                                }
                                .padding(8.dp),
                            color = purple
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
