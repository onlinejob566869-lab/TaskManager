package com.taskmanager.app.data.local

import androidx.room.Embedded
import androidx.room.Relation
import com.taskmanager.app.data.local.entities.TaskEntity

data class TaskWithCategory(
    @Embedded val task: TaskEntity,
    val categoryName: String?,
    val categoryColor: Long?
)
