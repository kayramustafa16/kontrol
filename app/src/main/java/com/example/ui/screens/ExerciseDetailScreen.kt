package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.data.model.ExerciseDatabase
import com.example.ui.theme.*
import com.example.ui.viewmodel.AtlasViewModel

@Composable
fun ExerciseDetailScreen(
    viewModel: AtlasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val exercise by viewModel.selectedExercise.collectAsState()
    val subregion by viewModel.selectedSubregion.collectAsState()
    var isFavorited by remember { mutableStateOf(false) }
    var isVideoPlaying by remember { mutableStateOf(false) }

    // 3 selectable exercise thumbnails for the selected muscle subregion
    val subregionExercises = remember(exercise.subregionTr, exercise.primaryMuscle) {
        val list = ExerciseDatabase.getExercisesForSubregion(exercise.subregionTr)
        if (list.isNotEmpty()) list else ExerciseDatabase.getExercisesForMuscle(exercise.primaryMuscle)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("exercise_detail_screen")
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .windowInsetsPadding(WindowInsets.navigationBars),
            contentPadding = PaddingValues(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Top Bar: Back arrow, Title & Favorite icon
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = exercise.nameTr,
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${exercise.equipmentTr} • ${exercise.subregionTr}",
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    IconButton(
                        onClick = { isFavorited = !isFavorited },
                        modifier = Modifier
                            .size(40.dp)
                            .background(DarkCardSurface, CircleShape)
                            .border(1.dp, DarkCardBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = if (isFavorited) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favori",
                            tint = if (isFavorited) CrimsonRed else TextPrimary
                        )
                    }
                }
            }

            // 2. Large Video Player Area at Top (Dark Video Viewport Container)
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp)),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0C101A))
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // Ambient dark studio gradient
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(Color(0xFF161F30), Color(0xFF0A0D14))
                                    )
                                )
                        )

                        // Center Play Button / Video State
                        Column(
                            modifier = Modifier
                                .align(Alignment.Center)
                                .clickable { isVideoPlaying = !isVideoPlaying },
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(58.dp)
                                    .background(if (isVideoPlaying) Color(0xCC00E5FF) else Color(0x99000000), CircleShape)
                                    .border(2.dp, ElectricCyan, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isVideoPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (isVideoPlaying) "Durdur" else "Oynat",
                                    tint = if (isVideoPlaying) Color.Black else ElectricCyan,
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (isVideoPlaying) "Oynatılıyor: ${exercise.nameTr}" else "Video Oynatıcı",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Video kaynağı yapılandırılıyor • 1080p HD",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }

                        // Bottom Video Progress Scrubber Bar
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .background(Color(0xD9090C12))
                                .padding(horizontal = 14.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isVideoPlaying) "00:15 / 01:20" else "00:00 / 01:20",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(horizontal = 12.dp)
                                    .height(4.dp)
                                    .clip(RoundedCornerShape(2.dp))
                                    .background(Color(0xFF222B3D))
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(if (isVideoPlaying) 0.35f else 0.05f)
                                        .fillMaxHeight()
                                        .background(ElectricCyan)
                                )
                            }
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = "Tam Ekran",
                                tint = TextSecondary,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            }

            // 3. Three Selectable Exercise Thumbnails for the Selected Muscle Subregion
            item {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${exercise.subregionTr} Egzersizleri",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Seçip videoyu yükleyin",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        subregionExercises.take(3).forEach { subEx ->
                            val isSelected = subEx.id == exercise.id
                            Card(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(100.dp)
                                    .clip(RoundedCornerShape(14.dp))
                                    .border(
                                        width = if (isSelected) 2.dp else 1.dp,
                                        color = if (isSelected) ElectricCyan else DarkCardBorder,
                                        shape = RoundedCornerShape(14.dp)
                                    )
                                    .clickable {
                                        viewModel.openExerciseDetail(subEx)
                                    }
                                    .testTag("exercise_thumb_${subEx.id}"),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) Color(0xFF19253B) else DarkCardSurface
                                )
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(8.dp),
                                    verticalArrangement = Arrangement.SpaceBetween,
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .background(
                                                if (isSelected) ElectricCyan.copy(alpha = 0.25f) else Color(0xFF1E283C),
                                                CircleShape
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isSelected) Icons.Default.PlayCircle else Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            tint = if (isSelected) ElectricCyan else TextSecondary,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = subEx.nameTr,
                                            color = if (isSelected) ElectricCyan else TextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                            maxLines = 1
                                        )
                                        Text(
                                            text = subEx.equipmentTr,
                                            color = TextMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 4. Large Anatomical Illustration Highlighting the EXACT Selected Muscle Subregion (Orange/Red)
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
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Hedeflenen Bölge: ${exercise.subregionTr}",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = CrimsonRed.copy(alpha = 0.2f),
                                border = ButtonDefaults.outlinedButtonBorder.copy(
                                    brush = androidx.compose.ui.graphics.SolidColor(CrimsonRed)
                                )
                            ) {
                                Text(
                                    text = "Primer Hedef",
                                    color = CrimsonRed,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // High-Contrast Anatomical Subregion Diagram
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(130.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF111624))
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val cx = size.width / 2f
                                val cy = size.height / 2f

                                // Torso Background Wireframe
                                drawRoundRect(
                                    color = Color(0xFF222B3E),
                                    topLeft = Offset(cx - 65f, cy - 45f),
                                    size = Size(130f, 95f),
                                    cornerRadius = CornerRadius(16f)
                                )

                                // Specific Subregion Highlight in Vivid Red / Orange
                                when {
                                    exercise.subregionTr.contains("Üst Göğüs") -> {
                                        // Left Clavicular Upper Head
                                        drawRoundRect(
                                            color = CrimsonRed,
                                            topLeft = Offset(cx - 52f, cy - 40f),
                                            size = Size(48f, 24f),
                                            cornerRadius = CornerRadius(6f)
                                        )
                                        // Right Clavicular Upper Head
                                        drawRoundRect(
                                            color = CrimsonRed,
                                            topLeft = Offset(cx + 4f, cy - 40f),
                                            size = Size(48f, 24f),
                                            cornerRadius = CornerRadius(6f)
                                        )
                                        // Remaining Chest (Dimmed)
                                        drawRoundRect(
                                            color = Color(0xFF2C364F),
                                            topLeft = Offset(cx - 52f, cy - 12f),
                                            size = Size(104f, 28f),
                                            cornerRadius = CornerRadius(6f)
                                        )
                                    }
                                    exercise.subregionTr.contains("Alt Göğüs") -> {
                                        // Lower Chest highlighted in Red
                                        drawRoundRect(
                                            color = Color(0xFF2C364F),
                                            topLeft = Offset(cx - 52f, cy - 40f),
                                            size = Size(104f, 26f),
                                            cornerRadius = CornerRadius(6f)
                                        )
                                        drawRoundRect(
                                            color = CrimsonRed,
                                            topLeft = Offset(cx - 52f, cy - 10f),
                                            size = Size(104f, 26f),
                                            cornerRadius = CornerRadius(6f)
                                        )
                                    }
                                    exercise.subregionTr.contains("Omuz") -> {
                                        // Deltoid heads highlighted
                                        drawCircle(color = CrimsonRed, radius = 22f, center = Offset(cx - 65f, cy - 25f))
                                        drawCircle(color = CrimsonRed, radius = 22f, center = Offset(cx + 65f, cy - 25f))
                                    }
                                    else -> {
                                        // Generic Full Pectoral / Primary Muscle
                                        drawRoundRect(
                                            color = CrimsonRed,
                                            topLeft = Offset(cx - 50f, cy - 35f),
                                            size = Size(100f, 48f),
                                            cornerRadius = CornerRadius(10f)
                                        )
                                    }
                                }
                            }

                            // Subregion badge indicator overlay
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = Color(0xDD000000),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(8.dp)
                            ) {
                                Text(
                                    text = "${exercise.subregionTr} İzolasyonu",
                                    color = NeonOrange,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                }
            }

            // 5. "Nasıl Yapılır?" Step-by-Step Instructions, Correct Technique, Breathing & Mistakes
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Nasıl Yapılır?",
                            color = TextPrimary,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        exercise.instructionsTr.forEachIndexed { index, step ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(ElectricCyan.copy(alpha = 0.2f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        color = ElectricCyan,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                    text = step,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))
                        Divider(color = DarkCardBorder, thickness = 0.5.dp)
                        Spacer(modifier = Modifier.height(12.dp))

                        // Breathing Tip
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(Icons.Default.Air, contentDescription = null, tint = BrightBlue, modifier = Modifier.size(18.dp))
                            Text(
                                text = "Nefes Tekniği: ${exercise.breathingTipTr}",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        // Common Mistakes
                        exercise.commonMistakesTr.forEach { mistake ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                modifier = Modifier.padding(top = 6.dp)
                            ) {
                                Icon(Icons.Default.WarningAmber, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(16.dp))
                                Text(
                                    text = "Dikkat: $mistake",
                                    color = CrimsonRed.copy(alpha = 0.9f),
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
