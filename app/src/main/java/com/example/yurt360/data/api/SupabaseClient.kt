package com.example.yurt360.data.api

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.realtime.Realtime

object SupabaseClient {
    private const val SUPABASE_URL = "https://pmqvmmbqfvjkgtxpbmwo.supabase.co"
    private const val SUPABASE_KEY = "sb_publishable_nWqV7reaw5OJ3YVCDtzQ7g_uNvw_e-C"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_KEY
    ) {
        install(Auth)
        install(Postgrest)
        install(Realtime)
    }
}