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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.yurt360.R
import com.example.yurt360.common.components.OrangePrimary
import com.example.yurt360.common.model.User

@Composable
fun ProfileScreen(
    user: User,
    onPasswordChangeClick: () -> Unit = {}
) {
    var expandedSection by remember { mutableStateOf<ProfileSection?>(null) }

    // Header yüksekliği ve Profil resmi boyut tanımları
    val headerImageHeight = 220.dp
    val profilePicSize = 120.dp
    val profilePicOffset = 60.dp

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF5F5F5))
    ) {
        // --- 0. ARKA PLAN İÇİN BEYAZ BLOK ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.55f)
                .align(Alignment.BottomCenter)
                .background(Color.White)
        )

        // --- 1. SABİT ÜST KISIM (Header + Profil Resmi) ---
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(headerImageHeight + profilePicOffset)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.TopCenter
        ) {
            // Kampüs Resmi
            Image(
                painter = painterResource(id = R.drawable.bina),
                contentDescription = "Kampüs",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(headerImageHeight)
            )

            // Profil Resmi
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(profilePicSize)
                    .align(Alignment.BottomCenter)
                    .background(Color.White, CircleShape)
                    .padding(4.dp)
                    .clip(CircleShape)
            ) {
                if (user.image_url.isNotEmpty()) {
                    Image(
                        painter = painterResource(id = android.R.drawable.sym_def_app_icon),
                        contentDescription = "Profil",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(modifier = Modifier.fillMaxSize().background(Color.LightGray))
                }
            }
        }

        // --- 2. HAREKETLİ İÇERİK (Buton + Kartlar) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .align(Alignment.TopCenter)
                .padding(top = headerImageHeight + profilePicOffset)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // Parola Güncelle Butonu
            Surface(
                onClick = onPasswordChangeClick,
                shape = RoundedCornerShape(20.dp),
                color = Color.White,
                modifier = Modifier.padding(bottom = 0.dp)
            ) {
                Text(
                    text = "Parola Güncelle",
                    color = OrangePrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }

            // KARTLAR DESTESİ
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 0.dp),
                // Kartları daha sıkı üst üste bindirmek için negatif boşluğu artırdık
                verticalArrangement = Arrangement.spacedBy((-40).dp)
            ) {

                // KART 1: İletişim Bilgileri (Altta kalacak olan)
                val isContactExpanded = expandedSection == ProfileSection.CONTACT

                // Z-Index Mantığı: Eğer bu kart açıksa en üste çıkar (10f).
                // Kapalıysa, diğer kartın altında kalması için düşük bir değer alır (1f).
                val contactZIndex = if (isContactExpanded) 10f else 1f

                ExpandableProfileCard(
                    title = "İletişim Bilgileri",
                    isExpanded = isContactExpanded,
                    zIndex = contactZIndex,
                    // Altta kaldığı için daha az yuvarlak ve daha az gölgeli
                    topCornerRadius = 30.dp,
                    shadowElevation = 4.dp,
                    onClick = {
                        expandedSection = if (isContactExpanded) null else ProfileSection.CONTACT
                    }
                ) {
                    ProfileInfoRow("Telefon", user.phone)
                    ProfileInfoRow("E-posta", user.email)
                    // İçerik açıldığında alttaki karta yer açmak için boşluk
                    Spacer(modifier = Modifier.height(40.dp))
                }

                // KART 2: Kişisel Bilgiler (Üstte duracak olan)
                val isPersonalExpanded = expandedSection == ProfileSection.PERSONAL

                // Z-Index Mantığı: Eğer bu kart açıksa en üste çıkar (10f).
                // Kapalıysa bile, İletişim kartının (1f) üzerinde durması için daha yüksek bir temel değer alır (5f).
                val personalZIndex = if (isPersonalExpanded) 10f else 5f

                ExpandableProfileCard(
                    title = "Kişisel Bilgiler",
                    isExpanded = isPersonalExpanded,
                    zIndex = personalZIndex,
                    // İSTEĞİNİZ ÜZERİNE: Daha yuvarlak üst köşeler ve daha belirgin gölge
                    topCornerRadius = 60.dp,
                    shadowElevation = 12.dp,
                    onClick = {
                        expandedSection = if (isPersonalExpanded) null else ProfileSection.PERSONAL
                    }
                ) {
                    ProfileInfoRow("Kimlik No", user.tc)
                    ProfileInfoRow("Ad", user.name)
                    ProfileInfoRow("Soyad", user.surname)
                    ProfileInfoRow("Cinsiyet", user.gender)
                    ProfileInfoRow("Kan Grubu", user.bloodType)
                    ProfileInfoRow("Doğum Tarihi", user.birthDate)
                    ProfileInfoRow("Ülke", user.address)
                    Spacer(modifier = Modifier.height(60.dp))
                }
            }

            if(expandedSection == null) {
                Spacer(modifier = Modifier.height(200.dp))
            }
        }
    }
}

enum class ProfileSection {
    CONTACT, PERSONAL
}

// GÜNCELLENMİŞ KART BİLEŞENİ
@Composable
fun ExpandableProfileCard(
    title: String,
    isExpanded: Boolean,
    zIndex: Float,
    // YENİ PARAMETRELER: Köşe yarıçapı ve gölge miktarı artık dışarıdan ayarlanabilir
    topCornerRadius: Dp = 40.dp,
    shadowElevation: Dp = 30.dp,
    onClick: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        // Parametreden gelen köşe yarıçapını kullan
        shape = RoundedCornerShape(
            topStart = topCornerRadius,
            topEnd = topCornerRadius,
            bottomStart = 0.dp,
            bottomEnd = 0.dp
        ),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        // Parametreden gelen gölge miktarını kullan
        elevation = CardDefaults.cardElevation(defaultElevation = shadowElevation),
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 0.dp)
            .zIndex(zIndex)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessLow
                )
            )
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            // Başlık
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            if (isExpanded) {
                Spacer(modifier = Modifier.height(16.dp))
                content()
            } else {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .background(Color(0xFFF9F9F9), RoundedCornerShape(12.dp))
                .border(0.5.dp, Color.LightGray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            Text(
                text = value,
                fontSize = 15.sp,
                color = Color.Black
            )
        }
    }
}