package com.example.yurt360.user.mainScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.yurt360.R
import com.example.yurt360.common.components.CustomBottomNavigationBar
import com.example.yurt360.common.model.User
import com.example.yurt360.common.utils.OrangePrimary

val InputBackground = Color(0xFFFAFAFA)

@Composable
fun ProfileScreen(
    user: User,
    onNavigate: (String) -> Unit,
    onMenuClick: () -> Unit
) {
    var expandedCard by remember { mutableStateOf<String?>(null) }

    // Kartlar arası boşluk animasyonu
    val personalInfoOffset by animateDpAsState(
        targetValue = if (expandedCard == "contact") (-20).dp else (-60).dp,
        label = "personalInfoOffset"
    )

    // Fotoğrafın bittiği yer hesabı
    val contentTopPadding = 240.dp

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. KATMAN: ARKA PLAN
        Image(
            painter = painterResource(id = R.drawable.ic_launcher_background),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // 2. KATMAN: PROFİL FOTOĞRAFI
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 80.dp)
                .zIndex(1f)
        ) {
            ProfilePhotoSection(user.image_url)
        }

        // 3. KATMAN: KARTLAR
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxSize()
                .padding(top = contentTopPadding)
                .verticalScroll(rememberScrollState(), enabled = false)
                .padding(bottom = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {

            // --- PAROLA GÜNCELLE BUTONU ---
            Button(
                onClick = { onNavigate("update_password") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFF0E0)),
                shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp, bottomStart = 0.dp, bottomEnd = 0.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 0.dp),
                modifier = Modifier
                    .height(32.dp)
                    .zIndex(0f)
            ) {
                Text(
                    text = "Parola Güncelle",
                    color = OrangePrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(0.dp))

            // --- İLETİŞİM BİLGİLERİ KARTI ---
            ProfileSectionCard(
                title = "İletişim Bilgileri",
                isExpanded = expandedCard == "contact",
                modifier = Modifier.zIndex(0f),
                contentPaddingBottom = 60.dp,
                onHeaderClick = {
                    expandedCard = if (expandedCard == "contact") null else "contact"
                }
            ) {
                ContactInfoContent(user)
            }

            // --- KİŞİSEL BİLGİLER KARTI ---
            ProfileSectionCard(
                title = "Kişisel Bilgiler",
                isExpanded = expandedCard == "personal",
                modifier = Modifier
                    .offset(y = personalInfoOffset)
                    .zIndex(1f)
                    .weight(1f, fill = false)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
                        spotColor = Color.Black,
                        ambientColor = Color.Black
                    )
                    .border(
                        width = 0.5.dp,
                        color = Color.Black.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp)
                    ),
                contentPaddingBottom = 130.dp,
                onHeaderClick = {
                    expandedCard = if (expandedCard == "personal") null else "personal"
                }
            ) {
                Column(
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    PersonalInfoContent(user)
                }
            }
        }

        // 4. KATMAN: SOL ÜST MENÜ İKONU
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = "Menu",
            tint = Color.White,
            modifier = Modifier
                .padding(top = 50.dp, start = 20.dp)
                .size(32.dp)
                .align(Alignment.TopStart)
                .clickable { onMenuClick() } // Callback buraya bağlandı
        )

        // 5. KATMAN: BOTTOM BAR
        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            CustomBottomNavigationBar(onNavigate = onNavigate)
        }
    }
}

@Composable
fun ProfilePhotoSection(imageUrl: String) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(130.dp)
            .clip(CircleShape)
            .background(Color.White)
            .padding(4.dp)
            .clip(CircleShape)
            .background(Color.White)
            .shadow(elevation = 10.dp, shape = CircleShape)
    ) {
        Text(text = "fotoğrafı", color = Color.Black, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ProfileSectionCard(
    title: String,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    contentPaddingBottom: Dp = 32.dp,
    onHeaderClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(topStart = 40.dp, topEnd = 40.dp),
        color = Color.White,
        shadowElevation = 0.dp
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onHeaderClick() }
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = title,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(expandFrom = Alignment.Top) + fadeIn(),
                exit = shrinkVertically(shrinkTowards = Alignment.Bottom) + fadeOut()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                ) {
                    content()
                }
            }

            if (contentPaddingBottom > 0.dp) {
                Spacer(modifier = Modifier.height(contentPaddingBottom))
            }
        }
    }
}

@Composable
fun ContactInfoContent(user: User) {
    ProfileInfoRow(label = "Telefon", value = user.phone)
    Spacer(modifier = Modifier.height(12.dp))
    ProfileInfoRow(label = "E-posta", value = user.email)
}

@Composable
fun PersonalInfoContent(user: User) {
    ProfileInfoRow(label = "Kimlik No", value = user.tc)
    ProfileInfoRow(label = "Ad", value = user.name)
    ProfileInfoRow(label = "Soyad", value = user.surname)
    ProfileInfoRow(label = "Cinsiyet", value = user.gender)
    ProfileInfoRow(label = "Kan Grubu", value = user.bloodType)
    ProfileInfoRow(label = "Doğum Tarihi", value = user.birthDate)
    ProfileInfoRow(label = "Yurt", value = user.location)
}

@Composable
fun ProfileInfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically // Yazı ve kutuyu dikeyde ortalar
    ) {
        // Sol taraftaki Başlık (Label)
        Text(
            text = label,
            color = Color.Gray,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(100.dp) // Tüm başlıkların aynı hizada durması için sabit genişlik
                .padding(end = 8.dp)
        )

        // Sağ taraftaki Değer Kutusu (TextField görünümü)
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = InputBackground,
            border = BorderStroke(1.dp, Color(0xFFE0E0E0)),
            modifier = Modifier
                .weight(1f) // Kalan boşluğu doldurur ama ekranın tamamını kaplamaz
                .heightIn(min = 45.dp) // Kutunun yüksekliğini sabitler
        ) {
            Box(
                contentAlignment = Alignment.CenterStart,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
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
}