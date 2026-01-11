package com.example.yurt360.user.mainScreen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yurt360.R
import com.example.yurt360.common.components.AnnouncementViewModel
import com.example.yurt360.common.components.UserBottomNavigationBar
import com.example.yurt360.common.model.User
import com.example.yurt360.common.utils.OrangePrimary
import com.example.yurt360.common.utils.purpleLinear

@Composable
fun UserHomeScreen(
    user: User,
    viewModel: AnnouncementViewModel = viewModel(),
    onMenuClick: () -> Unit,
    onNavigation: (String) -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.observeAnnouncements(context)
    }

    val latestAnnouncement = viewModel.announcements.firstOrNull()

    Scaffold(
        bottomBar = {
            UserBottomNavigationBar(
                onNavigate = onNavigation
            )
        },
        containerColor = Color.White
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight(),
                contentAlignment = Alignment.CenterStart
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.sidebar),
                    contentDescription = "Menu",
                    tint = Color.Unspecified,
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onMenuClick() }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                modifier = Modifier.padding(horizontal = 20.dp),
            ) {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(OrangePrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mainscreenperson),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.White),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(2f)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column {
                        Text(
                            text = "Hoş Geldin!",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = "Yeditepeli",
                            fontSize = 15.sp,
                            color = Color.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(15.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(30.dp))
                        .background(purpleLinear)
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.duyuru_arkaplan),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        alpha = 0.5f,
                        modifier = Modifier.matchParentSize()
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Box(
                                modifier = Modifier
                                    .background(Color.White.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                                    .padding(horizontal = 28.dp, vertical = 4.dp)
                            ) {
                                Text("Duyurular", color = Color.White, fontSize = 20.sp)
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = latestAnnouncement?.title ?: "Duyuru Yok",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(5.dp))

                            Text(
                                text = latestAnnouncement?.description ?: "Şu anda görüntülenecek güncel bir duyuru bulunmamaktadır.",
                                fontSize = 14.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 20.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Image(
                            painter = painterResource(id = R.drawable.megafon),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(Color.White),
                            contentScale = ContentScale.Fit,
                            modifier = Modifier
                                .size(85.dp)
                                .offset(x = (0).dp, y = (25).dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                Text(
                    text = "Hızlı Erişim",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mainscreenbutton),
                        contentDescription = "Hızlı Erişim Menüsü",
                        modifier = Modifier.fillMaxWidth(),
                        contentScale = ContentScale.FillWidth
                    )

                    Column(modifier = Modifier.matchParentSize()) {
                        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigation("user_study_area") })
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigation("user_laundrymain") })
                        }
                        Row(modifier = Modifier.weight(1f).fillMaxWidth()) {
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigation("user_applications") })
                            Box(modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigation("user_menu") })
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(5.dp))
        }
    }
}