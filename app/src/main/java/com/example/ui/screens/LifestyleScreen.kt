package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AtlasViewModel

@Composable
fun LifestyleScreen(
    viewModel: AtlasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val dailyLifestyle by viewModel.dailyLifestyle.collectAsState()
    var activeTab by remember { mutableStateOf("Su") }
    val tabs = listOf("Su", "Uyku", "Adım", "Ruh Hali")

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("lifestyle_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { viewModel.navigateBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .background(DarkCardSurface, CircleShape)
                        .border(1.dp, DarkCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Geri",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Yaşam Takibi",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Sub Tabs (Su, Uyku, Adım, Ruh Hali)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(DarkCardSurface, RoundedCornerShape(18.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                tabs.forEach { tab ->
                    val isSelected = activeTab == tab
                    Surface(
                        onClick = { activeTab = tab },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) ElectricCyan else Color.Transparent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        // Main Water Glass Tracker Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Big Water Glass Icon Graphic
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .background(BrightBlue.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                                .border(1.dp, BrightBlue.copy(alpha = 0.5f), RoundedCornerShape(20.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.WaterDrop,
                                contentDescription = null,
                                tint = BrightBlue,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        // Liter Value
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${dailyLifestyle.waterLiters} L",
                                color = TextPrimary,
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "/ ${dailyLifestyle.waterTargetLiters} L",
                                color = TextSecondary,
                                fontSize = 14.sp
                            )
                        }

                        // +/- Quick Buttons
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            IconButton(
                                onClick = { viewModel.adjustWater(0.25) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(DarkCardSurfaceElevated, CircleShape)
                                    .border(1.dp, DarkCardBorder, CircleShape)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Ekle", tint = ElectricCyan)
                            }
                            IconButton(
                                onClick = { viewModel.adjustWater(-0.25) },
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(DarkCardSurfaceElevated, CircleShape)
                                    .border(1.dp, DarkCardBorder, CircleShape)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Eksilt", tint = TextSecondary)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // 6 Glass Progress Icons Row
                    val filledGlasses = (dailyLifestyle.waterLiters / 0.4).toInt().coerceIn(0, 6)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        for (i in 1..6) {
                            val isFilled = i <= filledGlasses
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .background(
                                        if (isFilled) BrightBlue.copy(alpha = 0.25f) else Color(0xFF1B2334),
                                        RoundedCornerShape(10.dp)
                                    )
                                    .border(
                                        1.dp,
                                        if (isFilled) BrightBlue else DarkCardBorder,
                                        RoundedCornerShape(10.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.LocalDrink,
                                    contentDescription = null,
                                    tint = if (isFilled) BrightBlue else TextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Weekly Average Card with 7-Day Bar Chart
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Text(
                        text = "Bu Hafta Ortalaması",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "2.3 L",
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // 7-day mini bar chart
                    val days = listOf("Pzt", "Sal", "Çar", "Per", "Cum", "Cmt", "Paz")
                    val heights = listOf(0.7f, 0.9f, 0.85f, 1.0f, 0.65f, 0.8f, 0.95f)

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(90.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        days.forEachIndexed { idx, day ->
                            val barHeightFraction = heights[idx]
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Bottom,
                                modifier = Modifier.weight(1f)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(18.dp)
                                        .fillMaxHeight(barHeightFraction)
                                        .clip(RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .background(ElectricCyan)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = day,
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Hatırlatıcılar (Reminders Toggle) Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Hatırlatıcılar",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Alarm,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(24.dp)
                            )
                            Text(
                                text = "Her 2 saatte bir hatırlat",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }

                        Switch(
                            checked = dailyLifestyle.reminderEnabled,
                            onCheckedChange = { viewModel.updateLifestyle(reminders = it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.Black,
                                checkedTrackColor = ElectricCyan,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = DarkCardSurfaceElevated
                            )
                        )
                    }
                }
            }
        }
    }
}
