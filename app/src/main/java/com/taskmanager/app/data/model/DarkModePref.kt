package com.taskmanager.app.data.model

enum class DarkModePref(val displayName: String) {
    SYSTEM("System default"),
    LIGHT("Light"),
    DARK("Dark");

    companion object {
        fun fromName(name: String?): DarkModePref =
            entries.find { it.name == name } ?: SYSTEM
    }
}
