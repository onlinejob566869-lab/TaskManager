package com.taskmanager.app.data.model

enum class TaskStatus {
    TODO, IN_PROGRESS, DONE;

    companion object {
        fun fromName(name: String?): TaskStatus =
            entries.find { it.name == name } ?: TODO
    }
}
