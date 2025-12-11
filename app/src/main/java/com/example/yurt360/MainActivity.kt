package com.example.yurt360

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.yurt360.common.components.LoginScreen
import com.example.yurt360.user.mainScreen.UserHomeScreen
import com.example.yurt360.common.model.Admin
import com.example.yurt360.common.model.TopUser
import com.example.yurt360.common.model.User
import androidx.compose.material3.Scaffold
import com.example.yurt360.common.components.CustomBottomNavigationBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    var currentUser by remember { mutableStateOf<TopUser?>(null) }

                    if (currentUser == null) {
                        LoginScreen(
                            onLoginSuccess = { topUser ->
                                currentUser = topUser
                            }
                        )
                    } else {
                        when (val user = currentUser) {
                            is Admin -> {

                            }
                            is User -> {
                                UserHomeScreen(user = user)
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }
}