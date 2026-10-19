package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AtlasColorScheme = darkColorScheme(
    primary = ElectricCyan,
    onPrimary = Color.Black,
    primaryContainer = ElectricCyanMuted,
    onPrimaryContainer = Color.White,
    secondary = BrightBlue,
    onSecondary = Color.White,
    tertiary = CrimsonRed,
    onTertiary = Color.White,
    background = DarkBackground,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkCardSurface,
    onSurfaceVariant = TextSecondary,
    outline = DarkCardBorder,
    outlineVariant = DarkCardSurfaceElevated
)

@Composable
fun AnabolikAtlasTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AtlasColorScheme,
        typography = Typography,
        content = content
    )
}
