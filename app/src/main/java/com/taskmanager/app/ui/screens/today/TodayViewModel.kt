package com.taskmanager.app.ui.screens.today

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.taskmanager.app.data.local.TaskWithCategory
import com.taskmanager.app.data.model.Task
import com.taskmanager.app.data.model.TaskStatus
import com.taskmanager.app.data.model.toEntity
import com.taskmanager.app.data.repository.TaskRepository
import com.taskmanager.app.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class TodayUiState(
    val todayTasks: List<TaskWithCategory> = emptyList(),
    val upcomingTasks: List<TaskWithCategory> = emptyList(),
    val overdueTasks: List<TaskWithCategory> = emptyList(),
    val showAddSheet: Boolean = false,
    val editingTask: Task? = null
)

class TodayViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _showAddSheet = MutableStateFlow(false)
    private val _editingTask = MutableStateFlow<Task?>(null)

    private val allTasks = taskRepository.getAllTasksWithCategory()

    val uiState: StateFlow<TodayUiState> = combine(
        allTasks, _showAddSheet, _editingTask
    ) { tasks, showSheet, editing ->
        val pending = tasks.filter { it.task.status != TaskStatus.DONE.name }

        TodayUiState(
            todayTasks = pending.filter {
                it.task.dueDate != null && DateUtils.isToday(it.task.dueDate)
            },
            upcomingTasks = pending.filter {
                it.task.dueDate != null && DateUtils.isUpcoming(it.task.dueDate)
            },
            overdueTasks = pending.filter {
                it.task.dueDate != null && DateUtils.isOverdue(it.task.dueDate)
            },
            showAddSheet = showSheet,
            editingTask = editing
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), TodayUiState())

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

    fun updateTaskStatus(task: TaskWithCategory, status: TaskStatus) {
        viewModelScope.launch {
            val completedAt = if (status == TaskStatus.DONE) System.currentTimeMillis() else null
            taskRepository.updateStatus(task.task.id, status, completedAt)
        }
    }

    fun deleteTask(task: TaskWithCategory) {
        viewModelScope.launch { taskRepository.deleteTaskById(task.task.id) }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val taskRepository: TaskRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(TodayViewModel::class.java))
                return TodayViewModel(taskRepository) as T
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
