package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AtlasViewModel

@Composable
fun WorkoutSessionScreen(
    viewModel: AtlasViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.navigateBack()
    }

    val activeSession by viewModel.activeSession.collectAsState()
    val timerSeconds by viewModel.workoutTimerSeconds.collectAsState()
    val lastFinishedWorkout by viewModel.lastFinishedWorkout.collectAsState()

    var showAbandonDialog by remember { mutableStateOf(false) }

    val formattedTime = remember(timerSeconds) {
        val minutes = timerSeconds / 60
        val seconds = timerSeconds % 60
        String.format("%02d:%02d", minutes, seconds)
    }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("workout_session_screen"),
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(1.dp, DarkCardBorder),
                color = DarkCardSurface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = { showAbandonDialog = true },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = androidx.compose.ui.graphics.SolidColor(DarkCardBorder))
                    ) {
                        Text("Vazgeç", color = TextSecondary)
                    }

                    Button(
                        onClick = { viewModel.finishWorkout() },
                        modifier = Modifier
                            .weight(2f)
                            .height(50.dp)
                            .testTag("finish_workout_button"),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Antrenmanı Bitir",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header: Back arrow, Title, Muscle focus, Timer HUD
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
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
                                text = activeSession?.programTitleTr ?: "Push Day",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = activeSession?.muscleFocusTr ?: "Göğüs • Omuz • Triceps",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Live Timer Badge
                    Row(
                        modifier = Modifier
                            .background(Color(0x3300E5FF), RoundedCornerShape(18.dp))
                            .border(1.dp, ElectricCyan.copy(alpha = 0.5f), RoundedCornerShape(18.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = formattedTime,
                            color = ElectricCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Real-Time Total Volume Banner
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF1B2338)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Toplam Antrenman Hacmi",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${activeSession?.totalVolumeKg?.toInt() ?: 0} kg",
                                color = ElectricCyan,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }

                        Text(
                            text = "${activeSession?.completedSetsCount ?: 0} / ${activeSession?.totalSetsCount ?: 0} Set",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Exercises and Set Tables
            activeSession?.exercises?.let { exercisesList ->
                itemsIndexed(exercisesList) { exIndex, exState ->
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
                            // Exercise Header
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
                                            .size(40.dp)
                                            .background(Color(0xFF232D42), RoundedCornerShape(10.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.FitnessCenter,
                                            contentDescription = null,
                                            tint = ElectricCyan,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = exState.exercise.nameTr,
                                            color = TextPrimary,
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${exState.sets.size} Set • ${exState.exercise.defaultReps} Tekrar",
                                            color = TextSecondary,
                                            fontSize = 12.sp
                                        )
                                    }
                                }

                                IconButton(onClick = { viewModel.addSetToExercise(exIndex) }) {
                                    Icon(
                                        imageVector = Icons.Default.AddCircleOutline,
                                        contentDescription = "Set Ekle",
                                        tint = ElectricCyan
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            // Set Table Headers
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 4.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("Set", color = TextMuted, fontSize = 12.sp, modifier = Modifier.width(36.dp))
                                Text("Ağırlık (kg)", color = TextMuted, fontSize = 12.sp, modifier = Modifier.width(80.dp))
                                Text("Tekrar", color = TextMuted, fontSize = 12.sp, modifier = Modifier.width(60.dp))
                                Text("Dinlenme", color = TextMuted, fontSize = 12.sp, modifier = Modifier.width(80.dp))
                            }

                            Divider(color = DarkCardBorder, thickness = 1.dp)

                            // Set Rows
                            exState.sets.forEachIndexed { setIndex, setItem ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 6.dp)
                                        .background(
                                            if (setItem.isCompleted) Color(0x1A00E676) else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 4.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    // Set Number
                                    Text(
                                        text = "${setItem.setNumber}",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.width(36.dp)
                                    )

                                    // Weight Input
                                    OutlinedTextField(
                                        value = setItem.weightKg?.let { if (it % 1.0 == 0.0) it.toInt().toString() else it.toString() } ?: "",
                                        onValueChange = { newVal ->
                                            val w = newVal.toDoubleOrNull()
                                            viewModel.updateSetValues(exIndex, setIndex, w, setItem.reps)
                                        },
                                        modifier = Modifier
                                            .width(80.dp)
                                            .height(48.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = ElectricCyan,
                                            unfocusedBorderColor = DarkCardBorder
                                        ),
                                        singleLine = true
                                    )

                                    // Reps Input
                                    OutlinedTextField(
                                        value = setItem.reps?.toString() ?: "",
                                        onValueChange = { newVal ->
                                            val r = newVal.toIntOrNull()
                                            viewModel.updateSetValues(exIndex, setIndex, setItem.weightKg, r)
                                        },
                                        modifier = Modifier
                                            .width(60.dp)
                                            .height(48.dp),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = TextPrimary,
                                            unfocusedTextColor = TextPrimary,
                                            focusedBorderColor = ElectricCyan,
                                            unfocusedBorderColor = DarkCardBorder
                                        ),
                                        singleLine = true
                                    )

                                    // Complete Checkbox or Timer
                                    IconButton(
                                        onClick = { viewModel.toggleSetCompleted(exIndex, setIndex) },
                                        modifier = Modifier
                                            .size(38.dp)
                                            .background(
                                                if (setItem.isCompleted) NeonGreen.copy(alpha = 0.2f) else DarkCardSurfaceElevated,
                                                CircleShape
                                            )
                                    ) {
                                        Icon(
                                            imageVector = if (setItem.isCompleted) Icons.Default.Check else Icons.Default.Schedule,
                                            contentDescription = if (setItem.isCompleted) "Tamamlandı" else "Bekliyor",
                                            tint = if (setItem.isCompleted) NeonGreen else ElectricCyan,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Confirmation dialog before abandoning workout
    if (showAbandonDialog) {
        AlertDialog(
            onDismissRequest = { showAbandonDialog = false },
            title = { Text("Antrenmandan Çıkılsın mı?") },
            text = { Text("Mevcut antrenman seansınız sonlandırılacak ve kaydedilmemiş setler silinecektir.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showAbandonDialog = false
                        viewModel.navigateBack()
                    }
                ) {
                    Text("Evet, Çık", color = CrimsonRed)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAbandonDialog = false }) {
                    Text("Devam Et", color = TextPrimary)
                }
            },
            containerColor = DarkCardSurface
        )
    }

    // Finished Workout Summary Dialog
    lastFinishedWorkout?.let { summary ->
        AlertDialog(
            onDismissRequest = { viewModel.dismissFinishedWorkoutDialog() },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.EmojiEvents, contentDescription = null, tint = ElectricCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Antrenman Tamamlandı!", fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Tebrikler! ${summary.titleTr} başarıyla kaydedildi.", color = TextPrimary)
                    Divider(color = DarkCardBorder)
                    Text(text = "• Toplam Hacim: ${summary.totalVolumeKg.toInt()} kg", color = ElectricCyan, fontWeight = FontWeight.Bold)
                    Text(text = "• Tamamlanan Set: ${summary.completedSets} set", color = TextSecondary)
                    Text(text = "• Geçen Süre: ${summary.durationSeconds / 60} dakika ${summary.durationSeconds % 60} saniye", color = TextSecondary)
                }
            },
            confirmButton = {
                Button(
                    onClick = { viewModel.dismissFinishedWorkoutDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = CrimsonRed)
                ) {
                    Text("Harika!")
                }
            },
            containerColor = DarkCardSurface
        )
    }
}
