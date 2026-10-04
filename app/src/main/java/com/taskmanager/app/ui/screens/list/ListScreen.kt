package com.taskmanager.app.ui.screens.list

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskmanager.app.TaskManagerApp
import com.taskmanager.app.data.local.TaskWithCategory
import com.taskmanager.app.data.model.TaskStatus
import com.taskmanager.app.data.model.toDomain
import com.taskmanager.app.ui.components.AddTaskSheet
import com.taskmanager.app.ui.components.EmptyState
import com.taskmanager.app.ui.components.SearchBar
import com.taskmanager.app.ui.components.TaskCard
import com.taskmanager.app.ui.theme.SwipeCompleteColor
import com.taskmanager.app.ui.theme.SwipeDeleteColor
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListScreen(
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit,
    viewModel: ListViewModel = viewModel(factory = ListViewModel.Factory(
        TaskManagerApp.instance.appModule.taskRepository,
        TaskManagerApp.instance.appModule.categoryRepository
    ))
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Tasks", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            snackbarHostState.showSnackbar("All tasks cleared")
                        }
                        viewModel.deleteAllTasks()
                    }) {
                        Icon(Icons.Filled.Delete, contentDescription = "Clear all")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.showAddSheet() },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add task")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            SearchBar(
                query = uiState.searchQuery,
                onQueryChange = viewModel::updateSearchQuery
            )

            // Filter chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ListFilter.entries.forEach { filter ->
                    FilterChip(
                        selected = uiState.filter == filter,
                        onClick = { viewModel.updateFilter(filter) },
                        label = { Text(filter.name.lowercase().replaceFirstChar { it.uppercase() }) }
                    )
                }
            }

            // Task list
            if (uiState.tasks.isEmpty()) {
                EmptyState(
                    message = if (uiState.searchQuery.isNotEmpty())
                        "No tasks found for \"${uiState.searchQuery}\""
                    else
                        "No tasks yet.\nTap + to add your first task!"
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    items(
                        items = uiState.tasks,
                        key = { it.task.id }
                    ) { taskWithCategory ->
                        SwipeableTaskCard(
                            taskWithCategory = taskWithCategory,
                            onComplete = {
                                val newStatus = if (taskWithCategory.task.status == TaskStatus.DONE.name)
                                    TaskStatus.TODO else TaskStatus.DONE
                                viewModel.updateTaskStatus(taskWithCategory, newStatus)
                            },
                            onDelete = { viewModel.deleteTask(taskWithCategory) },
                            onEdit = { onEditTask(taskWithCategory.task.id) }
                        )
                    }
                }
            }
        }
    }

    // Add/Edit sheet
    if (uiState.showAddSheet) {
        AddTaskSheet(
            onDismiss = viewModel::hideSheet,
            onSave = { task -> viewModel.saveTask(task) },
            editingTask = uiState.editingTask,
            categories = uiState.categories.map {
                com.taskmanager.app.data.model.Category(it.id, it.name, it.color)
            }
        )
    }
}

@Composable
fun SwipeableTaskCard(
    taskWithCategory: TaskWithCategory,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    var dragOffset by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp)
    ) {
        // Background revealed on swipe — fixed offset
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .matchParentSize()
        ) {
            if (dragOffset > 0f) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SwipeCompleteColor),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Icon(
                        Icons.Default.Check,
                        contentDescription = "Complete",
                        tint = Color.White,
                        modifier = Modifier.padding(start = 24.dp)
                    )
                    Text(
                        "Complete",
                        color = Color.White,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            } else if (dragOffset < 0f) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(SwipeDeleteColor),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        "Delete",
                        color = Color.White,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = Color.White,
                        modifier = Modifier.padding(end = 24.dp)
                    )
                }
            }
        }

        // Foreground card with swipe offset
        Box(
            modifier = Modifier
                .offset { IntOffset(dragOffset.toInt(), 0) }
                .pointerInput(Unit) {
                    detectHorizontalDragGestures(
                        onDragEnd = {
                            if (dragOffset > 200f) {
                                onComplete()
                            } else if (dragOffset < -200f) {
                                onDelete()
                            }
                            dragOffset = 0f
                        },
                        onHorizontalDrag = { _, dragAmount ->
                            dragOffset += dragAmount
                        }
                    )
                }
        ) {
            TaskCard(
                task = taskWithCategory.task.toDomain(
                    taskWithCategory.categoryName,
                    taskWithCategory.categoryColor
                ),
                onStatusChange = { newStatus ->
                    val completedAt = if (newStatus == TaskStatus.DONE)
                        System.currentTimeMillis() else null
                    onComplete()
                },
                onEdit = onEdit
            )
        }
    }
}
