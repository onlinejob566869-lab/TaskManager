package com.taskmanager.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.taskmanager.app.notifications.NotificationHelper
import com.taskmanager.app.ui.navigation.AppNavigation
import com.taskmanager.app.ui.theme.TaskManagerTheme
import com.taskmanager.app.ui.screens.settings.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Create notification channel
        NotificationHelper.createNotificationChannel(this)

        setContent {
            val settingsViewModel = remember { SettingsViewModel(this) }
            val darkModePref by settingsViewModel.darkModeFlow.collectAsStateWithLifecycle(
                initialValue = com.taskmanager.app.data.model.DarkModePref.SYSTEM
            )

            val isDark = when (darkModePref) {
                com.taskmanager.app.data.model.DarkModePref.LIGHT -> false
                com.taskmanager.app.data.model.DarkModePref.DARK -> true
                com.taskmanager.app.data.model.DarkModePref.SYSTEM -> {
                    val nightMode = resources.configuration.uiMode and
                        android.content.res.Configuration.UI_MODE_NIGHT_MASK
                    nightMode == android.content.res.Configuration.UI_MODE_NIGHT_YES
                }
            }

            TaskManagerTheme(darkTheme = isDark) {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    onAddTask = { /* handled per-screen */ },
                    onEditTask = { /* handled per-screen */ }
                )
            }
        }
    }
}
