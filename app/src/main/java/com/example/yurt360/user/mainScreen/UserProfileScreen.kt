package com.example.yurt360.user.mainScreen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.yurt360.R
import com.example.yurt360.common.components.UserBottomNavigationBar
import com.example.yurt360.common.model.User

val InputBackground = Color(0xFFFAFAFA)

@Composable
fun ProfileScreen(
    user: User,
    onNavigate: (String) -> Unit,
    onMenuClick: () -> Unit
) {
    var isContactExpanded by remember { mutableStateOf(false) }

    val personalInfoOffset by animateDpAsState(
        targetValue = if (isContactExpanded) (-130).dp else (-100).dp,
        label = "personalInfoOffset"
    )

    val contentTopPadding = 260.dp

    Box(modifier = Modifier.fillMaxSize()) {

        // 1. Arka Plan Resmi
        Image(
            painter = painterResource(id = R.drawable.profilebackground),
            contentDescription = "Background",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
        )

        // 2. Profil Fotoğrafı
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 110.dp)
                .zIndex(1f)
        ) {
        }

        // 3. Ana İçerik (Kartlar)
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxSize()
                .padding(top = contentTopPadding)
                .padding(bottom = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // --- İLETİŞİM BİLGİLERİ (Üst Kart) ---
            ProfileSectionCard(
                title = "İletişim Bilgileri",
                isExpanded = isContactExpanded,
                modifier = Modifier
                    .zIndex(0f)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(topStart = 85.dp, topEnd = 85.dp),
                        spotColor = Color.Black,
                        ambientColor = Color.Black
                    ),
                contentPaddingBottom = if (isContactExpanded) 150.dp else 100.dp,
                customShape = RoundedCornerShape(topStart = 85.dp, topEnd = 85.dp),
                onHeaderClick = {
                    isContactExpanded = !isContactExpanded
                }
            ) {
                ContactInfoContent(user)
            }

            // --- KİŞİSEL BİLGİLER (Alt Kart) ---
            Box(
                modifier = Modifier
                    .weight(1f)
                    .zIndex(1f)
            ) {
                ProfileSectionCard(
                    title = "Kişisel Bilgiler",
                    isExpanded = true,
                    modifier = Modifier
                        .offset(y = personalInfoOffset)
                        .fillMaxWidth()
                        .height(1500.dp)
                        .shadow(elevation = 8.dp, shape = RoundedCornerShape(topStart = 85.dp, topEnd = 85.dp))
                        .border(
                            0.5.dp,
                            Color.Black.copy(alpha = 0.1f),
                            RoundedCornerShape(topStart = 85.dp, topEnd = 85.dp)
                        ),
                    contentPaddingBottom = 0.dp,
                    customShape = RoundedCornerShape(topStart = 85.dp, topEnd = 85.dp),
                    onHeaderClick = {}
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        PersonalInfoContent(user)
                        Spacer(modifier = Modifier.height(250.dp))
                    }
                }
            }
        }

        Icon(
            painter = painterResource(id = R.drawable.sidebar),
            contentDescription = "Menu",
            tint = Color.Unspecified,
            modifier = Modifier
                .padding(top = 50.dp, start = 20.dp)
                .size(32.dp)
                .align(Alignment.TopStart)
                .clickable { onMenuClick() }
        )

        Box(
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            UserBottomNavigationBar(onNavigate = onNavigate)
        }
    }
}

// --- YARDIMCI BİLEŞENLER ---

@Composable
fun ProfileSectionCard(
    title: String,
    isExpanded: Boolean,
    modifier: Modifier = Modifier,
    contentPaddingBottom: Dp = 32.dp,
    customShape: androidx.compose.ui.graphics.Shape = RoundedCornerShape(topStart = 85.dp, topEnd = 85.dp),
    onHeaderClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = customShape,
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
    ProfileInfoRow(label = "Öğrenci No", value = user.studentNumber)
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
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = Color.Black,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier
                .width(100.dp)
                .padding(end = 8.dp)
        )
        Surface(
            modifier = Modifier
                .weight(1f)
                .heightIn(min = 50.dp)
                .shadow(4.dp, RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            color = Color.White
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