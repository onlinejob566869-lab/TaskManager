package com.taskmanager.app.ui.screens.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.taskmanager.app.data.local.TaskWithCategory
import com.taskmanager.app.data.repository.TaskRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

data class CalendarUiState(
    val tasks: List<TaskWithCategory> = emptyList(),
    val selectedDate: Long = System.currentTimeMillis()
)

class CalendarViewModel(
    private val taskRepository: TaskRepository
) : ViewModel() {

    private val _selectedDate = kotlinx.coroutines.flow.MutableStateFlow(System.currentTimeMillis())

    private val allTasks = taskRepository.getTasksWithDueDate()

    val uiState: StateFlow<CalendarUiState> = kotlinx.coroutines.flow.combine(
        allTasks, _selectedDate
    ) { tasks, selectedDate ->
        CalendarUiState(tasks = tasks, selectedDate = selectedDate)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), CalendarUiState())

    fun selectDate(date: Long) {
        _selectedDate.value = date
    }

    fun getTasksForDate(date: Long): List<TaskWithCategory> {
        val startOfDay = getStartOfDay(date)
        val endOfDay = getEndOfDay(date)
        return uiState.value.tasks.filter {
            it.task.dueDate != null && it.task.dueDate in startOfDay..endOfDay
        }
    }

    private fun getStartOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return cal.timeInMillis
    }

    private fun getEndOfDay(timestamp: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = timestamp
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 59)
            set(Calendar.SECOND, 59)
            set(Calendar.MILLISECOND, 999)
        }
        return cal.timeInMillis
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(private val taskRepository: TaskRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(CalendarViewModel::class.java))
                return CalendarViewModel(taskRepository) as T
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
