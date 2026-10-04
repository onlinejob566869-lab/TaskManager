package com.taskmanager.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.taskmanager.app.ui.screens.board.BoardScreen
import com.taskmanager.app.ui.screens.calendar.CalendarScreen
import com.taskmanager.app.ui.screens.list.ListScreen
import com.taskmanager.app.ui.screens.settings.SettingsScreen
import com.taskmanager.app.ui.screens.today.TodayScreen

@Composable
fun AppNavigation(
    navController: NavHostController,
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit
) {
    val screens = listOf(Screen.List, Screen.Board, Screen.Calendar, Screen.Today, Screen.Settings)

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.destination?.route
            NavigationBar {
                screens.forEach { screen ->
                    NavigationBarItem(
                        selected = currentRoute == screen.route,
                        onClick = {
                            if (currentRoute != screen.route) {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        },
                        icon = {
                            Icon(
                                imageVector = if (currentRoute == screen.route)
                                    screen.selectedIcon else screen.unselectedIcon,
                                contentDescription = screen.label
                            )
                        },
                        label = { Text(screen.label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.List.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.List.route) {
                ListScreen(
                    onAddTask = onAddTask,
                    onEditTask = onEditTask
                )
            }
            composable(Screen.Board.route) {
                BoardScreen(
                    onAddTask = onAddTask,
                    onEditTask = onEditTask
                )
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(
                    onEditTask = onEditTask
                )
            }
            composable(Screen.Today.route) {
                TodayScreen(
                    onAddTask = onAddTask,
                    onEditTask = onEditTask
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen()
            }
        }
    }
}
