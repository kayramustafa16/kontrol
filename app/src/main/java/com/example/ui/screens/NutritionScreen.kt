package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.*
import com.example.ui.components.CircularProgressRing
import com.example.ui.components.MacroProgressBar
import com.example.ui.theme.*
import com.example.ui.viewmodel.AtlasViewModel

@Composable
fun NutritionScreen(
    viewModel: AtlasViewModel,
    modifier: Modifier = Modifier
) {
    val todayMeals by viewModel.todayMeals.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()

    var activeTopTab by remember { mutableStateOf("Bugün") }
    val topTabs = listOf("Bugün", "Öğünler", "Tarifler", "Yiyecekler")

    var showAddFoodDialog by remember { mutableStateOf(false) }

    val totalCalories = todayMeals.sumOf { it.calories }
    val totalProtein = todayMeals.sumOf { it.proteinGrams }
    val totalCarbs = todayMeals.sumOf { it.carbsGrams }
    val totalFat = todayMeals.sumOf { it.fatGrams }

    val targetCalories = userProfile.estimatedCaloriesTarget.toDouble()
    val targetProtein = userProfile.estimatedProteinTargetGrams.toDouble()
    val targetCarbs = userProfile.estimatedCarbsTargetGrams.toDouble()
    val targetFat = userProfile.estimatedFatTargetGrams.toDouble()

    val calorieProgress = (totalCalories / targetCalories).toFloat()

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
            .statusBarsPadding()
            .testTag("nutrition_screen"),
        bottomBar = {
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .border(1.dp, DarkCardBorder),
                color = DarkCardSurface
            ) {
                Button(
                    onClick = { showAddFoodDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .height(52.dp)
                        .testTag("add_food_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = Color.Black
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Yiyecek Ekle",
                        color = Color.Black,
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
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Top Bar
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { /* Calendar picker */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(DarkCardSurface, CircleShape)
                            .border(1.dp, DarkCardBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filtre",
                            tint = TextPrimary
                        )
                    }

                    Text(
                        text = "Beslenme",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    IconButton(
                        onClick = { /* Favorites */ },
                        modifier = Modifier
                            .size(40.dp)
                            .background(DarkCardSurface, CircleShape)
                            .border(1.dp, DarkCardBorder, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favoriler",
                            tint = TextPrimary
                        )
                    }
                }
            }

            // Top Category Tabs
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(DarkCardSurface, RoundedCornerShape(20.dp))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    topTabs.forEach { tab ->
                        val isSelected = activeTopTab == tab
                        Surface(
                            onClick = { activeTopTab = tab },
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

            // Circular Calorie Target Indicator
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
                        CircularProgressRing(
                            progress = calorieProgress,
                            currentValueStr = "${totalCalories.toInt()}",
                            targetValueStr = "${targetCalories.toInt()}",
                            unitStr = "kcal",
                            modifier = Modifier.size(200.dp)
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        // Macro Bars
                        MacroProgressBar(
                            labelTr = "Protein",
                            currentGrams = totalProtein,
                            targetGrams = targetProtein,
                            color = ElectricCyan
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MacroProgressBar(
                            labelTr = "Karbonhidrat",
                            currentGrams = totalCarbs,
                            targetGrams = targetCarbs,
                            color = BrightBlue
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        MacroProgressBar(
                            labelTr = "Yağ",
                            currentGrams = totalFat,
                            targetGrams = targetFat,
                            color = NeonOrange
                        )
                    }
                }
            }

            // Meal Entries Section Header
            item {
                Text(
                    text = "Günün Öğünleri",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            // Meal Entries List
            if (todayMeals.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                    ) {
                        Text(
                            text = "Henüz kaydedilmiş bir öğün bulunmuyor. Aşağıdaki 'Yiyecek Ekle' butonuna basarak ilk öğününüzü kaydedin.",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            } else {
                items(todayMeals) { meal ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .border(1.dp, DarkCardBorder, RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(Color(0xFF222B3F), RoundedCornerShape(12.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Restaurant,
                                        contentDescription = null,
                                        tint = ElectricCyan,
                                        modifier = Modifier.size(22.dp)
                                    )
                                }

                                Column {
                                    Text(
                                        text = meal.mealCategory.titleTr,
                                        color = TextPrimary,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = meal.foodNameTr,
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${meal.calories.toInt()} kcal",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                IconButton(
                                    onClick = { viewModel.deleteMealEntry(meal.id) },
                                    modifier = Modifier.size(32.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Close,
                                        contentDescription = "Sil",
                                        tint = TextMuted,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add Food Dialog with Per-100g Math Calculator
    if (showAddFoodDialog) {
        var searchQuery by remember { mutableStateOf("") }
        var selectedFood by remember { mutableStateOf(FoodDatabase.sampleFoods.first()) }
        var selectedCategory by remember { mutableStateOf(MealCategory.SABAH) }
        var inputGramsStr by remember { mutableStateOf("150") }

        val filteredFoods = remember(searchQuery) {
            if (searchQuery.isBlank()) FoodDatabase.sampleFoods
            else FoodDatabase.sampleFoods.filter { it.nameTr.contains(searchQuery, ignoreCase = true) }
        }

        val enteredGrams = inputGramsStr.toDoubleOrNull() ?: 100.0
        val computedCals = FoodDatabase.calculateNutrient(selectedFood.caloriesPer100g, enteredGrams)
        val computedProtein = FoodDatabase.calculateNutrient(selectedFood.proteinPer100g, enteredGrams)
        val computedCarbs = FoodDatabase.calculateNutrient(selectedFood.carbsPer100g, enteredGrams)
        val computedFat = FoodDatabase.calculateNutrient(selectedFood.fatPer100g, enteredGrams)

        AlertDialog(
            onDismissRequest = { showAddFoodDialog = false },
            title = { Text("Yiyecek Ekle", fontWeight = FontWeight.Bold) },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Search bar
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        label = { Text("Yiyecek ara...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) }
                    )

                    // Food selection chips
                    Row(modifier = Modifier.fillMaxWidth()) {
                        Text("Seçili:", color = TextSecondary, fontSize = 12.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(selectedFood.nameTr, color = ElectricCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Meal category dropdown / chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        listOf(MealCategory.SABAH, MealCategory.OGLE, MealCategory.IKINDI, MealCategory.AKSAM).forEach { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat.titleTr.take(5), fontSize = 10.sp) }
                            )
                        }
                    }

                    // Gram input
                    OutlinedTextField(
                        value = inputGramsStr,
                        onValueChange = { inputGramsStr = it },
                        label = { Text("Miktar (gram)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    // Real-Time Nutrition Summary Preview
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = DarkCardSurfaceElevated)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text("Hesaplanan Besin Değerleri:", fontSize = 11.sp, color = TextSecondary)
                            Text(
                                "Kalori: ${computedCals.toInt()} kcal • Protein: ${computedProtein.toInt()}g • Karb: ${computedCarbs.toInt()}g • Yağ: ${computedFat.toInt()}g",
                                fontSize = 12.sp,
                                color = ElectricCyan,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.logFoodItem(selectedFood, enteredGrams, selectedCategory)
                        showAddFoodDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonGreen)
                ) {
                    Text("Öğüne Ekle", color = Color.Black)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddFoodDialog = false }) {
                    Text("İptal", color = TextSecondary)
                }
            },
            containerColor = DarkCardSurface
        )
    }
}
