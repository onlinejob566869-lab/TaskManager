package com.taskmanager.app.data.model

import com.taskmanager.app.data.local.entities.TaskEntity

data class Task(
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val categoryId: Long? = null,
    val categoryName: String? = null,
    val categoryColor: Long? = null,
    val status: TaskStatus = TaskStatus.TODO,
    val priority: Priority = Priority.MEDIUM,
    val dueDate: Long? = null,
    val dueTime: Long? = null,
    val reminderEnabled: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)

fun TaskEntity.toDomain(
    categoryName: String? = null,
    categoryColor: Long? = null
): Task = Task(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId,
    categoryName = categoryName,
    categoryColor = categoryColor,
    status = TaskStatus.fromName(status),
    priority = Priority.fromName(priority),
    dueDate = dueDate,
    dueTime = dueTime,
    reminderEnabled = reminderEnabled,
    createdAt = createdAt,
    updatedAt = updatedAt,
    completedAt = completedAt
)

fun Task.toEntity(): TaskEntity = TaskEntity(
    id = id,
    title = title,
    description = description,
    categoryId = categoryId,
    status = status.name,
    priority = priority.name,
    dueDate = dueDate,
    dueTime = dueTime,
    reminderEnabled = reminderEnabled,
    createdAt = createdAt,
    updatedAt = System.currentTimeMillis(),
    completedAt = completedAt
)
