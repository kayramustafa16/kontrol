package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MuscleGroup
import com.example.ui.components.AnatomyViewport3D
import com.example.ui.theme.*
import com.example.ui.viewmodel.AtlasViewModel

@Composable
fun AnatomyScreen(
    viewModel: AtlasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val selectedMuscle by viewModel.selectedMuscle.collectAsState()
    val customModelPath by viewModel.customModelPath.collectAsState()

    val quickMuscles = listOf(
        MuscleGroup.CHEST,
        MuscleGroup.BACK,
        MuscleGroup.SHOULDERS,
        MuscleGroup.BICEPS,
        MuscleGroup.ABS,
        MuscleGroup.QUADS
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("anatomy_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
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

            Text(
                text = "3D Kas Modeli",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { viewModel.navigateTo("model_manager") },
                modifier = Modifier
                    .size(40.dp)
                    .background(DarkCardSurface, CircleShape)
                    .border(1.dp, DarkCardBorder, CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Model Ayarları",
                    tint = TextPrimary
                )
            }
        }

        // Viewport + Left Side Quick Selector Area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Main 3D Viewport
            AnatomyViewport3D(
                selectedMuscle = selectedMuscle,
                onMuscleSelected = { viewModel.selectMuscle(it) },
                customModelPath = customModelPath,
                onOpenModelManager = { viewModel.navigateTo("model_manager") },
                modifier = Modifier.fillMaxSize()
            )

            // Left Vertical Quick Muscle Selector Bar
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 12.dp)
                    .background(Color(0xCC111522), RoundedCornerShape(24.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(24.dp))
                    .padding(vertical = 8.dp, horizontal = 6.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                quickMuscles.forEach { muscle ->
                    val isSelected = selectedMuscle == muscle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectMuscle(muscle) }
                            .padding(4.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(
                                    if (isSelected) CrimsonRed else Color(0xFF1E2538),
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (muscle) {
                                    MuscleGroup.CHEST -> Icons.Default.Accessibility
                                    MuscleGroup.BACK -> Icons.Default.AirlineSeatFlat
                                    MuscleGroup.SHOULDERS -> Icons.Default.SportsHandball
                                    MuscleGroup.BICEPS -> Icons.Default.FitnessCenter
                                    MuscleGroup.ABS -> Icons.Default.ViewAgenda
                                    else -> Icons.Default.DirectionsRun
                                },
                                contentDescription = muscle.displayNameTr,
                                tint = if (isSelected) Color.White else TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Text(
                            text = muscle.displayNameTr.split(" ").first(),
                            color = if (isSelected) CrimsonRed else TextMuted,
                            fontSize = 9.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Bottom Selected Muscle Floating Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp))
                .clickable { viewModel.openMuscleDetail(selectedMuscle) },
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
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(RoundedCornerShape(14.dp))
                            .background(CrimsonRed.copy(alpha = 0.15f))
                            .border(1.dp, CrimsonRed, RoundedCornerShape(14.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccessibilityNew,
                            contentDescription = null,
                            tint = CrimsonRed,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Column {
                        Text(
                            text = selectedMuscle.displayNameTr,
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = selectedMuscle.latinName,
                            color = ElectricCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = selectedMuscle.subMusclesTr.joinToString(" • "),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 1
                        )
                    }
                }

                IconButton(
                    onClick = { viewModel.openMuscleDetail(selectedMuscle) }
                ) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Detay",
                        tint = ElectricCyan
                    )
                }
            }
        }
    }
}
