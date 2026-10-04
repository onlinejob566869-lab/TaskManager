package com.taskmanager.app.ui.screens.board

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.taskmanager.app.data.local.TaskWithCategory
import com.taskmanager.app.data.model.Task
import com.taskmanager.app.data.model.TaskStatus
import com.taskmanager.app.data.model.toEntity
import com.taskmanager.app.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BoardUiState(
    val todoTasks: List<TaskWithCategory> = emptyList(),
    val inProgressTasks: List<TaskWithCategory> = emptyList(),
    val doneTasks: List<TaskWithCategory> = emptyList(),
    val showAddSheet: Boolean = false,
    val editingTask: Task? = null
)

class BoardViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _showAddSheet = kotlinx.coroutines.flow.MutableStateFlow(false)
    private val _editingTask = kotlinx.coroutines.flow.MutableStateFlow<Task?>(null)

    private val allTasks = taskRepository.getAllTasksWithCategory()

    val uiState: StateFlow<BoardUiState> = combine(
        allTasks, _showAddSheet, _editingTask
    ) { tasks, showSheet, editing ->
        BoardUiState(
            todoTasks = tasks.filter { it.task.status == TaskStatus.TODO.name },
            inProgressTasks = tasks.filter { it.task.status == TaskStatus.IN_PROGRESS.name },
            doneTasks = tasks.filter { it.task.status == TaskStatus.DONE.name },
            showAddSheet = showSheet,
            editingTask = editing
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BoardUiState())

    fun showAddSheet() {
        _editingTask.value = null
        _showAddSheet.value = true
    }

    fun showEditSheet(task: Task) {
        _editingTask.value = task
        _showAddSheet.value = true
    }

    fun hideSheet() {
        _showAddSheet.value = false
        _editingTask.value = null
    }

    fun saveTask(task: Task) {
        viewModelScope.launch {
            if (task.id == 0L) taskRepository.insertTask(task.toEntity())
            else taskRepository.updateTask(task.toEntity())
            hideSheet()
        }
    }

    fun moveTask(task: TaskWithCategory, newStatus: TaskStatus) {
        viewModelScope.launch {
            val completedAt = if (newStatus == TaskStatus.DONE) System.currentTimeMillis() else null
            taskRepository.updateStatus(task.task.id, newStatus, completedAt)
        }
    }

    fun deleteTask(task: TaskWithCategory) {
        viewModelScope.launch { taskRepository.deleteTaskById(task.task.id) }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val taskRepository: TaskRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(BoardViewModel::class.java))
                return BoardViewModel(taskRepository) as T
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
