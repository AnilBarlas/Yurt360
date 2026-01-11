package com.example.yurt360

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.yurt360.navigation.RootNavigation
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.gotrue.handleDeeplinks

class MainActivity : ComponentActivity() {

    private var intentState by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intentState = intent

        val supabase = SupabaseClient.client
        supabase.handleDeeplinks(intent = intent)

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RootNavigation(currentIntent = intentState)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        val supabase = SupabaseClient.client
        supabase.handleDeeplinks(intent = intent)
        setIntent(intent)
        intentState = intent
    }
}