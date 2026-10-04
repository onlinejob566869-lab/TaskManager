package com.taskmanager.app.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Screen(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
) {
    List(
        route = "list",
        label = "Tasks",
        selectedIcon = Icons.Filled.List,
        unselectedIcon = Icons.Filled.List
    ),
    Board(
        route = "board",
        label = "Board",
        selectedIcon = Icons.Filled.Menu,
        unselectedIcon = Icons.Filled.Menu
    ),
    Calendar(
        route = "calendar",
        label = "Calendar",
        selectedIcon = Icons.Filled.DateRange,
        unselectedIcon = Icons.Filled.DateRange
    ),
    Today(
        route = "today",
        label = "Today",
        selectedIcon = Icons.Filled.Done,
        unselectedIcon = Icons.Filled.Done
    ),
    Settings(
        route = "settings",
        label = "Settings",
        selectedIcon = Icons.Filled.Settings,
        unselectedIcon = Icons.Filled.Settings
    )
}
