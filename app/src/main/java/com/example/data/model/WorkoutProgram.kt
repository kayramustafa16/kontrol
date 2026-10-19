package com.example.data.model

data class WorkoutProgram(
    val id: String,
    val titleTr: String,
    val subtitleTr: String,
    val durationWeeks: Int,
    val levelTr: String, // Başlangıç, Orta, İleri, Tüm Seviyeler
    val targetGoalTr: String, // Kas Gelişimi, Yağ Yakımı, Güç
    val descriptionTr: String,
    val daysPerWeek: Int,
    val routines: List<ProgramRoutine>
)

data class ProgramRoutine(
    val id: String,
    val titleTr: String,
    val muscleFocusTr: String,
    val exercises: List<Exercise>
)

object ProgramDatabase {
    val samplePrograms = listOf(
        WorkoutProgram(
            id = "hypertrophy",
            titleTr = "Kas Geliştirme",
            subtitleTr = "Hacim ve Güç",
            durationWeeks = 12,
            levelTr = "Orta Seviye",
            targetGoalTr = "Kas Gelişimi",
            descriptionTr = "Maksimum hipertrofi ve kas dolgunluğu için tasarlanmış progresif aşırı yükleme temelli antrenman protokolü.",
            daysPerWeek = 4,
            routines = listOf(
                ProgramRoutine(
                    id = "push_day",
                    titleTr = "Push Day (İtiş Günü)",
                    muscleFocusTr = "Göğüs • Omuz • Triceps",
                    exercises = listOf(
                        ExerciseDatabase.sampleExercises[0], // Bench Press
                        ExerciseDatabase.sampleExercises[1], // Incline Dumbbell Press
                        ExerciseDatabase.sampleExercises[2], // Cable Fly
                        ExerciseDatabase.sampleExercises[3], // Overhead Press
                        ExerciseDatabase.sampleExercises[7]  // Triceps Pushdown
                    )
                ),
                ProgramRoutine(
                    id = "pull_day",
                    titleTr = "Pull Day (Çekiş Günü)",
                    muscleFocusTr = "Sırt • Arka Omuz • Biceps",
                    exercises = listOf(
                        ExerciseDatabase.sampleExercises[4], // Lat Pulldown
                        ExerciseDatabase.sampleExercises[5], // Seated Row
                        ExerciseDatabase.sampleExercises[6]  // Barbell Curl
                    )
                ),
                ProgramRoutine(
                    id = "leg_day",
                    titleTr = "Leg Day (Bacak Günü)",
                    muscleFocusTr = "Quadriceps • Hamstrings • Kalf",
                    exercises = listOf(
                        ExerciseDatabase.sampleExercises[8], // Squat
                        ExerciseDatabase.sampleExercises[9]  // Leg Press
                    )
                )
            )
        ),
        WorkoutProgram(
            id = "fat_loss",
            titleTr = "Yağ Yakma",
            subtitleTr = "Fit ve Sıkı Görünüm",
            durationWeeks = 8,
            levelTr = "Tüm Seviyeler",
            targetGoalTr = "Yağ Kaybı",
            descriptionTr = "Yüksek yoğunluklu setler ve kısa dinlenme aralıkları ile metabolik hızı artıran yağ yakım programı.",
            daysPerWeek = 4,
            routines = listOf(
                ProgramRoutine(
                    id = "upper_circuit",
                    titleTr = "Üst Vücut Metabolik",
                    muscleFocusTr = "Göğüs • Sırt • Omuz",
                    exercises = listOf(
                        ExerciseDatabase.sampleExercises[0],
                        ExerciseDatabase.sampleExercises[4],
                        ExerciseDatabase.sampleExercises[2]
                    )
                ),
                ProgramRoutine(
                    id = "lower_circuit",
                    titleTr = "Alt Vücut & Core",
                    muscleFocusTr = "Bacak • Kalça • Karın",
                    exercises = listOf(
                        ExerciseDatabase.sampleExercises[8],
                        ExerciseDatabase.sampleExercises[9]
                    )
                )
            )
        ),
        WorkoutProgram(
            id = "full_body",
            titleTr = "Tüm Vücut",
            subtitleTr = "Yoğun ve Dengeli",
            durationWeeks = 4,
            levelTr = "Başlangıç",
            targetGoalTr = "Genel Fitness",
            descriptionTr = "Haftada 3 gün tüm ana kas gruplarını çalıştırarak temel kuvvet ve koordinasyon oluşturan split.",
            daysPerWeek = 3,
            routines = listOf(
                ProgramRoutine(
                    id = "fullbody_a",
                    titleTr = "Tüm Vücut - Seans A",
                    muscleFocusTr = "Göğüs • Bacak • Sırt • Kol",
                    exercises = listOf(
                        ExerciseDatabase.sampleExercises[0],
                        ExerciseDatabase.sampleExercises[8],
                        ExerciseDatabase.sampleExercises[4],
                        ExerciseDatabase.sampleExercises[6]
                    )
                )
            )
        ),
        WorkoutProgram(
            id = "push_pull_legs",
            titleTr = "Push Pull Legs",
            subtitleTr = "İleri Seviye Program",
            durationWeeks = 12,
            levelTr = "İleri Seviye",
            targetGoalTr = "Kuvvet & Hacim",
            descriptionTr = "Spor salonunda deneyimli sporcular için kas gruplarını haftada iki kez hedefleyen profesyonel PPL döngüsü.",
            daysPerWeek = 6,
            routines = listOf(
                ProgramRoutine(
                    id = "ppl_push",
                    titleTr = "PPL - İtiş",
                    muscleFocusTr = "Göğüs • Omuz • Triceps",
                    exercises = listOf(
                        ExerciseDatabase.sampleExercises[0],
                        ExerciseDatabase.sampleExercises[1],
                        ExerciseDatabase.sampleExercises[3],
                        ExerciseDatabase.sampleExercises[7]
                    )
                ),
                ProgramRoutine(
                    id = "ppl_pull",
                    titleTr = "PPL - Çekiş",
                    muscleFocusTr = "Sırt • Biceps • Arka Omuz",
                    exercises = listOf(
                        ExerciseDatabase.sampleExercises[4],
                        ExerciseDatabase.sampleExercises[5],
                        ExerciseDatabase.sampleExercises[6]
                    )
                ),
                ProgramRoutine(
                    id = "ppl_legs",
                    titleTr = "PPL - Bacak",
                    muscleFocusTr = "Quadriceps • Hamstring • Kalça",
                    exercises = listOf(
                        ExerciseDatabase.sampleExercises[8],
                        ExerciseDatabase.sampleExercises[9]
                    )
                )
            )
        )
    )
}
