package com.example.data.model

import java.util.UUID

data class WeightRecord(
    val id: String = UUID.randomUUID().toString(),
    val weightKg: Double,
    val dateEpochMs: Long = System.currentTimeMillis()
)

data class BodyMeasurement(
    val id: String = UUID.randomUUID().toString(),
    val chestCm: Double,
    val waistCm: Double,
    val armCm: Double,
    val legCm: Double,
    val dateEpochMs: Long = System.currentTimeMillis()
)

data class PerformanceMetric(
    val muscleMassKgChange: Double = 2.1,
    val bodyFatPctChange: Double = -1.8,
    val strengthPctChange: Double = 12.0,
    val volumePctChange: Double = 28.0
)
