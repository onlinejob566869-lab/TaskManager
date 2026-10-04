package com.taskmanager.app.util

object Constants {
    const val NOTIFICATION_CHANNEL_ID = "task_reminders"
    const val NOTIFICATION_CHANNEL_NAME = "Task Reminders"
    const val NOTIFICATION_CHANNEL_DESC = "Notifications for task due date reminders"

    const val DATASTORE_PREFS = "taskmanager_prefs"
    const val DARK_MODE_KEY = "dark_mode"

    const val EXTRA_TASK_ID = "task_id"
    const val EXTRA_TASK_TITLE = "task_title"

    // Default category colors
    val DEFAULT_CATEGORY_COLORS = listOf(
        0xFF6750A4, // Purple
        0xFF2196F3, // Blue
        0xFF4CAF50, // Green
        0xFFFF9800, // Orange
        0xFFE91E63, // Pink
        0xFF00BCD4, // Cyan
        0xFFFFC107, // Amber
        0xFF795548  // Brown
    )
}
