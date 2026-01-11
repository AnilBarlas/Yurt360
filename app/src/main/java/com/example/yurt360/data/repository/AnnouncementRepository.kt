package com.example.yurt360.data.repository

import com.example.yurt360.common.model.Announcement
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import io.github.jan.supabase.realtime.realtime
import kotlinx.coroutines.flow.Flow

class AnnouncementRepository {

    private val supabase = SupabaseClient.client
    private val channel = supabase.realtime.channel("announcements")

    fun getAnnouncementStream(): Flow<PostgresAction.Insert> {
        return channel.postgresChangeFlow(schema = "public") {
            table = "announcement"
        }
    }

    suspend fun subscribeToRealtime() {
        channel.subscribe()
    }

    suspend fun addAnnouncement(announcement: Announcement) {
        supabase.from("announcement").insert(announcement)
    }

    suspend fun getLatestAnnouncements(limit: Long = 3): List<Announcement> {
        return supabase.from("announcement").select {
            order(column = "created_at", order = Order.DESCENDING)
            limit(limit)
        }.decodeList<Announcement>()
    }
}