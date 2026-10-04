package com.taskmanager.app.ui.screens.board

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskmanager.app.TaskManagerApp
import com.taskmanager.app.data.local.TaskWithCategory
import com.taskmanager.app.data.model.TaskStatus
import com.taskmanager.app.data.model.toDomain
import com.taskmanager.app.ui.components.AddTaskSheet
import com.taskmanager.app.ui.components.EmptyState
import com.taskmanager.app.ui.theme.HighPriorityColor
import com.taskmanager.app.ui.theme.LowPriorityColor
import com.taskmanager.app.ui.theme.MediumPriorityColor
import com.taskmanager.app.util.DateUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BoardScreen(
    onAddTask: () -> Unit,
    onEditTask: (Long) -> Unit,
    viewModel: BoardViewModel = viewModel(factory = BoardViewModel.Factory(
        TaskManagerApp.instance.appModule.taskRepository
    ))
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Board", style = MaterialTheme.typography.titleLarge) })
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
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 8.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BoardColumn(
                title = "To Do",
                tasks = uiState.todoTasks,
                columnColor = MaterialTheme.colorScheme.tertiary,
                onMoveRight = { viewModel.moveTask(it, TaskStatus.IN_PROGRESS) },
                onDelete = { viewModel.deleteTask(it) },
                onEdit = { onEditTask(it.task.id) },
                modifier = Modifier.width(280.dp)
            )
            BoardColumn(
                title = "In Progress",
                tasks = uiState.inProgressTasks,
                columnColor = MaterialTheme.colorScheme.primary,
                onMoveRight = { viewModel.moveTask(it, TaskStatus.DONE) },
                onMoveLeft = { viewModel.moveTask(it, TaskStatus.TODO) },
                onDelete = { viewModel.deleteTask(it) },
                onEdit = { onEditTask(it.task.id) },
                modifier = Modifier.width(280.dp)
            )
            BoardColumn(
                title = "Done",
                tasks = uiState.doneTasks,
                columnColor = Color(0xFF43A047),
                onMoveRight = { },
                onMoveLeft = { viewModel.moveTask(it, TaskStatus.IN_PROGRESS) },
                onDelete = { viewModel.deleteTask(it) },
                onEdit = { onEditTask(it.task.id) },
                modifier = Modifier.width(280.dp)
            )
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
private fun BoardColumn(
    title: String,
    tasks: List<TaskWithCategory>,
    columnColor: Color,
    onMoveRight: (TaskWithCategory) -> Unit,
    onMoveLeft: (TaskWithCategory) -> Unit = {},
    onDelete: (TaskWithCategory) -> Unit,
    onEdit: (TaskWithCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Column header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(columnColor, CircleShape)
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "(${tasks.size})",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Task cards
            if (tasks.isEmpty()) {
                Text(
                    text = "No tasks",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                    modifier = Modifier.padding(top = 16.dp)
                )
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    items(tasks, key = { it.task.id }) { task ->
                        KanbanCard(
                            task = task,
                            onMoveRight = { onMoveRight(task) },
                            onMoveLeft = { onMoveLeft(task) },
                            onDelete = { onDelete(task) },
                            onEdit = { onEdit(task) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun KanbanCard(
    task: TaskWithCategory,
    onMoveRight: () -> Unit,
    onMoveLeft: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit
) {
    val domainTask = task.task.toDomain(task.categoryName, task.categoryColor)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() },
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Priority bar + title
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(
                            when (domainTask.priority) {
                                com.taskmanager.app.data.model.Priority.HIGH -> HighPriorityColor
                                com.taskmanager.app.data.model.Priority.MEDIUM -> MediumPriorityColor
                                com.taskmanager.app.data.model.Priority.LOW -> LowPriorityColor
                            },
                            CircleShape
                        )
                )
                Text(
                    text = domainTask.title,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            // Category + Due date
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (domainTask.categoryName != null) {
                    Text(
                        text = domainTask.categoryName,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (domainTask.dueDate != null) {
                    Text(
                        text = DateUtils.formatDate(domainTask.dueDate),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (DateUtils.isOverdue(domainTask.dueDate))
                            MaterialTheme.colorScheme.error
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Action buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "← Left",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onMoveLeft() }
                        .padding(4.dp)
                )
                Text(
                    text = "Right →",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .clickable { onMoveRight() }
                        .padding(4.dp)
                )
                Text(
                    text = "🗑",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier
                        .clickable { onDelete() }
                        .padding(4.dp)
                )
            }
        }
    }
}
