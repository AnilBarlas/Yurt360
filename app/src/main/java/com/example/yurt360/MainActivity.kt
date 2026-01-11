package com.example.yurt360

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import com.example.yurt360.navigation.RootNavigation
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.gotrue.handleDeeplinks
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {

    private var intentState by mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Yurt360)

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
                    AppContent(intentState)
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

@Composable
fun AppContent(currentIntent: Intent?) {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(2000)
        showSplash = false
    }

    if (showSplash) {
        SplashScreen()
    } else {
        RootNavigation(currentIntent = currentIntent)
    }
}
@Composable
fun SplashScreen() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.loadingscreen),
            contentDescription = "Loading Screen",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}