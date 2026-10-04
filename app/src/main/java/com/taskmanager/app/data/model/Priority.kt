package com.taskmanager.app.data.model

enum class Priority {
    LOW, MEDIUM, HIGH;

    companion object {
        fun fromName(name: String?): Priority =
            entries.find { it.name == name } ?: MEDIUM
    }
}
