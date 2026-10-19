package com.example.data.model

import java.util.UUID

enum class MealCategory(val id: String, val titleTr: String) {
    SABAH("breakfast", "Kahvaltı"),
    KUSLUK("snack_morning", "Kuşluk"),
    OGLE("lunch", "Öğle Yemeği"),
    IKINDI("snack_afternoon", "İkindi"),
    AKSAM("dinner", "Akşam"),
    GECE("night", "Gece / Yatmadan Önce")
}

data class FoodItem(
    val id: String = UUID.randomUUID().toString(),
    val nameTr: String,
    val caloriesPer100g: Double,
    val proteinPer100g: Double,
    val carbsPer100g: Double,
    val fatPer100g: Double,
    val defaultServingGrams: Double = 100.0,
    val servingUnitTr: String = "gram"
)

data class LoggedMealEntry(
    val id: String = UUID.randomUUID().toString(),
    val mealCategory: MealCategory,
    val foodNameTr: String,
    val servingGrams: Double,
    val calories: Double,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatGrams: Double,
    val dateEpochDay: Long = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
)

object FoodDatabase {
    fun calculateNutrient(valuePer100g: Double, grams: Double): Double {
        return (valuePer100g * grams) / 100.0
    }

    val sampleFoods = listOf(
        FoodItem("1", "Tavuk Göğsü (Izgara)", caloriesPer100g = 165.0, proteinPer100g = 31.0, carbsPer100g = 0.0, fatPer100g = 3.6, defaultServingGrams = 200.0),
        FoodItem("2", "Yulaf Ezmesi", caloriesPer100g = 370.0, proteinPer100g = 13.5, carbsPer100g = 68.0, fatPer100g = 7.0, defaultServingGrams = 80.0),
        FoodItem("3", "Yumurta (Tam, Haşlanmış)", caloriesPer100g = 155.0, proteinPer100g = 12.6, carbsPer100g = 1.1, fatPer100g = 10.6, defaultServingGrams = 100.0),
        FoodItem("4", "Pirinç (Basmati / Pişmiş)", caloriesPer100g = 130.0, proteinPer100g = 2.7, carbsPer100g = 28.0, fatPer100g = 0.3, defaultServingGrams = 150.0),
        FoodItem("5", "Muz", caloriesPer100g = 89.0, proteinPer100g = 1.1, carbsPer100g = 23.0, fatPer100g = 0.3, defaultServingGrams = 120.0),
        FoodItem("6", "Whey Protein Tozu", caloriesPer100g = 390.0, proteinPer100g = 78.0, carbsPer100g = 6.0, fatPer100g = 4.0, defaultServingGrams = 30.0),
        FoodItem("7", "Süzme Yoğurt / Lor", caloriesPer100g = 97.0, proteinPer100g = 10.0, carbsPer100g = 3.5, fatPer100g = 5.0, defaultServingGrams = 150.0),
        FoodItem("8", "Çiğ Badem / Fındık", caloriesPer100g = 579.0, proteinPer100g = 21.0, carbsPer100g = 22.0, fatPer100g = 49.0, defaultServingGrams = 30.0),
        FoodItem("9", "Somon Balığı (Fırın)", caloriesPer100g = 208.0, proteinPer100g = 22.0, carbsPer100g = 0.0, fatPer100g = 13.0, defaultServingGrams = 200.0),
        FoodItem("10", "Zeytinyağı", caloriesPer100g = 884.0, proteinPer100g = 0.0, carbsPer100g = 0.0, fatPer100g = 100.0, defaultServingGrams = 15.0)
    )
}
