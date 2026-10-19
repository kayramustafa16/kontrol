package com.example.data.model

data class DailyLifestyle(
    val waterLiters: Double = 2.1,
    val waterTargetLiters: Double = 2.5,
    val sleepHours: Double = 7.3,
    val sleepTargetHours: Double = 8.0,
    val steps: Int = 7850,
    val stepsTarget: Int = 10000,
    val moodScore: Int = 4, // 1 to 5 (Çok Kötü - Harika)
    val reminderEnabled: Boolean = true
)

data class UserProfile(
    val displayName: String = "Anabolik Atlas",
    val level: Int = 12,
    val xp: Int = 520,
    val isPro: Boolean = true,
    val age: Int = 26,
    val heightCm: Double = 182.0,
    val currentWeightKg: Double = 78.4,
    val targetWeightKg: Double = 82.0,
    val fitnessGoalTr: String = "Kas Geliştirme (Hipertrofi)",
    val experienceLevelTr: String = "Orta Seviye (2 Yıl)",
    val availableEquipmentTr: String = "Tam Donanımlı Spor Salonu",
    val custom3dModelPath: String? = null
) {
    /**
     * Dynamic calculation of daily nutrition and hydration goals
     * based on user biometrics and fitness goal
     */
    val estimatedCaloriesTarget: Int
        get() {
            val bmr = (10 * currentWeightKg) + (6.25 * heightCm) - (5 * age) + 5
            val tdee = bmr * 1.55 // Moderate-high activity
            return when {
                fitnessGoalTr.contains("Kas", ignoreCase = true) -> (tdee + 350).toInt()
                fitnessGoalTr.contains("Yağ", ignoreCase = true) -> (tdee - 400).toInt()
                else -> tdee.toInt()
            }
        }

    val estimatedProteinTargetGrams: Int
        get() = (currentWeightKg * 2.2).toInt() // 2.2g per kg for hypertrophy

    val estimatedCarbsTargetGrams: Int
        get() = ((estimatedCaloriesTarget * 0.45) / 4).toInt()

    val estimatedFatTargetGrams: Int
        get() = ((estimatedCaloriesTarget * 0.25) / 9).toInt()

    val estimatedWaterTargetLiters: Double
        get() = (currentWeightKg * 0.035).coerceAtLeast(2.5) // ~35ml per kg body weight
}
