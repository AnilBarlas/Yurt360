package com.example.yurt360.user.mainScreen

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.zIndex
import com.example.yurt360.R
import com.example.yurt360.common.components.CustomBottomNavigationBar

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun Laundry(onNavigateBottom: (String) -> Unit = {}) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp

    val imageHeight = screenHeight / 2
    val extraUp = screenHeight * 0.10f
    val sheetHeight = imageHeight + extraUp + 30.dp

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            CustomBottomNavigationBar(onNavigate = onNavigateBottom)
        }
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Üstteki resim (drawable/laundry_bina.png)
            Image(
                painter = painterResource(id = R.drawable.laundry_bina),
                contentDescription = "Laundry building",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(imageHeight)
                    .align(Alignment.TopCenter)
            )

            // Alt yarıyı kaplayan beyaz sekme (Card ile köşeleri daha geniş yuvarlak)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(sheetHeight)
                    .align(Alignment.BottomCenter)
                    // sheet'in bottom bar'ın altında kalması için zIndex düşük bırakıyoruz; Scaffold'ın
                    // bottomBar'ı otomatik olarak üstte render edilir.
                    .zIndex(0f),
                shape = RoundedCornerShape(topStart = 80.dp, topEnd = 80.dp), // Daha geniş yuvarlak köşeler
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {

                    // "Kurutma Makinesi" butonu
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .padding(horizontal = 80.dp)
                            .offset(y = (-80).dp)
                    ) {
                        // PNG'yi arka plana eklemek için
                        val image = painterResource(id = R.drawable.rectangle_2117)
                        Image(
                            painter = image,
                            contentDescription = "Arka Plan Görseli",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop // Resmin tam boyuta uymasını sağlar
                        )

                        // Buton kısmı
                        Button(
                            onClick = { /* OnClick action */ },
                            modifier = Modifier
                                .fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // Arka planı şeffaf yapar
                            shape = RoundedCornerShape(12.dp) // Yuvarlak köşeler
                        ) {
                            Text(text = "Kurutma Makinesi", color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // "Çamaşır Makinesi" butonu
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(36.dp)
                            .padding(horizontal = 80.dp)
                            .offset(y = (-80).dp)
                    ) {
                        // PNG'yi arka plana eklemek için
                        val image = painterResource(id = R.drawable.rectangle_2117)
                        Image(
                            painter = image,
                            contentDescription = "Arka Plan Görseli",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop // Resmin tam boyuta uymasını sağlar
                        )

                        // Buton kısmı
                        Button(
                            onClick = { /* OnClick action */ },
                            modifier = Modifier
                                .fillMaxSize(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent), // Arka planı şeffaf yapar
                            shape = RoundedCornerShape(12.dp) // Yuvarlak köşeler
                        ) {
                            Text(text = "Çamaşır Makinesi", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}
