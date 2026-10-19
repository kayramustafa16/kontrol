package com.example.ui.screens

import android.graphics.BitmapFactory
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.MetricSmallCard
import com.example.ui.theme.*
import com.example.ui.viewmodel.AtlasViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
fun HomeScreen(
    viewModel: AtlasViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    val dailyLifestyle by viewModel.dailyLifestyle.collectAsState()
    val todayMeals by viewModel.todayMeals.collectAsState()
    val workoutHistory by viewModel.workoutHistory.collectAsState()
    val weightHistory by viewModel.weightHistory.collectAsState()

    val totalCaloriesConsumed = todayMeals.sumOf { it.calories }.toInt()

    // Real data calculations for development summary
    val completedWorkoutsCount = workoutHistory.size
    val latestWeight = weightHistory.lastOrNull()?.weightKg ?: userProfile.currentWeightKg
    val firstWeight = weightHistory.firstOrNull()?.weightKg ?: latestWeight
    val weightDelta = latestWeight - firstWeight
    val totalVolumeTracked = workoutHistory.sumOf { it.totalVolumeKg }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("home_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // 1. Top App Bar: Brand Logo & Notification
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(ElectricCyan.copy(alpha = 0.15f), CircleShape)
                            .border(1.dp, ElectricCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FitnessCenter,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Text(
                        text = "ANABOLİK ATLAS",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                }

                IconButton(
                    onClick = { viewModel.navigateTo("profile") },
                    modifier = Modifier
                        .size(42.dp)
                        .background(DarkCardSurface, CircleShape)
                        .border(1.dp, DarkCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Notifications,
                        contentDescription = "Bildirimler",
                        tint = TextPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // 2. High-Quality Muscular Athlete Hero Banner with Smooth Slideshow & Dark-Gym Lighting
        item {
            HeroSlideshowBanner()
        }

        // 3. Prominent "Antrenmanı Başlat" Button (Opens Dedicated Anatomy Workout Selection Screen)
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Button(
                    onClick = { viewModel.navigateTo("anatomy_workout_selection") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp)
                        .testTag("start_workout_button"),
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(26.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Antrenmanı Başlat",
                        color = Color.White,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )
                    Spacer(modifier = Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Default.AccessibilityNew,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }

        // 4. Compact Daily Metric Cards: Kalori, Su, Uyku, Adım
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MetricSmallCard(
                    titleTr = "Kalori",
                    valueStr = "$totalCaloriesConsumed",
                    icon = Icons.Default.LocalFireDepartment,
                    iconColor = CrimsonRed,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo("nutrition") }
                )
                MetricSmallCard(
                    titleTr = "Su",
                    valueStr = "${dailyLifestyle.waterLiters} L",
                    icon = Icons.Default.WaterDrop,
                    iconColor = BrightBlue,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo("lifestyle") }
                )
                MetricSmallCard(
                    titleTr = "Uyku",
                    valueStr = "${dailyLifestyle.sleepHours} s",
                    icon = Icons.Default.Bedtime,
                    iconColor = Color(0xFFA78BFA),
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo("lifestyle") }
                )
                MetricSmallCard(
                    titleTr = "Adım",
                    valueStr = "${dailyLifestyle.steps}",
                    icon = Icons.Default.DirectionsRun,
                    iconColor = NeonGreen,
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.navigateTo("lifestyle") }
                )
            }
        }

        // 5. Development Summary (Gelişim Özeti) Populated from Real Saved Data
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp))
                    .clickable { viewModel.navigateTo("progress") },
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Gelişim Özeti",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = DarkCardSurfaceElevated
                        ) {
                            Text(
                                text = "Kayıtlı Veriler",
                                color = ElectricCyan,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$completedWorkoutsCount",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Antrenman",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = if (totalVolumeTracked > 0) "${(totalVolumeTracked / 1000).toInt()}t" else "0 kg",
                                color = ElectricCyan,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Toplam Hacim",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            val sign = if (weightDelta >= 0) "+" else ""
                            Text(
                                text = "$sign${String.format("%.1f", weightDelta)} kg",
                                color = if (weightDelta >= 0) ElectricCyan else CrimsonRed,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Kilo Değişimi",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "${String.format("%.1f", latestWeight)} kg",
                                color = TextPrimary,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Mevcut Kilo",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // 6. Anatomy Exploration Quick Shortcut
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp))
                    .clickable { viewModel.navigateTo("anatomy_workout_selection") },
                colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceElevated)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(Color(0xFF222B3F), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.ViewInAr,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "3D Kas Anatomisi Keşfi",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Bölgesel hedefleme ve egzersiz keşfi",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Aç",
                        tint = ElectricCyan
                    )
                }
            }
        }
    }
}

