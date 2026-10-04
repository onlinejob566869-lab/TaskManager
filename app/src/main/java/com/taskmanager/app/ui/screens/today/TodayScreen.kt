package com.taskmanager.app.ui.screens.today

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskmanager.app.TaskManagerApp
import com.taskmanager.app.data.local.TaskWithCategory
import com.taskmanager.app.data.model.TaskStatus
import com.taskmanager.app.data.model.toDomain
import com.taskmanager.app.ui.components.AddTaskSheet
import com.taskmanager.app.ui.components.EmptyState
import com.taskmanager.app.ui.components.TaskCard
import com.taskmanager.app.ui.screens.list.SwipeableTaskCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TodayScreen(
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit,
    viewModel: TodayViewModel = viewModel(factory = TodayViewModel.Factory(
        TaskManagerApp.instance.appModule.taskRepository
    ))
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Today & Upcoming", style = MaterialTheme.typography.titleLarge) })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddSheet() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        }
    ) { paddingValues ->
        if (uiState.todayTasks.isEmpty() && uiState.upcomingTasks.isEmpty() && uiState.overdueTasks.isEmpty()) {
            EmptyState(
                message = "No tasks scheduled.\nTap + to add a task with a due date!",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Overdue section
                if (uiState.overdueTasks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Overdue",
                            color = MaterialTheme.colorScheme.error,
                            count = uiState.overdueTasks.size
                        )
                    }
                    items(uiState.overdueTasks, key = { it.task.id }) { task ->
                        TaskItem(
                            task = task,
                            onComplete = {
                                val newStatus = if (task.task.status == TaskStatus.DONE.name)
                                    TaskStatus.TODO else TaskStatus.DONE
                                viewModel.updateTaskStatus(task, newStatus)
                            },
                            onDelete = { viewModel.deleteTask(task) },
                            onEdit = { onEditTask(task.task.id) }
                        )
                    }
                }

                // Today section
                if (uiState.todayTasks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Today",
                            color = MaterialTheme.colorScheme.primary,
                            count = uiState.todayTasks.size
                        )
                    }
                    items(uiState.todayTasks, key = { it.task.id }) { task ->
                        TaskItem(
                            task = task,
                            onComplete = {
                                val newStatus = if (task.task.status == TaskStatus.DONE.name)
                                    TaskStatus.TODO else TaskStatus.DONE
                                viewModel.updateTaskStatus(task, newStatus)
                            },
                            onDelete = { viewModel.deleteTask(task) },
                            onEdit = { onEditTask(task.task.id) }
                        )
                    }
                }

                // Upcoming section
                if (uiState.upcomingTasks.isNotEmpty()) {
                    item {
                        SectionHeader(
                            title = "Upcoming",
                            color = MaterialTheme.colorScheme.tertiary,
                            count = uiState.upcomingTasks.size
                        )
                    }
                    items(uiState.upcomingTasks, key = { it.task.id }) { task ->
                        TaskItem(
                            task = task,
                            onComplete = {
                                val newStatus = if (task.task.status == TaskStatus.DONE.name)
                                    TaskStatus.TODO else TaskStatus.DONE
                                viewModel.updateTaskStatus(task, newStatus)
                            },
                            onDelete = { viewModel.deleteTask(task) },
                            onEdit = { onEditTask(task.task.id) }
                        )
                    }
                }
            }
        }
    }

    if (uiState.showAddSheet) {
        AddTaskSheet(
            onDismiss = viewModel::hideSheet,
            onSave = { task -> viewModel.saveTask(task) },
            editingTask = uiState.editingTask,
            categories = emptyList()
        )
    }
}

@Composable
private fun SectionHeader(
    title: String,
    color: androidx.compose.ui.graphics.Color,
    count: Int
) {
    Text(
        text = "$title ($count)",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 4.dp, top = 12.dp, bottom = 4.dp)
    )
}

@Composable
private fun TaskItem(
    task: TaskWithCategory,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    SwipeableTaskCard(
        taskWithCategory = task,
        onComplete = onComplete,
        onDelete = onDelete,
        onEdit = onEdit
    )
}
