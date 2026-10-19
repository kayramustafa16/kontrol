package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.data.model.ExerciseDatabase
import com.example.data.model.MuscleGroup
import com.example.ui.components.AnatomyViewport3D
import com.example.ui.theme.*
import com.example.ui.viewmodel.AtlasViewModel

@Composable
fun ProgramBuilderScreen(
    viewModel: AtlasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val selectedProgramMuscles by viewModel.selectedProgramMuscles.collectAsState()
    val customModelPath by viewModel.customModelPath.collectAsState()

    var customProgramName by remember { mutableStateOf("Özel Split") }

    val allMuscles = listOf(
        MuscleGroup.CHEST,
        MuscleGroup.BACK,
        MuscleGroup.SHOULDERS,
        MuscleGroup.BICEPS,
        MuscleGroup.ABS,
        MuscleGroup.QUADS
    )

    // Validated exercises matching the selected muscles
    val matchedExercises = remember(selectedProgramMuscles) {
        selectedProgramMuscles.flatMap { muscle ->
            ExerciseDatabase.getExercisesForMuscle(muscle).take(2)
        }
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("program_builder_screen"),
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(1.dp, DarkCardBorder),
                color = DarkCardSurface
            ) {
                Button(
                    onClick = {
                        viewModel.generateProgramFromSelectedMuscles(customProgramName)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(54.dp)
                        .testTag("create_program_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed),
                    enabled = matchedExercises.isNotEmpty()
                ) {
                    Icon(
                        imageVector = Icons.Default.FitnessCenter,
                        contentDescription = null,
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Programı Oluştur (${matchedExercises.size} Egzersiz)",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
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

                    Column {
                        Text(
                            text = "Özel Program Hazırla",
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Çoklu kas grubu seçimi ve otomatik planlama",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Interactive 3D Anatomy Model with Multi-Selection Highlight
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(340.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        // 3D Viewport highlighting ALL selected muscles simultaneously
                        AnatomyViewport3D(
                            selectedMuscle = selectedProgramMuscles.firstOrNull() ?: MuscleGroup.CHEST,
                            onMuscleSelected = { clickedMuscle ->
                                viewModel.toggleProgramMuscle(clickedMuscle)
                            },
                            highlightedMuscles = selectedProgramMuscles,
                            customModelPath = customModelPath,
                            onOpenModelManager = { viewModel.navigateTo("model_manager") },
                            modifier = Modifier.fillMaxSize()
                        )

                        // Left Side Multi-Select Toggle Bar
                        Column(
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .padding(start = 8.dp)
                                .background(Color(0xD9101420), RoundedCornerShape(20.dp))
                                .border(1.dp, DarkCardBorder, RoundedCornerShape(20.dp))
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            allMuscles.forEach { muscle ->
                                val isSelected = selectedProgramMuscles.contains(muscle)
                                Surface(
                                    onClick = { viewModel.toggleProgramMuscle(muscle) },
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) CrimsonRed else Color(0xFF1B2336),
                                    border = if (isSelected) ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(Color.White)
                                    ) else null,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Text(
                                            text = muscle.displayNameTr.take(2).uppercase(),
                                            color = if (isSelected) Color.White else TextSecondary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Multi-Selection Summary & Validation Status Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(18.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(18.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceElevated)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Seçilen Kas Grupları (${selectedProgramMuscles.size})",
                                color = TextPrimary,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Çoklu Seçim",
                                color = ElectricCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Selected Muscle Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            selectedProgramMuscles.forEach { m ->
                                Surface(
                                    shape = RoundedCornerShape(10.dp),
                                    color = CrimsonRed.copy(alpha = 0.2f),
                                    border = ButtonDefaults.outlinedButtonBorder.copy(
                                        brush = androidx.compose.ui.graphics.SolidColor(CrimsonRed)
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = m.displayNameTr,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = CrimsonRed,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Matched Exercises List Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Programa Eklenecek Egzersizler",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${matchedExercises.size} Egzersiz",
                        color = ElectricCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // List of verified matched exercises
            items(matchedExercises) { ex ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .border(1.dp, DarkCardBorder, RoundedCornerShape(14.dp)),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .background(Color(0xFF222B3F), RoundedCornerShape(8.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FitnessCenter,
                                    contentDescription = null,
                                    tint = ElectricCyan,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Column {
                                Text(
                                    text = ex.nameTr,
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${ex.subregionTr} • ${ex.equipmentTr}",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Text(
                            text = "${ex.defaultSets} Set • ${ex.defaultReps}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
