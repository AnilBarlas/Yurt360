package com.example.yurt360

import org.junit.Test
import org.junit.Assert.*

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.common.components.CustomBottomNavigationBar
import com.example.yurt360.common.components.LoginScreen
import com.example.yurt360.common.components.LoginViewModel
import com.example.yurt360.common.components.NewPasswordScreen
import com.example.yurt360.user.mainScreen.UserHomeScreen
import com.example.yurt360.common.model.Admin
import com.example.yurt360.common.model.TopUser
import com.example.yurt360.common.model.User
import com.example.yurt360.data.api.SupabaseClient
import io.github.jan.supabase.gotrue.handleDeeplinks
import androidx.compose.foundation.layout.padding
import com.example.yurt360.user.mainScreen.ProfileScreen
/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }
}