@Composable
fun HeroSlideshowBanner() {
    val context = LocalContext.current
    val imageFileNames = remember { listOf("1.webp", "2.webp", "3.webp", "4.webp") }

    // Preload and cache local image resources into memory (assets or drawables)
    val loadedBitmaps = remember(context) {
        val bitmaps = mutableListOf<ImageBitmap>()
        for (name in imageFileNames) {
            var loaded = false
            // Primary path: assets folder (1.webp, 2.webp, 3.webp, 4.webp)
            try {
                context.assets.open(name).use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()?.let {
                        bitmaps.add(it)
                        loaded = true
                    }
                }
            } catch (_: Exception) {}

            // Secondary fallback path: res/drawable (hero_1, hero_2, etc.)
            if (!loaded) {
                val resName = "hero_" + name.substringBefore(".")
                val resId = context.resources.getIdentifier(resName, "drawable", context.packageName)
                if (resId != 0) {
                    try {
                        BitmapFactory.decodeResource(context.resources, resId)?.asImageBitmap()?.let {
                            bitmaps.add(it)
                            loaded = true
                        }
                    } catch (_: Exception) {}
                }
            }
        }
        bitmaps
    }

    var currentIndex by remember { mutableIntStateOf(0) }

    // Slideshow loop: advance every 3 seconds through 1 -> 2 -> 3 -> 4 -> 1
    // LaunchedEffect is automatically cancelled when leaving composition (screen not visible) and resumes safely
    if (loadedBitmaps.size > 1) {
        LaunchedEffect(loadedBitmaps.size) {
            while (isActive) {
                delay(3000L)
                currentIndex = (currentIndex + 1) % loadedBitmaps.size
            }
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(26.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(26.dp)),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F131D))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (loadedBitmaps.isNotEmpty()) {
                Crossfade(
                    targetState = currentIndex,
                    animationSpec = tween(durationMillis = 450),
                    modifier = Modifier.fillMaxSize(),
                    label = "HeroSlideshowCrossfade"
                ) { index ->
                    val bitmap = loadedBitmaps.getOrNull(index) ?: loadedBitmaps.first()
                    Image(
                        bitmap = bitmap,
                        contentDescription = "Kaslı Sporcu Hero Görseli ${index + 1}",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                        alignment = Alignment.CenterEnd
                    )
                }
            } else {
                Image(
                    painter = painterResource(id = R.drawable.hero_1),
                    contentDescription = "Kaslı Sporcu Hero Arka Planı",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alignment = Alignment.CenterEnd
                )
            }

            // Subtle Dark Gradient Scrim on the left side to guarantee optimal text readability
            // while keeping athlete's face, deltoids, and torso clearly visible on the right
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(
                                Color(0xEE090B10),
                                Color(0x99090B10),
                                Color(0x22090B10),
                                Color.Transparent
                            ),
                            startX = 0f,
                            endX = 750f
                        )
                    )
            )

            // Left-aligned greeting text overlay in readable white (fixed above images)
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(start = 24.dp, end = 20.dp, top = 28.dp, bottom = 28.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(0.62f)
                ) {
                    Text(
                        text = "Merhaba,",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "bugün daha güçlü bir sen için hazırsın!",
                        color = Color.White,
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Black,
                        lineHeight = 28.sp
                    )
                }
            }
        }
    }
}
