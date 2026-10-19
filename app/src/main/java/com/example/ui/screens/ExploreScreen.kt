package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Exercise
import com.example.data.model.ExerciseDatabase
import com.example.data.model.MuscleGroup
import com.example.ui.theme.*
import com.example.ui.viewmodel.AtlasViewModel

@Composable
fun ExploreScreen(
    viewModel: AtlasViewModel,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedMuscleFilter by remember { mutableStateOf<MuscleGroup?>(null) }
    var activeSubTab by remember { mutableStateOf("Tüm Egzersizler") }
    val subTabs = listOf("Tüm Egzersizler", "Kas Grupları", "Ekipman")

    val muscleChips = listOf(
        null to "Tümü",
        MuscleGroup.CHEST to "Göğüs",
        MuscleGroup.BACK to "Sırt",
        MuscleGroup.SHOULDERS to "Omuz",
        MuscleGroup.BICEPS to "Kol",
        MuscleGroup.QUADS to "Bacak"
    )

    val filteredExercises = remember(searchQuery, selectedMuscleFilter) {
        ExerciseDatabase.sampleExercises.filter { ex ->
            val matchesQuery = searchQuery.isBlank() ||
                    ex.nameTr.contains(searchQuery, ignoreCase = true) ||
                    ex.equipmentTr.contains(searchQuery, ignoreCase = true)
            val matchesMuscle = selectedMuscleFilter == null ||
                    ex.primaryMuscle == selectedMuscleFilter ||
                    ex.secondaryMuscles.contains(selectedMuscleFilter)
            matchesQuery && matchesMuscle
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("explore_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = { viewModel.navigateTo("home") },
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
                text = "Keşfet",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Sub Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            subTabs.forEach { tab ->
                val isSelected = activeSubTab == tab
                Surface(
                    onClick = { activeSubTab = tab },
                    shape = RoundedCornerShape(14.dp),
                    color = if (isSelected) ElectricCyan else DarkCardSurface,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = tab,
                        color = if (isSelected) Color.Black else TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier
                            .padding(vertical = 7.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Search Input Field
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Egzersiz ara...", color = TextMuted, fontSize = 13.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextMuted) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = DarkCardSurface,
                unfocusedContainerColor = DarkCardSurface,
                focusedBorderColor = ElectricCyan,
                unfocusedBorderColor = DarkCardBorder,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Muscle Filter Horizontal Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            muscleChips.forEach { (muscle, label) ->
                val isSelected = selectedMuscleFilter == muscle
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedMuscleFilter = muscle },
                    label = { Text(label, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = ElectricCyan,
                        selectedLabelColor = Color.Black,
                        containerColor = DarkCardSurface,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Exercise Grid matching reference image cards
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(bottom = 96.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredExercises) { exercise ->
                ExerciseGridItem(
                    exercise = exercise,
                    onClick = { viewModel.openExerciseDetail(exercise) }
                )
            }
        }
    }
}

@Composable
private fun ExerciseGridItem(
    exercise: Exercise,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(170.dp)
            .clip(RoundedCornerShape(18.dp))
            .border(1.dp, DarkCardBorder, RoundedCornerShape(18.dp))
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Visual demonstration placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(Color(0xFF232D42), Color(0xFF131722))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FitnessCenter,
                    contentDescription = null,
                    tint = ElectricCyan.copy(alpha = 0.35f),
                    modifier = Modifier.size(46.dp)
                )
            }

            // Bottom Label Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .background(Color(0xE60D111A))
                    .padding(10.dp)
            ) {
                Text(
                    text = exercise.nameTr,
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = "${exercise.equipmentTr} • ${exercise.primaryMuscle.displayNameTr.split(" ").first()}",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    maxLines = 1
                )
            }
        }
    }
}
