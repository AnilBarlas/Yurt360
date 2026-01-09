package com.example.yurt360.user.mainScreen

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yurt360.common.model.Announcement
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch



class AnnouncementViewModel : ViewModel() {
    val announcements = mutableStateListOf<Announcement>()
    private var isFirstLoad = true


    fun observeAnnouncements(context: Context) {
        val supabase = SupabaseClient.client
        val myChannel = supabase.realtime.channel("announcements")


        myChannel.postgresChangeFlow<PostgresAction.Insert>(schema = "public") {
            table = "announcement"
        }.onEach {
            fetchLatest(context)
        }.launchIn(viewModelScope)

        viewModelScope.launch {
            myChannel.subscribe()
            fetchLatest(context)
        }
    }

    fun addAnnouncement(title: String, description: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val newAnnouncement = Announcement(title = title, description = description)
                SupabaseClient.client.from("announcement").insert(newAnnouncement)
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun fetchLatest(context: Context) {
        try {
            val result = SupabaseClient.client.from("announcement").select {
                order(column = "created_at", order = Order.DESCENDING)
                limit(3)
            }.decodeList<Announcement>()


            if (result.isNotEmpty() && !isFirstLoad) {
                if (announcements.isEmpty() || result[0].id != announcements[0].id) {
                    sendNotification(context, result[0].title, result[0].description)
                }
            }

            announcements.clear()
            announcements.addAll(result)
            isFirstLoad = false
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun sendNotification(context: Context, title: String, message: String) {
        val channelId = "announcement_notifs"
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Yurt Duyuruları", NotificationManager.IMPORTANCE_HIGH)
            notificationManager.createNotificationChannel(channel)
        }
    }
}