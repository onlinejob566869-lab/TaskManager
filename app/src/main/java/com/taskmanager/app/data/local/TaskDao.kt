package com.taskmanager.app.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.taskmanager.app.data.local.entities.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks ORDER BY createdAt DESC")
    fun getAllTasks(): Flow<List<TaskEntity>>

    @Query("""
        SELECT t.*, c.name as categoryName, c.color as categoryColor
        FROM tasks t
        LEFT JOIN categories c ON t.categoryId = c.id
        ORDER BY 
            CASE t.status WHEN 'DONE' THEN 1 ELSE 0 END,
            CASE t.priority WHEN 'HIGH' THEN 0 WHEN 'MEDIUM' THEN 1 ELSE 2 END,
            t.createdAt DESC
    """)
    fun getAllTasksWithCategory(): Flow<List<TaskWithCategory>>

    @Query("""
        SELECT t.*, c.name as categoryName, c.color as categoryColor
        FROM tasks t
        LEFT JOIN categories c ON t.categoryId = c.id
        WHERE t.status != 'DONE'
        ORDER BY 
            CASE WHEN t.dueDate IS NULL THEN 1 ELSE 0 END,
            t.dueDate ASC,
            t.createdAt DESC
    """)
    fun getPendingTasksWithCategory(): Flow<List<TaskWithCategory>>

    @Query("""
        SELECT t.*, c.name as categoryName, c.color as categoryColor
        FROM tasks t
        LEFT JOIN categories c ON t.categoryId = c.id
        WHERE t.dueDate IS NOT NULL
        ORDER BY t.dueDate ASC
    """)
    fun getTasksWithDueDate(): Flow<List<TaskWithCategory>>

    @Query("""
        SELECT t.*, c.name as categoryName, c.color as categoryColor
        FROM tasks t
        LEFT JOIN categories c ON t.categoryId = c.id
        WHERE t.status = :status
        ORDER BY t.createdAt DESC
    """)
    fun getTasksByStatus(status: String): Flow<List<TaskWithCategory>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskById(id: Long): TaskEntity?

    @Query("""
        SELECT t.*, c.name as categoryName, c.color as categoryColor
        FROM tasks t
        LEFT JOIN categories c ON t.categoryId = c.id
        WHERE t.title LIKE '%' || :query || '%' OR t.description LIKE '%' || :query || '%'
        ORDER BY t.createdAt DESC
    """)
    fun searchTasks(query: String): Flow<List<TaskWithCategory>>

    @Query("SELECT * FROM tasks WHERE reminderEnabled = 1 AND status != 'DONE'")
    suspend fun getTasksWithReminders(): List<TaskEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskEntity): Long

    @Update
    suspend fun updateTask(task: TaskEntity)

    @Delete
    suspend fun deleteTask(task: TaskEntity)

    @Query("DELETE FROM tasks WHERE id = :id")
    suspend fun deleteTaskById(id: Long)

    @Query("UPDATE tasks SET status = :status, completedAt = :completedAt, updatedAt = :updatedAt WHERE id = :id")
    suspend fun updateStatus(id: Long, status: String, completedAt: Long?, updatedAt: Long)

    @Query("DELETE FROM tasks")
    suspend fun deleteAllTasks()
}
