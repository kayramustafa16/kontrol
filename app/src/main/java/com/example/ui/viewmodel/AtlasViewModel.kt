package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AtlasDatabase
import com.example.data.mesh.MuscleMeshMapping
import com.example.data.model.*
import com.example.data.repository.AtlasRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.math.round

class AtlasViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AtlasDatabase.getInstance(application)
    private val repository = AtlasRepository(database.atlasDao())
    val meshMapping = MuscleMeshMapping(application)

    // Navigation state
    private val _currentScreen = MutableStateFlow("home")
    val currentScreen: StateFlow<String> = _currentScreen.asStateFlow()

    private val navStack = mutableListOf("home")

    // Anatomy & 3D state
    private val _selectedMuscle = MutableStateFlow(MuscleGroup.CHEST)
    val selectedMuscle: StateFlow<MuscleGroup> = _selectedMuscle.asStateFlow()

    private val _customModelPath = MutableStateFlow<String?>(null)
    val customModelPath: StateFlow<String?> = _customModelPath.asStateFlow()

    // Subregion state for single-muscle discovery flow
    private val _selectedSubregion = MutableStateFlow("Üst Göğüs")
    val selectedSubregion: StateFlow<String> = _selectedSubregion.asStateFlow()

    // Multi-muscle selection state for Program Builder
    private val _selectedProgramMuscles = MutableStateFlow<Set<MuscleGroup>>(setOf(MuscleGroup.CHEST, MuscleGroup.SHOULDERS))
    val selectedProgramMuscles: StateFlow<Set<MuscleGroup>> = _selectedProgramMuscles.asStateFlow()

    // Exercise details state
    private val _selectedExercise = MutableStateFlow(ExerciseDatabase.sampleExercises.first())
    val selectedExercise: StateFlow<Exercise> = _selectedExercise.asStateFlow()

    // Selected program details
    private val _selectedProgram = MutableStateFlow(ProgramDatabase.samplePrograms.first())
    val selectedProgram: StateFlow<WorkoutProgram> = _selectedProgram.asStateFlow()

    // Active Workout session state
    private val _activeSession = MutableStateFlow<ActiveWorkoutSession?>(null)
    val activeSession: StateFlow<ActiveWorkoutSession?> = _activeSession.asStateFlow()

    private var workoutTimerJob: Job? = null
    private val _workoutTimerSeconds = MutableStateFlow(45)
    val workoutTimerSeconds: StateFlow<Int> = _workoutTimerSeconds.asStateFlow()

    // Finished workout summary dialog state
    private val _lastFinishedWorkout = MutableStateFlow<FinishedWorkoutSummary?>(null)
    val lastFinishedWorkout: StateFlow<FinishedWorkoutSummary?> = _lastFinishedWorkout.asStateFlow()

    // Nutrition state
    private val currentEpochDay = System.currentTimeMillis() / (1000 * 60 * 60 * 24)
    val todayMeals: StateFlow<List<LoggedMealEntry>> = repository.getTodayMeals(currentEpochDay)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Progress & History
    val weightHistory: StateFlow<List<WeightRecord>> = repository.weightHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val bodyMeasurements: StateFlow<List<BodyMeasurement>> = repository.bodyMeasurements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val workoutHistory: StateFlow<List<FinishedWorkoutSummary>> = repository.workoutHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Lifestyle state
    private val _dailyLifestyle = MutableStateFlow(DailyLifestyle())
    val dailyLifestyle: StateFlow<DailyLifestyle> = _dailyLifestyle.asStateFlow()

    // Profile state
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()

    init {
        // Seed initial demo data matching the reference image if database is empty
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty(currentEpochDay)
        }
        // Observe saved user profile
        viewModelScope.launch {
            repository.userSettings.collect { profile ->
                _userProfile.value = profile
                _customModelPath.value = profile.custom3dModelPath
            }
        }
        // Initialize default active workout session with Push Day
        initDefaultPushDayWorkout()
    }

    // --- Navigation Flow ---

    fun navigateTo(route: String) {
        if (_currentScreen.value != route) {
            navStack.add(route)
            _currentScreen.value = route
        }
    }

    fun navigateBack(): Boolean {
        if (navStack.size > 1) {
            navStack.removeAt(navStack.size - 1)
            _currentScreen.value = navStack.last()
            return true
        }
        return false
    }

    // --- 3D Anatomy & Muscle Selection ---

    fun selectMuscle(group: MuscleGroup) {
        _selectedMuscle.value = group
        // Reset subregion to first valid subregion of selected muscle
        group.subMusclesTr.firstOrNull()?.let {
            _selectedSubregion.value = it
        }
    }

    fun selectSubregion(subregion: String) {
        _selectedSubregion.value = subregion
    }

    fun openSubregionExercises(subregion: String) {
        _selectedSubregion.value = subregion
        val matching = ExerciseDatabase.getExercisesForSubregion(subregion)
        if (matching.isNotEmpty()) {
            _selectedExercise.value = matching.first()
        } else {
            val fallback = ExerciseDatabase.getExercisesForMuscle(_selectedMuscle.value).firstOrNull()
                ?: ExerciseDatabase.sampleExercises.first()
            _selectedExercise.value = fallback
        }
        navigateTo("exercise_detail")
    }

    // --- Multi-Muscle Program Builder Flow ---

    fun toggleProgramMuscle(group: MuscleGroup) {
        val current = _selectedProgramMuscles.value.toMutableSet()
        if (current.contains(group)) {
            if (current.size > 1) { // Keep at least one muscle selected
                current.remove(group)
            }
        } else {
            current.add(group)
        }
        _selectedProgramMuscles.value = current
    }

    fun generateProgramFromSelectedMuscles(customTitle: String? = null) {
        val selectedMuscles = _selectedProgramMuscles.value
        val matchedExercises = mutableListOf<Exercise>()

        selectedMuscles.forEach { muscle ->
            val exForMuscle = ExerciseDatabase.getExercisesForMuscle(muscle)
            if (exForMuscle.isNotEmpty()) {
                matchedExercises.addAll(exForMuscle.take(2)) // 2 best exercises per selected muscle
            }
        }

        if (matchedExercises.isEmpty()) {
            matchedExercises.addAll(ExerciseDatabase.sampleExercises.take(4))
        }

        val muscleFocusStr = selectedMuscles.joinToString(" • ") { it.displayNameTr }
        val generatedTitle = customTitle ?: "Özel Split (${selectedMuscles.size} Kas Grubu)"

        val exerciseStates = matchedExercises.map { ex ->
            ActiveExerciseState(
                exercise = ex,
                sets = (1..ex.defaultSets).map { setIndex ->
                    WorkoutSetState(
                        setNumber = setIndex,
                        weightKg = 60.0,
                        reps = 10,
                        isCompleted = false,
                        restSeconds = ex.defaultRestSeconds
                    )
                }
            )
        }

        _activeSession.value = ActiveWorkoutSession(
            programTitleTr = generatedTitle,
            muscleFocusTr = muscleFocusStr,
            startTimeMs = System.currentTimeMillis(),
            exercises = exerciseStates
        )

        startWorkoutTimer()
        navigateTo("active_workout")
    }

    fun openMuscleDetail(group: MuscleGroup) {
        _selectedMuscle.value = group
        navigateTo("muscle_detail")
    }

    fun setCustomModelPath(path: String) {
        _customModelPath.value = path
        viewModelScope.launch {
            val updated = _userProfile.value.copy(custom3dModelPath = path)
            repository.saveProfile(updated)
        }
    }

    // --- Exercise & Program Flows ---

    fun openExerciseDetail(exercise: Exercise) {
        _selectedExercise.value = exercise
        _selectedSubregion.value = exercise.subregionTr
        navigateTo("exercise_detail")
    }

    fun openProgramDetail(program: WorkoutProgram) {
        _selectedProgram.value = program
        navigateTo("program_detail")
    }

    // --- Active Workout Engine ---

    private fun initDefaultPushDayWorkout() {
        val pushProgram = ProgramDatabase.samplePrograms.first()
        val routine = pushProgram.routines.first()
        val exerciseStates = routine.exercises.map { ex ->
            ActiveExerciseState(
                exercise = ex,
                sets = (1..ex.defaultSets).map { setIndex ->
                    WorkoutSetState(
                        setNumber = setIndex,
                        weightKg = if (setIndex == 1) 60.0 else if (setIndex == 2) 70.0 else 75.0,
                        reps = if (setIndex <= 2) 10 else 8,
                        isCompleted = setIndex <= 2, // Sets 1 & 2 completed in reference image
                        restSeconds = ex.defaultRestSeconds
                    )
                }
            )
        }

        _activeSession.value = ActiveWorkoutSession(
            programTitleTr = routine.titleTr,
            muscleFocusTr = routine.muscleFocusTr,
            exercises = exerciseStates
        )
    }

    fun startWorkout(programRoutine: ProgramRoutine? = null) {
        val routine = programRoutine ?: ProgramDatabase.samplePrograms.first().routines.first()
        val exerciseStates = routine.exercises.map { ex ->
            ActiveExerciseState(
                exercise = ex,
                sets = (1..ex.defaultSets).map { setIndex ->
                    WorkoutSetState(
                        setNumber = setIndex,
                        weightKg = 60.0,
                        reps = 10,
                        isCompleted = false,
                        restSeconds = ex.defaultRestSeconds
                    )
                }
            )
        }

        _activeSession.value = ActiveWorkoutSession(
            programTitleTr = routine.titleTr,
            muscleFocusTr = routine.muscleFocusTr,
            startTimeMs = System.currentTimeMillis(),
            exercises = exerciseStates
        )
        startWorkoutTimer()
        navigateTo("active_workout")
    }

    private fun startWorkoutTimer() {
        workoutTimerJob?.cancel()
        _workoutTimerSeconds.value = 45 // 00:45 starter
        workoutTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000)
                _workoutTimerSeconds.value += 1
            }
        }
    }

    fun toggleSetCompleted(exerciseIndex: Int, setIndex: Int) {
        val current = _activeSession.value ?: return
        val exercises = current.exercises.toMutableList()
        val targetEx = exercises[exerciseIndex]
        val sets = targetEx.sets.toMutableList()
        val targetSet = sets[setIndex]

        sets[setIndex] = targetSet.copy(isCompleted = !targetSet.isCompleted)
        exercises[exerciseIndex] = targetEx.copy(sets = sets)
        _activeSession.value = current.copy(exercises = exercises)
    }

    fun updateSetValues(exerciseIndex: Int, setIndex: Int, weight: Double?, reps: Int?) {
        val current = _activeSession.value ?: return
        val exercises = current.exercises.toMutableList()
        val targetEx = exercises[exerciseIndex]
        val sets = targetEx.sets.toMutableList()
        val targetSet = sets[setIndex]

        sets[setIndex] = targetSet.copy(
            weightKg = weight ?: targetSet.weightKg,
            reps = reps ?: targetSet.reps
        )
        exercises[exerciseIndex] = targetEx.copy(sets = sets)
        _activeSession.value = current.copy(exercises = exercises)
    }

    fun addSetToExercise(exerciseIndex: Int) {
        val current = _activeSession.value ?: return
        val exercises = current.exercises.toMutableList()
        val targetEx = exercises[exerciseIndex]
        val sets = targetEx.sets.toMutableList()
        val lastSet = sets.lastOrNull()
        val nextNumber = (lastSet?.setNumber ?: 0) + 1

        sets.add(
            WorkoutSetState(
                setNumber = nextNumber,
                weightKg = lastSet?.weightKg ?: 60.0,
                reps = lastSet?.reps ?: 10,
                isCompleted = false,
                restSeconds = targetEx.exercise.defaultRestSeconds
            )
        )
        exercises[exerciseIndex] = targetEx.copy(sets = sets)
        _activeSession.value = current.copy(exercises = exercises)
    }

    fun finishWorkout() {
        val current = _activeSession.value ?: return
        workoutTimerJob?.cancel()

        val summary = FinishedWorkoutSummary(
            sessionId = current.sessionId,
            titleTr = current.programTitleTr,
            dateEpochMs = System.currentTimeMillis(),
            durationSeconds = _workoutTimerSeconds.value,
            totalVolumeKg = current.totalVolumeKg,
            completedSets = current.completedSetsCount,
            exercisesCount = current.exercises.size
        )

        _lastFinishedWorkout.value = summary
        viewModelScope.launch {
            repository.saveWorkout(summary)
        }
    }

    fun dismissFinishedWorkoutDialog() {
        _lastFinishedWorkout.value = null
        navigateTo("home")
    }

    // --- Nutrition Actions ---

    fun logFoodItem(food: FoodItem, servingGrams: Double, category: MealCategory) {
        val cals = FoodDatabase.calculateNutrient(food.caloriesPer100g, servingGrams)
        val protein = FoodDatabase.calculateNutrient(food.proteinPer100g, servingGrams)
        val carbs = FoodDatabase.calculateNutrient(food.carbsPer100g, servingGrams)
        val fat = FoodDatabase.calculateNutrient(food.fatPer100g, servingGrams)

        val entry = LoggedMealEntry(
            mealCategory = category,
            foodNameTr = "${food.nameTr} (${servingGrams.toInt()}g)",
            servingGrams = servingGrams,
            calories = cals,
            proteinGrams = protein,
            carbsGrams = carbs,
            fatGrams = fat,
            dateEpochDay = currentEpochDay
        )

        viewModelScope.launch {
            repository.addMeal(entry)
        }
    }

    fun deleteMealEntry(id: String) {
        viewModelScope.launch {
            repository.deleteMeal(id)
        }
    }

    // --- Progress & Weight Logging ---

    fun logWeight(weightKg: Double) {
        viewModelScope.launch {
            repository.addWeight(weightKg)
            val updatedProfile = _userProfile.value.copy(currentWeightKg = weightKg)
            _userProfile.value = updatedProfile
            repository.saveProfile(updatedProfile)
        }
    }

    fun logMeasurements(chestCm: Double, waistCm: Double, armCm: Double, legCm: Double) {
        viewModelScope.launch {
            repository.addMeasurement(chestCm, waistCm, armCm, legCm)
        }
    }

    // --- Lifestyle Logging ---

    fun adjustWater(deltaLiters: Double) {
        val current = _dailyLifestyle.value
        val newAmount = (current.waterLiters + deltaLiters).coerceAtLeast(0.0)
        _dailyLifestyle.value = current.copy(waterLiters = (round(newAmount * 10) / 10.0))
    }

    fun updateLifestyle(sleepHours: Double? = null, steps: Int? = null, mood: Int? = null, reminders: Boolean? = null) {
        val current = _dailyLifestyle.value
        _dailyLifestyle.value = current.copy(
            sleepHours = sleepHours ?: current.sleepHours,
            steps = steps ?: current.steps,
            moodScore = mood ?: current.moodScore,
            reminderEnabled = reminders ?: current.reminderEnabled
        )
    }

    fun updateProfile(profile: UserProfile) {
        _userProfile.value = profile
        viewModelScope.launch {
            repository.saveProfile(profile)
        }
    }
}
