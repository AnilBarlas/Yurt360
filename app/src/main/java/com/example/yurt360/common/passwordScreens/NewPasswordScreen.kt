package com.example.yurt360.common.passwordScreens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.R
import kotlinx.coroutines.delay
import com.example.yurt360.common.utils.OrangePrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPasswordScreen(
    viewModel: NewPasswordViewModel = viewModel(),
    onConfirmClick: (String) -> Unit
) {

    val password = viewModel.newPassword
    val notificationMessage = viewModel.notificationMessage

    LaunchedEffect(notificationMessage) {
        if (notificationMessage != null) {
            delay(2000)
            viewModel.clearNotification()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Image(
            painter = painterResource(id = R.drawable.loginscreen),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
                .align(Alignment.BottomCenter),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(top = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Image(
                painter = painterResource(id = R.drawable.mainlogo),
                contentDescription = "Logo",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(380.dp),
                contentScale = ContentScale.FillHeight
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .offset(y = (-100).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "YENİ ŞİFRE BELİRLE",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color.Gray,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .height(55.dp)
                        .shadow(4.dp, RoundedCornerShape(30.dp)),
                    shape = RoundedCornerShape(30.dp),
                    color = Color.White
                ) {
                    TextField(
                        value = password,
                        onValueChange = { viewModel.onPasswordChange(it) },
                        placeholder = {
                            Text("Yeni Şifreniz", color = Color.Gray, fontSize = 14.sp)
                        },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            cursorColor = OrangePrimary
                        ),
                        modifier = Modifier.fillMaxSize(),
                        singleLine = true,
                        textStyle = androidx.compose.ui.text.TextStyle(
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                    )
                }

                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    onClick = {
                        viewModel.submitPassword(onSuccess = onConfirmClick)
                    },
                    modifier = Modifier
                        .width(200.dp)
                        .height(50.dp)
                        .shadow(8.dp, RoundedCornerShape(20.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = OrangePrimary),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text(
                        text = "Şifreyi Güncelle",
                        fontSize = 18.sp,
                        color = Color.White
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = notificationMessage != null,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 80.dp)
        ) {
            notificationMessage?.let { msg ->
                Surface(
                    modifier = Modifier
                        .padding(horizontal = 40.dp)
                        .shadow(6.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    tonalElevation = 4.dp
                ) {
                    Text(
                        text = msg,
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 24.dp),
                        color = Color.Black,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}