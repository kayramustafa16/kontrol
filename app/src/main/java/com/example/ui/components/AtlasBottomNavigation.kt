package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

enum class BottomNavScreen(
    val route: String,
    val titleTr: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    HOME("home", "Ana Sayfa", Icons.Filled.Home, Icons.Outlined.Home),
    PROGRAMS("programs", "Program", Icons.Filled.CalendarMonth, Icons.Outlined.CalendarMonth),
    EXPLORE("explore", "Keşfet", Icons.Filled.Search, Icons.Outlined.Search),
    NUTRITION("nutrition", "Beslenme", Icons.Filled.Restaurant, Icons.Outlined.Restaurant),
    PROFILE("profile", "Profil", Icons.Filled.Person, Icons.Outlined.Person)
}

@Composable
fun AtlasBottomNavigation(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .windowInsetsPadding(WindowInsets.navigationBars)
            .border(width = 1.dp, color = DarkCardBorder)
            .testTag("atlas_bottom_navigation"),
        containerColor = Color(0xF20B0E16),
        tonalElevation = 8.dp
    ) {
        BottomNavScreen.entries.forEach { screen ->
            val isSelected = currentRoute == screen.route

            NavigationBarItem(
                selected = isSelected,
                onClick = { onNavigate(screen.route) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) screen.selectedIcon else screen.unselectedIcon,
                        contentDescription = screen.titleTr,
                        modifier = Modifier.size(24.dp)
                    )
                },
                label = {
                    Text(
                        text = screen.titleTr,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = CrimsonRed,
                    selectedTextColor = CrimsonRed,
                    indicatorColor = Color(0x33FF334B),
                    unselectedIconColor = TextSecondary,
                    unselectedTextColor = TextSecondary
                ),
                modifier = Modifier.testTag("nav_item_${screen.route}")
            )
        }
    }
}
