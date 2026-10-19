package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.AtlasViewModel

@Composable
fun ProgressScreen(
    viewModel: AtlasViewModel,
    modifier: Modifier = Modifier
) {
    val weightHistory by viewModel.weightHistory.collectAsState()
    val bodyMeasurements by viewModel.bodyMeasurements.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var activeTab by remember { mutableStateOf("Ağırlık") }
    val tabs = listOf("Ağırlık", "Vücut Ölçüleri", "Performans")

    var selectedPeriod by remember { mutableStateOf("1A") }
    val periods = listOf("1H", "1A", "3A", "1Y", "Tümü")

    var showAddWeightDialog by remember { mutableStateOf(false) }
    var showAddMeasurementDialog by remember { mutableStateOf(false) }

    val latestWeight = weightHistory.lastOrNull()?.weightKg ?: userProfile.currentWeightKg
    val firstWeight = weightHistory.firstOrNull()?.weightKg ?: latestWeight
    val weightDiff = latestWeight - firstWeight

    val latestMeasurement = bodyMeasurements.firstOrNull()

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("progress_screen"),
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
                    text = "Gelişim / İstatistikler",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = { showAddWeightDialog = true },
                    modifier = Modifier
                        .size(40.dp)
                        .background(DarkCardSurface, CircleShape)
                        .border(1.dp, DarkCardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Ölçüm Ekle",
                        tint = ElectricCyan
                    )
                }
            }
        }

        // Top Navigation Tabs (Ağırlık, Vücut Ölçüleri, Performans)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .background(DarkCardSurface, RoundedCornerShape(20.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                tabs.forEach { tab ->
                    val isSelected = activeTab == tab
                    Surface(
                        onClick = { activeTab = tab },
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) ElectricCyan else Color.Transparent,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = tab,
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        // Time Filter Chips (1H, 1A, 3A, 1Y, Tümü)
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                periods.forEach { period ->
                    val isSelected = selectedPeriod == period
                    Surface(
                        onClick = { selectedPeriod = period },
                        shape = RoundedCornerShape(14.dp),
                        color = if (isSelected) ElectricCyan else DarkCardSurface,
                        border = if (isSelected) null else ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(DarkCardBorder)
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = period,
                            color = if (isSelected) Color.Black else TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .padding(vertical = 6.dp)
                                .wrapContentWidth(Alignment.CenterHorizontally)
                        )
                    }
                }
            }
        }

        // Weight Chart Card matching reference image
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp)),
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
                        Column {
                            Text(
                                text = "Vücut Ağırlığı (kg)",
                                color = TextSecondary,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${String.format("%.1f", latestWeight)} kg",
                                color = TextPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        // Delta Pill
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF142E35),
                            border = ButtonDefaults.outlinedButtonBorder.copy(
                                brush = androidx.compose.ui.graphics.SolidColor(ElectricCyan.copy(alpha = 0.5f))
                            )
                        ) {
                            Text(
                                text = if (weightDiff >= 0) "+${String.format("%.1f", weightDiff)} kg" else "${String.format("%.1f", weightDiff)} kg",
                                color = ElectricCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Custom Canvas Weight Bezier Curve Chart
                    val weights = if (weightHistory.isNotEmpty()) weightHistory.map { it.weightKg } else listOf(73.0, 74.0, 75.5, 76.8, 78.4)
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val minVal = (weights.minOrNull() ?: 70.0) - 2.0
                            val maxVal = (weights.maxOrNull() ?: 80.0) + 2.0
                            val range = (maxVal - minVal).coerceAtLeast(1.0)

                            val stepX = size.width / (weights.size - 1).coerceAtLeast(1)
                            val points = weights.mapIndexed { idx, w ->
                                val x = idx * stepX
                                val y = size.height - (((w - minVal) / range) * size.height).toFloat()
                                Offset(x, y)
                            }

                            // Fill gradient under curve
                            val fillPath = Path().apply {
                                moveTo(0f, size.height)
                                points.forEach { lineTo(it.x, it.y) }
                                lineTo(size.width, size.height)
                                close()
                            }
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(ElectricCyan.copy(alpha = 0.3f), Color.Transparent)
                                )
                            )

                            // Stroke line
                            val strokePath = Path().apply {
                                moveTo(points.first().x, points.first().y)
                                for (i in 1 until points.size) {
                                    val prev = points[i - 1]
                                    val curr = points[i]
                                    val midX = (prev.x + curr.x) / 2f
                                    cubicTo(midX, prev.y, midX, curr.y, curr.x, curr.y)
                                }
                            }
                            drawPath(
                                path = strokePath,
                                color = ElectricCyan,
                                style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
                            )

                            // Highlight dots
                            points.forEach { pt ->
                                drawCircle(color = ElectricCyan, radius = 5.dp.toPx(), center = pt)
                                drawCircle(color = DarkBackground, radius = 2.dp.toPx(), center = pt)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Date labels row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("1 Eki", "8 Eki", "15 Eki", "22 Eki", "29 Eki").forEach { dateLabel ->
                            Text(text = dateLabel, color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        // 4 Performance Metrics Grid
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Kas Kütlesi
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(NeonGreen.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.FitnessCenter, contentDescription = null, tint = NeonGreen, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Kas Kütlesi", color = TextSecondary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("+2.1 kg", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Yağ Oranı
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(CrimsonRed.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.LocalFireDepartment, contentDescription = null, tint = CrimsonRed, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Yağ Oranı", color = TextSecondary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("-1.8 %", color = CrimsonRed, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Güç Artışı
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(BrightBlue.copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Bolt, contentDescription = null, tint = BrightBlue, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Güç Artışı", color = TextSecondary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("+12 %", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Antrenman Hacmi
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = DarkCardSurface),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(Color(0xFFA78BFA).copy(alpha = 0.15f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Hacim", color = TextSecondary, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("+28 %", color = ElectricCyan, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Vücut Ölçüleri Card Section
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .clip(RoundedCornerShape(22.dp))
                    .border(1.dp, DarkCardBorder, RoundedCornerShape(22.dp)),
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
                            text = "Vücut Ölçüleri",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showAddMeasurementDialog = true }) {
                            Icon(Icons.Default.Add, contentDescription = "Ölçüm Ekle", tint = ElectricCyan)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val chest = latestMeasurement?.chestCm ?: 102.0
                    val waist = latestMeasurement?.waistCm ?: 82.0
                    val arm = latestMeasurement?.armCm ?: 40.0
                    val leg = latestMeasurement?.legCm ?: 58.0

                    MeasurementRowItem("Göğüs", "${chest.toInt()} cm", "+2")
                    Divider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                    MeasurementRowItem("Bel", "${waist.toInt()} cm", "-1")
                    Divider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                    MeasurementRowItem("Kol", "${arm.toInt()} cm", "+1")
                    Divider(color = DarkCardBorder, thickness = 0.5.dp, modifier = Modifier.padding(vertical = 8.dp))
                    MeasurementRowItem("Bacak", "${leg.toInt()} cm", "+1")
                }
            }
        }
    }

    // Add Weight Dialog
    if (showAddWeightDialog) {
        var inputWeight by remember { mutableStateOf(latestWeight.toString()) }
        AlertDialog(
            onDismissRequest = { showAddWeightDialog = false },
            title = { Text("Ağırlık Kaydet", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = inputWeight,
                    onValueChange = { inputWeight = it },
                    label = { Text("Kilo (kg)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        val w = inputWeight.toDoubleOrNull()
                        if (w != null && w > 30.0) {
                            viewModel.logWeight(w)
                        }
                        showAddWeightDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                ) {
                    Text("Kaydet", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddWeightDialog = false }) {
                    Text("İptal", color = TextSecondary)
                }
            },
            containerColor = DarkCardSurface
        )
    }

    // Add Measurement Dialog
    if (showAddMeasurementDialog) {
        var chestStr by remember { mutableStateOf("102") }
        var waistStr by remember { mutableStateOf("82") }
        var armStr by remember { mutableStateOf("40") }
        var legStr by remember { mutableStateOf("58") }

        AlertDialog(
            onDismissRequest = { showAddMeasurementDialog = false },
            title = { Text("Vücut Ölçüsü Ekle", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(value = chestStr, onValueChange = { chestStr = it }, label = { Text("Göğüs (cm)") })
                    OutlinedTextField(value = waistStr, onValueChange = { waistStr = it }, label = { Text("Bel (cm)") })
                    OutlinedTextField(value = armStr, onValueChange = { armStr = it }, label = { Text("Kol (cm)") })
                    OutlinedTextField(value = legStr, onValueChange = { legStr = it }, label = { Text("Bacak (cm)") })
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val c = chestStr.toDoubleOrNull() ?: 100.0
                        val w = waistStr.toDoubleOrNull() ?: 80.0
                        val a = armStr.toDoubleOrNull() ?: 38.0
                        val l = legStr.toDoubleOrNull() ?: 56.0
                        viewModel.logMeasurements(c, w, a, l)
                        showAddMeasurementDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan)
                ) {
                    Text("Kaydet", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddMeasurementDialog = false }) {
                    Text("İptal", color = TextSecondary)
                }
            },
            containerColor = DarkCardSurface
        )
    }
}

@Composable
private fun MeasurementRowItem(label: String, valueStr: String, deltaStr: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextPrimary, fontSize = 14.sp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text = valueStr, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = deltaStr,
                color = if (deltaStr.startsWith("+")) NeonGreen else CrimsonRed,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
