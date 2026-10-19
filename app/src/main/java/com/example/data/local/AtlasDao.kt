package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AtlasDao {

    // Workout history
    @Query("SELECT * FROM workout_history ORDER BY dateEpochMs DESC")
    fun getAllWorkouts(): Flow<List<WorkoutSessionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutSessionEntity)

    // Meals
    @Query("SELECT * FROM meal_entries WHERE dateEpochDay = :epochDay ORDER BY id DESC")
    fun getMealsForDay(epochDay: Long): Flow<List<MealEntryEntity>>

    @Query("SELECT * FROM meal_entries ORDER BY dateEpochDay DESC")
    fun getAllMeals(): Flow<List<MealEntryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealEntryEntity)

    @Query("DELETE FROM meal_entries WHERE id = :id")
    suspend fun deleteMealById(id: String)

    // Weight history
    @Query("SELECT * FROM weight_history ORDER BY dateEpochMs ASC")
    fun getAllWeights(): Flow<List<WeightEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWeight(weight: WeightEntity)

    // Body measurements
    @Query("SELECT * FROM body_measurements ORDER BY dateEpochMs DESC")
    fun getAllMeasurements(): Flow<List<MeasurementEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: MeasurementEntity)

    // User settings
    @Query("SELECT * FROM user_settings WHERE id = 1")
    fun getUserSettings(): Flow<UserSettingsEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUserSettings(settings: UserSettingsEntity)
}
