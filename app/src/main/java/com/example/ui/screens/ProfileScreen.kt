package com.example.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AtlasViewModel

@Composable
fun ProfileScreen(
    viewModel: AtlasViewModel,
    modifier: Modifier = Modifier
) {
    val userProfile by viewModel.userProfile.collectAsState()
    var showEditProfileDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("profile_screen"),
        contentPadding = PaddingValues(bottom = 96.dp)
    ) {
        // Top App Bar
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo("home") },
                    modifier = Modifier
                        .size(40.dp)
                        .background(DarkCardSurface, CircleShape)
                        .border(1.dp, DarkCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "Menü",
                        tint = TextPrimary
                    )
                }

                Text(
                    text = "Profil",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { showEditProfileDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(DarkCardSurface, CircleShape)
                        .border(1.dp, DarkCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Ayarlar",
                        tint = TextPrimary
                    )
                }
            }
        }

        // Profile Avatar & Level Header Card matching reference image
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        // User Avatar
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF243048))
                                .border(2.dp, ElectricCyan, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(36.dp)
                            )
                        }

                        Column {
                            Text(
                                text = userProfile.displayName,
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Seviye ${userProfile.level} • ${userProfile.xp} XP",
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }
                    }

                    // Golden PRO Badge
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFD97706)
                    ) {
                        Text(
                            text = "PRO",
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        // Profile Menu Items List
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp)),
                colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
            ) {
                Column(modifier = Modifier.padding(vertical = 6.dp)) {
                    ProfileMenuRowItem(
                        icon = Icons.Default.Flag,
                        titleTr = "Hedeflerim",
                        subtitleTr = userProfile.fitnessGoalTr,
                        onClick = { showEditProfileDialog = true }
                    )
                    Divider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuRowItem(
                        icon = Icons.Default.Straighten,
                        titleTr = "Vücut Ölçülerim",
                        subtitleTr = "${userProfile.currentWeightKg} kg • ${userProfile.heightCm.toInt()} cm",
                        onClick = { viewModel.navigateTo("progress") }
                    )
                    Divider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuRowItem(
                        icon = Icons.Default.PersonOutline,
                        titleTr = "Kişisel Bilgiler",
                        subtitleTr = "${userProfile.age} Yaşında • ${userProfile.experienceLevelTr}",
                        onClick = { showEditProfileDialog = true }
                    )
                    Divider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuRowItem(
                        icon = Icons.Default.ViewInAr,
                        titleTr = "3D Model Yöneticisi (FBX / GLB)",
                        subtitleTr = "Tripo 3D Modelini İçe Aktar & Kasları Eşle",
                        highlight = true,
                        onClick = { viewModel.navigateTo("model_manager") }
                    )
                    Divider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuRowItem(
                        icon = Icons.Default.WorkspacePremium,
                        titleTr = "Premium Özellikler",
                        onClick = {}
                    )
                    Divider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuRowItem(
                        icon = Icons.Default.Settings,
                        titleTr = "Ayarlar",
                        onClick = {}
                    )
                    Divider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuRowItem(
                        icon = Icons.Default.CloudSync,
                        titleTr = "Veri Yedekleme",
                        onClick = {}
                    )
                    Divider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(horizontal = 16.dp))
                    ProfileMenuRowItem(
                        icon = Icons.Default.HelpOutline,
                        titleTr = "Yardım ve Destek",
                        onClick = {}
                    )
                }
            }
        }
    }

    // Edit Profile Modal Dialog
    if (showEditProfileDialog) {
        var nameInput by remember { mutableStateOf(userProfile.displayName) }
        var ageInput by remember { mutableStateOf(userProfile.age.toString()) }
        var heightInput by remember { mutableStateOf(userProfile.heightCm.toInt().toString()) }
        var weightInput by remember { mutableStateOf(userProfile.currentWeightKg.toString()) }
        var targetWeightInput by remember { mutableStateOf(userProfile.targetWeightKg.toString()) }

        AlertDialog(
            onDismissRequest = { showEditProfileDialog = false },
            title = { Text("Kişisel Bilgileri Güncelle", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = nameInput, onValueChange = { nameInput = it }, label = { Text("İsim") })
                    OutlinedTextField(value = ageInput, onValueChange = { ageInput = it }, label = { Text("Yaş") })
                    OutlinedTextField(value = heightInput, onValueChange = { heightInput = it }, label = { Text("Boy (cm)") })
                    OutlinedTextField(value = weightInput, onValueChange = { weightInput = it }, label = { Text("Mevcut Kilo (kg)") })
                    OutlinedTextField(value = targetWeightInput, onValueChange = { targetWeightInput = it }, label = { Text("Hedef Kilo (kg)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val updated = userProfile.copy(
                            displayName = nameInput,
                            age = ageInput.toIntOrNull() ?: userProfile.age,
                            heightCm = heightInput.toDoubleOrNull() ?: userProfile.heightCm,
                            currentWeightKg = weightInput.toDoubleOrNull() ?: userProfile.currentWeightKg,
                            targetWeightKg = targetWeightInput.toDoubleOrNull() ?: userProfile.targetWeightKg
                        )
                        viewModel.updateProfile(updated)
                        showEditProfileDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                ) {
                    Text("Kaydet", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditProfileDialog = false }) {
                    Text("İptal", color = TextSecondary)
                }
            },
            containerColor = DarkCardSurface
        )
    }
}

@Composable
private fun ProfileMenuRowItem(
    icon: ImageVector,
    titleTr: String,
    subtitleTr: String? = null,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            modifier = Modifier.weight(1f)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (highlight) ElectricCyan else TextSecondary,
                modifier = Modifier.size(22.dp)
            )
            Column {
                Text(
                    text = titleTr,
                    color = if (highlight) ElectricCyan else TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium
                )
                subtitleTr?.let {
                    Text(
                        text = it,
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = TextMuted,
            modifier = Modifier.size(18.dp)
        )
    }
}
