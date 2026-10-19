package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_history")
data class WorkoutSessionEntity(
    @PrimaryKey val sessionId: String,
    val titleTr: String,
    val dateEpochMs: Long,
    val durationSeconds: Int,
    val totalVolumeKg: Double,
    val completedSets: Int,
    val exercisesCount: Int
)

@Entity(tableName = "meal_entries")
data class MealEntryEntity(
    @PrimaryKey val id: String,
    val mealCategory: String,
    val foodNameTr: String,
    val servingGrams: Double,
    val calories: Double,
    val proteinGrams: Double,
    val carbsGrams: Double,
    val fatGrams: Double,
    val dateEpochDay: Long
)

@Entity(tableName = "weight_history")
data class WeightEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val weightKg: Double,
    val dateEpochMs: Long
)

@Entity(tableName = "body_measurements")
data class MeasurementEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val chestCm: Double,
    val waistCm: Double,
    val armCm: Double,
    val legCm: Double,
    val dateEpochMs: Long
)

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey val id: Int = 1,
    val displayName: String,
    val age: Int,
    val heightCm: Double,
    val weightKg: Double,
    val targetWeightKg: Double,
    val fitnessGoalTr: String,
    val custom3dModelPath: String?
)
