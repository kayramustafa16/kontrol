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
import androidx.compose.runtime.*
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
fun AnatomyWorkoutSelectionScreen(
    viewModel: AtlasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val selectedMuscle by viewModel.selectedMuscle.collectAsState()
    val customModelPath by viewModel.customModelPath.collectAsState()

    val verticalMuscles = listOf(
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
            .testTag("anatomy_workout_selection_screen")
    ) {
        // Top App Bar: Back Button, Title, and "Program Hazırla" Action Button
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

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Bölge Seçimi",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tek Kas Grubu ve Alt Bölgeler",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            // Distinct "Program Hazırla" Button opening multi-selection program builder
            Button(
                onClick = { viewModel.navigateTo("program_builder") },
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E2D47)),
                border = ButtonDefaults.outlinedButtonBorder.copy(
                    brush = androidx.compose.ui.graphics.SolidColor(ElectricCyan)
                ),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("open_program_builder_button")
            ) {
                Icon(
                    imageVector = Icons.Default.PlaylistAdd,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Program Hazırla",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Dedicated Full-Screen 3D Anatomy Model Area (Dominates available screen space)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 12.dp, vertical = 4.dp)
        ) {
            // Main 3D Viewport
            AnatomyViewport3D(
                selectedMuscle = selectedMuscle,
                onMuscleSelected = { muscle ->
                    viewModel.selectMuscle(muscle)
                },
                highlightedMuscles = setOf(selectedMuscle),
                customModelPath = customModelPath,
                onOpenModelManager = { viewModel.navigateTo("model_manager") },
                modifier = Modifier.fillMaxSize()
            )

            // Vertical Muscle-Selection Menu on the Left: Göğüs, Sırt, Omuz, Kol, Karın, Bacak
            Column(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .padding(start = 8.dp)
                    .background(Color(0xD9101420), RoundedCornerShape(22.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp))
                    .padding(vertical = 10.dp, horizontal = 6.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                verticalMuscles.forEach { muscle ->
                    val isSelected = selectedMuscle == muscle
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .clickable { viewModel.selectMuscle(muscle) }
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                            .testTag("muscle_tab_${muscle.name.lowercase()}")
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(
                                    if (isSelected) CrimsonRed else Color(0xFF1B2336),
                                    CircleShape
                                )
                                .border(
                                    if (isSelected) 2.dp else 1.dp,
                                    if (isSelected) Color.White.copy(alpha = 0.5f) else DarkCardBorder,
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
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = muscle.displayNameTr.split(" ").first(),
                            color = if (isSelected) CrimsonRed else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Bottom Selected Muscle Group & Subregion Discovery Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .clip(RoundedCornerShape(22.dp))
                .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp)),
            colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceElevated)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Active Muscle Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(12.dp)
                                .background(CrimsonRed, CircleShape)
                        )
                        Text(
                            text = "${selectedMuscle.displayNameTr} Kasları",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = selectedMuscle.latinName,
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "Hedeflemek istediğiniz alt kas bölgesini seçin:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Subregion Chips Row (e.g. Üst Göğüs, Orta Göğüs, Alt Göğüs)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    selectedMuscle.subMusclesTr.forEach { subregion ->
                        Surface(
                            onClick = {
                                viewModel.openSubregionExercises(subregion)
                            },
                            shape = RoundedCornerShape(14.dp),
                            color = Color(0xFF1E283C),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(ElectricCyan.copy(alpha = 0.6f))
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("subregion_${subregion.replace(" ", "_")}")
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = subregion,
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Egzersizler →",
                                    color = ElectricCyan,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
