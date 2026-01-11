package com.example.yurt360.common.components

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.common.model.Announcement
import com.example.yurt360.data.repository.AnnouncementRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

// UI'ın durumunu takip etmek için bir veri sınıfı
data class AnnouncementState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val items: List<Announcement> = emptyList()
)

class AnnouncementViewModel : ViewModel() {

    private val repository = AnnouncementRepository()

    // UI tarafında gözlemlenecek ana durum
    private val _state = mutableStateOf(AnnouncementState())
    val state: State<AnnouncementState> = _state

    // Eski listeyi de geriye dönük uyumluluk için koruyoruz
    val announcements = mutableStateListOf<Announcement>()

    private var isFirstLoad = true
    private var isObserving = false

    fun observeAnnouncements(context: Context) {
        if (isObserving) return
        isObserving = true

        // Gerçek zamanlı akışı dinle
        repository.getAnnouncementStream()
            .onEach {
                fetchLatest(context, isSilent = true)
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, error = null)
                repository.subscribeToRealtime()
                fetchLatest(context)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = "Bağlantı hatası: ${e.message}"
                )
                e.printStackTrace()
            }
        }
    }

    fun addAnnouncement(title: String, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val newAnnouncement = Announcement(title = title, description = description)
                repository.addAnnouncement(newAnnouncement)
                onSuccess()
            } catch (e: Exception) {
                // Hata durumunda kullanıcıya bilgi verilebilir
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchLatest(context: Context, isSilent: Boolean = false) {
        if (!isSilent) {
            _state.value = _state.value.copy(isLoading = true)
        }

        try {
            val result = repository.getLatestAnnouncements()

            // Bildirim kontrolü
            if (result.isNotEmpty() && !isFirstLoad) {
                if (announcements.isEmpty() || result[0].id != announcements[0].id) {
                    sendNotification(context, result[0].title, result[0])
                }
            }

            // Listeleri güncelle
            announcements.clear()
            announcements.addAll(result)

            // State'i güncelle
            _state.value = _state.value.copy(
                isLoading = false,
                items = result,
                error = if (result.isEmpty()) "Henüz duyuru bulunmuyor." else null
            )

            isFirstLoad = false
        } catch (e: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                error = "Veriler alınırken bir hata oluştu."
            )
            e.printStackTrace()
        }
    }

    private fun sendNotification(context: Context, title: String, message: Announcement) {
        val channelId = "announcement_notifs"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Duyurular",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message.description)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}