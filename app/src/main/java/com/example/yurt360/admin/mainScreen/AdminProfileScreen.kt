package com.example.yurt360.admin.mainScreen

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.yurt360.R
import com.example.yurt360.common.components.CustomAdminBottomNavigationBar
import com.example.yurt360.common.model.Admin
import com.example.yurt360.user.mainScreen.ProfileInfoRow
import com.example.yurt360.user.mainScreen.ProfileSectionCard

@Composable
fun AdminProfileScreen(
    admin: Admin,
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

        // 2. Profil Fotoğrafı (Admin modelinde image_url olmadığı için varsayılan bir ikon veya boş bırakılabilir)
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 110.dp)
                .zIndex(1f)
        ) {
        }

        // 3. Ana İçerik
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxSize()
                .padding(top = contentTopPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {

            // --- İLETİŞİM BİLGİLERİ ---
            ProfileSectionCard(
                title = "İletişim Bilgileri",
                isExpanded = isContactExpanded,
                modifier = Modifier
                    .zIndex(0f)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(topStart = 85.dp, topEnd = 85.dp)
                    ),
                contentPaddingBottom = if (isContactExpanded) 150.dp else 100.dp,
                onHeaderClick = { isContactExpanded = !isContactExpanded }
            ) {
                ProfileInfoRow(label = "E-posta", value = admin.email)
            }

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
                    onHeaderClick = {}
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Admin modelinde bulunan sınırlı veriler
                        ProfileInfoRow(label = "Ad", value = admin.name)
                        ProfileInfoRow(label = "Soyad", value = admin.surname)
                        ProfileInfoRow(label = "Yönetici ID", value = admin.id)

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

        Box(modifier = Modifier.align(Alignment.BottomCenter)) {
            CustomAdminBottomNavigationBar(onNavigate = onNavigate)
        }
    }
}