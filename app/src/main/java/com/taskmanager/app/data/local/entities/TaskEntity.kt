package com.taskmanager.app.data.local.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.taskmanager.app.data.model.Priority
import com.taskmanager.app.data.model.TaskStatus

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val categoryId: Long? = null,
    val status: String = TaskStatus.TODO.name,
    val priority: String = Priority.MEDIUM.name,
    val dueDate: Long? = null,
    val dueTime: Long? = null,
    val reminderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
