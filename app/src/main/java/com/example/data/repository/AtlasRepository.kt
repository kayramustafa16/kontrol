package com.example.data.repository

import com.example.data.local.*
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AtlasRepository(private val dao: AtlasDao) {

    val workoutHistory: Flow<List<FinishedWorkoutSummary>> = dao.getAllWorkouts().map { list ->
        list.map {
            FinishedWorkoutSummary(
                sessionId = it.sessionId,
                titleTr = it.titleTr,
                dateEpochMs = it.dateEpochMs,
                durationSeconds = it.durationSeconds,
                totalVolumeKg = it.totalVolumeKg,
                completedSets = it.completedSets,
                exercisesCount = it.exercisesCount
            )
        }
    }

    fun getTodayMeals(epochDay: Long): Flow<List<LoggedMealEntry>> = dao.getMealsForDay(epochDay).map { list ->
        list.map {
            LoggedMealEntry(
                id = it.id,
                mealCategory = MealCategory.valueOf(it.mealCategory),
                foodNameTr = it.foodNameTr,
                servingGrams = it.servingGrams,
                calories = it.calories,
                proteinGrams = it.proteinGrams,
                carbsGrams = it.carbsGrams,
                fatGrams = it.fatGrams,
                dateEpochDay = it.dateEpochDay
            )
        }
    }

    val weightHistory: Flow<List<WeightRecord>> = dao.getAllWeights().map { list ->
        list.map { WeightRecord(id = it.id.toString(), weightKg = it.weightKg, dateEpochMs = it.dateEpochMs) }
    }

    val bodyMeasurements: Flow<List<BodyMeasurement>> = dao.getAllMeasurements().map { list ->
        list.map {
            BodyMeasurement(
                id = it.id.toString(),
                chestCm = it.chestCm,
                waistCm = it.waistCm,
                armCm = it.armCm,
                legCm = it.legCm,
                dateEpochMs = it.dateEpochMs
            )
        }
    }

    val userSettings: Flow<UserProfile> = dao.getUserSettings().map { entity ->
        if (entity != null) {
            UserProfile(
                displayName = entity.displayName,
                age = entity.age,
                heightCm = entity.heightCm,
                currentWeightKg = entity.weightKg,
                targetWeightKg = entity.targetWeightKg,
                fitnessGoalTr = entity.fitnessGoalTr,
                custom3dModelPath = entity.custom3dModelPath
            )
        } else {
            UserProfile()
        }
    }

    suspend fun saveWorkout(summary: FinishedWorkoutSummary) {
        dao.insertWorkout(
            WorkoutSessionEntity(
                sessionId = summary.sessionId,
                titleTr = summary.titleTr,
                dateEpochMs = summary.dateEpochMs,
                durationSeconds = summary.durationSeconds,
                totalVolumeKg = summary.totalVolumeKg,
                completedSets = summary.completedSets,
                exercisesCount = summary.exercisesCount
            )
        )
    }

    suspend fun addMeal(entry: LoggedMealEntry) {
        dao.insertMeal(
            MealEntryEntity(
                id = entry.id,
                mealCategory = entry.mealCategory.name,
                foodNameTr = entry.foodNameTr,
                servingGrams = entry.servingGrams,
                calories = entry.calories,
                proteinGrams = entry.proteinGrams,
                carbsGrams = entry.carbsGrams,
                fatGrams = entry.fatGrams,
                dateEpochDay = entry.dateEpochDay
            )
        )
    }

    suspend fun deleteMeal(id: String) {
        dao.deleteMealById(id)
    }

    suspend fun addWeight(weightKg: Double, epochMs: Long = System.currentTimeMillis()) {
        dao.insertWeight(WeightEntity(weightKg = weightKg, dateEpochMs = epochMs))
    }

    suspend fun addMeasurement(chestCm: Double, waistCm: Double, armCm: Double, legCm: Double) {
        dao.insertMeasurement(
            MeasurementEntity(
                chestCm = chestCm,
                waistCm = waistCm,
                armCm = armCm,
                legCm = legCm,
                dateEpochMs = System.currentTimeMillis()
            )
        )
    }

    suspend fun saveProfile(profile: UserProfile) {
        dao.saveUserSettings(
            UserSettingsEntity(
                displayName = profile.displayName,
                age = profile.age,
                heightCm = profile.heightCm,
                weightKg = profile.currentWeightKg,
                targetWeightKg = profile.targetWeightKg,
                fitnessGoalTr = profile.fitnessGoalTr,
                custom3dModelPath = profile.custom3dModelPath
            )
        )
    }

    /**
     * Seeds initial realistic baseline demo data matching the reference screen visuals
     * if the database is initially empty.
     */
    suspend fun seedInitialDataIfEmpty(currentEpochDay: Long) {
        // Initial weights matching chart in reference screen (72kg up to 78.4kg)
        val now = System.currentTimeMillis()
        val dayMs = 24L * 60 * 60 * 1000
        val baseTime = now - (28 * dayMs)
        
        dao.insertWeight(WeightEntity(weightKg = 73.0, dateEpochMs = baseTime))
        dao.insertWeight(WeightEntity(weightKg = 73.8, dateEpochMs = baseTime + (7 * dayMs)))
        dao.insertWeight(WeightEntity(weightKg = 75.2, dateEpochMs = baseTime + (14 * dayMs)))
        dao.insertWeight(WeightEntity(weightKg = 76.5, dateEpochMs = baseTime + (21 * dayMs)))
        dao.insertWeight(WeightEntity(weightKg = 78.4, dateEpochMs = now))

        // Initial measurement
        dao.insertMeasurement(
            MeasurementEntity(
                chestCm = 102.0,
                waistCm = 82.0,
                armCm = 40.0,
                legCm = 58.0,
                dateEpochMs = now
            )
        )

        // Initial default meals matching reference screen
        dao.insertMeal(
            MealEntryEntity(
                id = "init_m1",
                mealCategory = MealCategory.SABAH.name,
                foodNameTr = "Yulaf + Muz + Protein",
                servingGrams = 200.0,
                calories = 450.0,
                proteinGrams = 32.0,
                carbsGrams = 65.0,
                fatGrams = 8.0,
                dateEpochDay = currentEpochDay
            )
        )
        dao.insertMeal(
            MealEntryEntity(
                id = "init_m2",
                mealCategory = MealCategory.OGLE.name,
                foodNameTr = "Tavuk + Pirinç + Salata",
                servingGrams = 350.0,
                calories = 620.0,
                proteinGrams = 52.0,
                carbsGrams = 75.0,
                fatGrams = 12.0,
                dateEpochDay = currentEpochDay
            )
        )
        dao.insertMeal(
            MealEntryEntity(
                id = "init_m3",
                mealCategory = MealCategory.IKINDI.name,
                foodNameTr = "Yoğurt + Fındık",
                servingGrams = 180.0,
                calories = 280.0,
                proteinGrams = 16.0,
                carbsGrams = 14.0,
                fatGrams = 18.0,
                dateEpochDay = currentEpochDay
            )
        )
        dao.insertMeal(
            MealEntryEntity(
                id = "init_m4",
                mealCategory = MealCategory.AKSAM.name,
                foodNameTr = "Balık + Sebze",
                servingGrams = 300.0,
                calories = 500.0,
                proteinGrams = 42.0,
                carbsGrams = 18.0,
                fatGrams = 17.0,
                dateEpochDay = currentEpochDay
            )
        )
    }
}
