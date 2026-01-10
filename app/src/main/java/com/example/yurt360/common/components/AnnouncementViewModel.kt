package com.example.yurt360.common.components

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.common.model.Announcement
import com.example.yurt360.data.repository.AnnouncementRepository
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class AnnouncementViewModel : ViewModel() {

    private val repository = AnnouncementRepository()
    val announcements = mutableStateListOf<Announcement>()
    private var isFirstLoad = true

    private var isObserving = false

    fun observeAnnouncements(context: Context) {
        if (isObserving) return

        isObserving = true

        repository.getAnnouncementStream()
            .onEach {
                fetchLatest(context)
            }
            .launchIn(viewModelScope)

        viewModelScope.launch {
            try {
                repository.subscribeToRealtime()
                fetchLatest(context)
            } catch (e: Exception) {
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
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchLatest(context: Context) {
        try {
            val result = repository.getLatestAnnouncements()

            if (result.isNotEmpty() && !isFirstLoad) {
                if (announcements.isEmpty() || result[0].id != announcements[0].id) {
                    sendNotification(context, result[0].title, result[0])
                }
            }

            announcements.clear()
            announcements.addAll(result)
            isFirstLoad = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendNotification(context: Context, title: String, message: Announcement) {
        val channelId = "announcement_notifs"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                message.title,
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

      //  notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }
}