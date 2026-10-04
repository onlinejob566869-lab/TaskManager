package com.taskmanager.app.data.repository

import com.taskmanager.app.data.local.TaskDao
import com.taskmanager.app.data.local.TaskWithCategory
import com.taskmanager.app.data.local.entities.TaskEntity
import com.taskmanager.app.data.model.Priority
import com.taskmanager.app.data.model.TaskStatus
import kotlinx.coroutines.flow.Flow

class TaskRepository(private val taskDao: TaskDao) {

    fun getAllTasksWithCategory(): Flow<List<TaskWithCategory>> =
        taskDao.getAllTasksWithCategory()

    fun getPendingTasksWithCategory(): Flow<List<TaskWithCategory>> =
        taskDao.getPendingTasksWithCategory()

    fun getTasksWithDueDate(): Flow<List<TaskWithCategory>> =
        taskDao.getTasksWithDueDate()

    fun getTasksByStatus(status: TaskStatus): Flow<List<TaskWithCategory>> =
        taskDao.getTasksByStatus(status.name)

    fun searchTasks(query: String): Flow<List<TaskWithCategory>> =
        taskDao.searchTasks(query)

    suspend fun getTaskById(id: Long): TaskEntity? = taskDao.getTaskById(id)

    suspend fun getTasksWithReminders(): List<TaskEntity> = taskDao.getTasksWithReminders()

    suspend fun insertTask(task: TaskEntity): Long = taskDao.insertTask(task)

    suspend fun updateTask(task: TaskEntity) = taskDao.updateTask(task)

    suspend fun deleteTask(task: TaskEntity) = taskDao.deleteTask(task)

    suspend fun deleteTaskById(id: Long) = taskDao.deleteTaskById(id)

    suspend fun updateStatus(id: Long, status: TaskStatus, completedAt: Long?) {
        val updatedAt = System.currentTimeMillis()
        taskDao.updateStatus(id, status.name, completedAt, updatedAt)
    }

    suspend fun deleteAllTasks() = taskDao.deleteAllTasks()
}
