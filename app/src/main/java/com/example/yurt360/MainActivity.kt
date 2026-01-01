package com.example.yurt360

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.yurt360.common.components.LoginScreen
import com.example.yurt360.common.model.Admin
import com.example.yurt360.common.model.User
import com.example.yurt360.user.workSpace.WorkSpace1
import com.example.yurt360.user.workSpace.WorkSpace2
import com.example.yurt360.user.workSpace.WorkSpace3
//import com.example.yurt360.user.laundry.Laundry1_1
//import com.example.yurt360.user.laundry.Laundry1_2

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    //Laundry1_1()
                    //Laundry1_2()
                    WorkSpace1()
                    //WorkSpace2()
                    //WorkSpace3()
                    /*LoginScreen(
                        onLoginSuccess = { topUser ->

                            when (topUser) {
                                is Admin -> {

                                }
                                is User -> {

                                }
                            }
                        }
                    )*/
                }
            }
        }
    }
}