package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.example.ui.components.AtlasBottomNavigation
import com.example.ui.screens.*
import com.example.ui.theme.AnabolikAtlasTheme
import com.example.ui.viewmodel.AtlasViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: AtlasViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AnabolikAtlasTheme {
                MainContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainContent(viewModel: AtlasViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    val showBottomBar = currentScreen in listOf(
        "home", "programs", "explore", "nutrition", "profile"
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            if (showBottomBar) {
                AtlasBottomNavigation(
                    currentRoute = currentScreen,
                    onNavigate = { route -> viewModel.navigateTo(route) }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = if (showBottomBar) innerPadding.calculateBottomPadding() else androidx.compose.ui.unit.Dp.Unspecified)
        ) {
            Crossfade(targetState = currentScreen, label = "screenTransition") { screen ->
                when (screen) {
                    "home" -> HomeScreen(viewModel = viewModel)
                    "programs" -> ProgramsScreen(viewModel = viewModel)
                    "explore" -> ExploreScreen(viewModel = viewModel)
                    "nutrition" -> NutritionScreen(viewModel = viewModel)
                    "profile" -> ProfileScreen(viewModel = viewModel)
                    "anatomy_3d" -> AnatomyScreen(viewModel = viewModel)
                    "anatomy_workout_selection" -> AnatomyWorkoutSelectionScreen(viewModel = viewModel)
                    "program_builder" -> ProgramBuilderScreen(viewModel = viewModel)
                    "exercise_detail" -> ExerciseDetailScreen(viewModel = viewModel)
                    "active_workout" -> WorkoutSessionScreen(viewModel = viewModel)
                    "progress" -> ProgressScreen(viewModel = viewModel)
                    "lifestyle" -> LifestyleScreen(viewModel = viewModel)
                    "muscle_detail" -> MuscleDetailScreen(viewModel = viewModel)
                    "program_detail" -> ProgramDetailScreen(viewModel = viewModel)
                    "model_manager" -> ModelManagerScreen(viewModel = viewModel)
                    else -> HomeScreen(viewModel = viewModel)
                }
            }
        }
    }
}
