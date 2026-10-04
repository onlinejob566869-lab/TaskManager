package com.taskmanager.app.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.taskmanager.app.data.model.Task
import com.taskmanager.app.data.model.TaskStatus
import com.taskmanager.app.ui.theme.HighPriorityColor
import com.taskmanager.app.ui.theme.LowPriorityColor
import com.taskmanager.app.ui.theme.MediumPriorityColor
import com.taskmanager.app.util.DateUtils

@Composable
fun TaskCard(
    task: Task,
    onStatusChange: (TaskStatus) -> Unit,
    onEdit: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDone = task.status == TaskStatus.DONE
    val cardElevation by animateDpAsState(
        targetValue = if (isDone) 0.dp else 1.dp,
        label = "cardElevation"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone)
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else
                MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = cardElevation)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 8.dp, end = 12.dp, top = 6.dp, bottom = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Checkbox(
                checked = isDone,
                onCheckedChange = { isChecked ->
                    onStatusChange(if (isChecked) TaskStatus.DONE else TaskStatus.TODO)
                },
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colorScheme.primary,
                    uncheckedColor = MaterialTheme.colorScheme.outline
                ),
                modifier = Modifier.size(36.dp)
            )

            // Task content
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 4.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                // Title
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDone)
                        MaterialTheme.colorScheme.onSurfaceVariant
                    else
                        MaterialTheme.colorScheme.onSurface,
                    textDecoration = if (isDone) TextDecoration.LineThrough else TextDecoration.None,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Category + Due date row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Category chip
                    if (task.categoryName != null) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(
                                        color = Color(task.categoryColor ?: 0xFF6750A4),
                                        shape = CircleShape
                                    )
                            )
                            Text(
                                text = task.categoryName,
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Due date
                    if (task.dueDate != null) {
                        val dateText = DateUtils.formatDate(task.dueDate)
                        val isOverdue = DateUtils.isOverdue(task.dueDate) && !isDone
                        Text(
                            text = dateText,
                            style = MaterialTheme.typography.labelSmall,
                            color = if (isOverdue)
                                MaterialTheme.colorScheme.error
                            else
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        if (task.dueTime != null) {
                            Text(
                                text = "• ${DateUtils.formatTime(task.dueTime)}",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (isOverdue)
                                    MaterialTheme.colorScheme.error
                                else
                                    MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Priority indicator dot
            Spacer(modifier = Modifier.width(4.dp))
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .background(
                        color = when (task.priority) {
                            com.taskmanager.app.data.model.Priority.HIGH -> HighPriorityColor
                            com.taskmanager.app.data.model.Priority.MEDIUM -> MediumPriorityColor
                            com.taskmanager.app.data.model.Priority.LOW -> LowPriorityColor
                        },
                        shape = CircleShape
                    )
            )
        }
    }
}
