package com.example.yurt360.user.mainScreen

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yurt360.R // Kendi R dosyanıza göre güncelleyin
import com.example.yurt360.common.components.CustomBottomNavigationBar
import com.example.yurt360.common.model.User

// Renk Tanımları (Proje renklerinize göre düzenleyebilirsiniz)
val OrangePrimary = Color(0xFFFF8A65) // Örnek Turuncu
val CardBackground = Color.White
val TextColorSecondary = Color.Gray

@Composable
fun ProfileScreen(
    user: User, // User modelini parametre olarak alıyoruz
    onNavigate: (String) -> Unit
) {
    // Hangi kartın açık olduğunu tutan state (null = hepsi kapalı)
    var expandedCard by remember { mutableStateOf<String?>(null) }

    // Ekran boyutunu alarak dinamik boşluklar yaratabiliriz
    val screenHeight = LocalConfiguration.current.screenHeightDp.dp
    val scrollState = rememberScrollState()

    Scaffold(
        bottomBar = {
            // Senin attığın BottomNavigationBar bileşeni
            CustomBottomNavigationBar(
                onNavigate = onNavigate,
               // modifier = Modifier.zIndex(10f) // En üstte durması için
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 1. KATMAN: Arka Plan Resmi
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background), // Buraya kendi yurt resmini (drawable) koy
                contentDescription = "Background",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Hamburger Menü İkonu (Sol Üst)
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = "Menu",
                tint = Color.White,
                modifier = Modifier
                    .padding(16.dp)
                    .size(32.dp)
                    .align(Alignment.TopStart)
            )

            // 2. KATMAN: Kaydırılabilir İçerik
            // İçeriği ortalamak ve aşağıdan yukarı açılma hissi için
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(bottom = 20.dp), // BottomBar'ın hemen üstünde bitmesi için boşluk
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom // İçeriği aşağıya yasla
            ) {
                // Üstteki boşluk (Resmin görünmesi için esnek alan)
                // Kartlar açıldığında bu alan daralacak ve içerik yukarı kayacak
                Spacer(modifier = Modifier.weight(1f))

                // Profil Resmi ve Parola Butonu Alanı
                ProfileHeaderSection(user.image_url)

                Spacer(modifier = Modifier.height(20.dp))

                // İletişim Bilgileri Kartı
                ExpandableCard(
                    title = "İletişim Bilgileri",
                    isExpanded = expandedCard == "contact",
                    onHeaderClick = {
                        // Eğer zaten açıksa kapat, değilse aç ve diğerini kapat
                        expandedCard = if (expandedCard == "contact") null else "contact"
                    }
                ) {
                    ContactInfoContent(user)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Kişisel Bilgiler Kartı
                ExpandableCard(
                    title = "Kişisel Bilgiler",
                    isExpanded = expandedCard == "personal",
                    onHeaderClick = {
                        expandedCard = if (expandedCard == "personal") null else "personal"
                    }
                ) {
                    PersonalInfoContent(user)
                }

                // BottomBar'ın arkasında kalmaması için ekstra boşluk
                Spacer(modifier = Modifier.height(100.dp))
            }
        }
    }
}

@Composable
fun ProfileHeaderSection(imageUrl: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profil Resmi
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, OrangePrimary, CircleShape)
        ) {
            // Gerçek resim yükleme kütüphanesi (Coil/Glide) kullanıyorsan burayı güncelle
            // Örnek: AsyncImage(model = imageUrl ...)
            Text(text = "fotoğrafı", color = Color.Black)
        }

        Spacer(modifier = Modifier.height((-15).dp)) // Butonu resmin içine biraz gömmek için

        // Parola Güncelle Butonu
        Button(
            onClick = { /* Parola Güncelle */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(20.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp),
            modifier = Modifier.height(35.dp)
        ) {
            Text(
                text = "Parola Güncelle",
                color = OrangePrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun ExpandableCard(
    title: String,
    isExpanded: Boolean,
    onHeaderClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ) // Büyüme/Küçülme animasyonu
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            // Header (Her zaman görünür)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp) // Kapalıyken görünecek yükseklik
                    .clickable { onHeaderClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            // İçerik (Sadece expanded ise görünür)
            if (isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 10.dp)
                ) {
                    content()
                    Spacer(modifier = Modifier.height(20.dp)) // Alttan biraz boşluk
                }
            }
        }
    }
}

// İletişim Bilgileri İçeriği
@Composable
fun ContactInfoContent(user: User) {
    InfoRow(label = "Telefon", value = user.phone)
    Spacer(modifier = Modifier.height(10.dp))
    InfoRow(label = "E-posta", value = user.email)
}

// Kişisel Bilgiler İçeriği
@Composable
fun PersonalInfoContent(user: User) {
    InfoRow(label = "Kimlik No", value = user.tc)
    InfoRow(label = "Ad", value = user.name)
    InfoRow(label = "Soyad", value = user.surname)
    InfoRow(label = "Cinsiyet", value = user.gender)
    InfoRow(label = "Kan Grubu", value = user.bloodType)
    InfoRow(label = "Uyruk", value = "T.C.") // Modelde yoksa statik veya ekle
    InfoRow(label = "Doğum Tarihi", value = user.birthDate)
    InfoRow(label = "Ülke", value = user.location) // Location ülke olarak varsayıldı
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            modifier = Modifier.width(100.dp),
            color = Color.Gray,
            fontSize = 14.sp
        )

        // Input benzeri görünüm için Box ve Text
        Box(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFFF5F5F5), shape = RoundedCornerShape(10.dp))
                .padding(vertical = 12.dp, horizontal = 16.dp)
        ) {
            Text(
                text = value,
                color = Color.Black,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}