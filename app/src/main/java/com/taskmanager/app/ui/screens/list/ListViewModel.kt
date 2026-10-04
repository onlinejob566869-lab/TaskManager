package com.taskmanager.app.ui.screens.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.taskmanager.app.data.local.TaskWithCategory
import com.taskmanager.app.data.local.entities.CategoryEntity
import com.taskmanager.app.data.model.Task
import com.taskmanager.app.data.model.TaskStatus
import com.taskmanager.app.data.model.toEntity
import com.taskmanager.app.data.repository.CategoryRepository
import com.taskmanager.app.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ListFilter { ALL, PENDING, COMPLETED }

data class ListUiState(
    val tasks: List<TaskWithCategory> = emptyList(),
    val categories: List<CategoryEntity> = emptyList(),
    val searchQuery: String = "",
    val filter: ListFilter = ListFilter.ALL,
    val showAddSheet: Boolean = false,
    val editingTask: Task? = null
)

class ListViewModel(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _filter = MutableStateFlow(ListFilter.ALL)
    private val _showAddSheet = MutableStateFlow(false)
    private val _editingTask = MutableStateFlow<Task?>(null)

    private val allTasks = taskRepository.getAllTasksWithCategory()
    private val allCategories = categoryRepository.getAllCategories()

    // Combine in stages to avoid 6-flow combine limit
    private val sheetState = combine(_showAddSheet, _editingTask) { show, edit ->
        show to edit
    }

    val uiState: StateFlow<ListUiState> = combine(
        allTasks,
        combine(allCategories, _searchQuery, _filter) { cats, q, f -> Triple(cats, q, f) },
        sheetState
    ) { tasks, (categories, query, filter), (showSheet, editing) ->
        val filteredTasks = when {
            query.isNotEmpty() -> tasks.filter {
                it.task.title.contains(query, ignoreCase = true) ||
                it.task.description.contains(query, ignoreCase = true)
            }
            filter == ListFilter.COMPLETED -> tasks.filter { it.task.status == TaskStatus.DONE.name }
            filter == ListFilter.PENDING -> tasks.filter { it.task.status != TaskStatus.DONE.name }
            else -> tasks
        }
        ListUiState(
            tasks = filteredTasks,
            categories = categories,
            searchQuery = query,
            filter = filter,
            showAddSheet = showSheet,
            editingTask = editing
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        ListUiState()
    )

    fun updateSearchQuery(query: String) { _searchQuery.value = query }
    fun updateFilter(filter: ListFilter) { _filter.value = filter }

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

    fun deleteAllTasks() {
        viewModelScope.launch { taskRepository.deleteAllTasks() }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val taskRepository: TaskRepository,
        private val categoryRepository: CategoryRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ListViewModel::class.java))
                return ListViewModel(taskRepository, categoryRepository) as T
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
