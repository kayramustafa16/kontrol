package com.example.data.model

import java.util.UUID

data class ActiveWorkoutSession(
    val sessionId: String = UUID.randomUUID().toString(),
    val programTitleTr: String = "Push Day",
    val muscleFocusTr: String = "Göğüs • Omuz • Triceps",
    val startTimeMs: Long = System.currentTimeMillis(),
    val exercises: List<ActiveExerciseState> = emptyList(),
    val isFinished: Boolean = false,
    val elapsedSeconds: Int = 0
) {
    /**
     * Calculates training volume using completed sets:
     * Training volume = sum of weight × repetitions for all completed sets.
     */
    val totalVolumeKg: Double
        get() = exercises.flatMap { it.sets }
            .filter { it.isCompleted }
            .sumOf { (it.weightKg ?: 0.0) * (it.reps ?: 0) }

    val completedSetsCount: Int
        get() = exercises.flatMap { it.sets }.count { it.isCompleted }

    val totalSetsCount: Int
        get() = exercises.flatMap { it.sets }.size
}

data class ActiveExerciseState(
    val exercise: Exercise,
    val sets: List<WorkoutSetState> = emptyList()
)

data class WorkoutSetState(
    val setNumber: Int,
    val weightKg: Double? = 60.0,
    val reps: Int? = 10,
    val isCompleted: Boolean = false,
    val restSeconds: Int = 90
)

data class FinishedWorkoutSummary(
    val sessionId: String,
    val titleTr: String,
    val dateEpochMs: Long,
    val durationSeconds: Int,
    val totalVolumeKg: Double,
    val completedSets: Int,
    val exercisesCount: Int
)
