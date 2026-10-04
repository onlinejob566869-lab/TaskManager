package com.taskmanager.app.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.taskmanager.app.data.model.Priority
import com.taskmanager.app.data.model.Task
import com.taskmanager.app.util.DateUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTaskSheet(
    onDismiss: () -> Unit,
    onSave: (Task) -> Unit,
    editingTask: Task? = null,
    categories: List<com.taskmanager.app.data.model.Category> = emptyList()
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var title by remember { mutableStateOf(editingTask?.title ?: "") }
    var description by remember { mutableStateOf(editingTask?.description ?: "") }
    var selectedPriority by remember { mutableStateOf(editingTask?.priority ?: Priority.MEDIUM) }
    var selectedCategoryId by remember { mutableStateOf(editingTask?.categoryId) }
    var dueDate by remember { mutableStateOf(editingTask?.dueDate) }
    var dueTime by remember { mutableStateOf(editingTask?.dueTime) }
    var reminderEnabled by remember { mutableStateOf(editingTask?.reminderEnabled ?: false) }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Text(
                text = if (editingTask != null) "Edit Task" else "New Task",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            // Title field
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Task title") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = title.isBlank(),
                supportingText = if (title.isBlank()) {
                    { Text("Title is required") }
                } else null
            )

            // Description field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description (optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4
            )

            // Priority selector
            Text(
                text = "Priority",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Priority.entries.forEach { priority ->
                    FilterChip(
                        selected = selectedPriority == priority,
                        onClick = { selectedPriority = priority },
                        label = { Text(priority.name.lowercase().replaceFirstChar { it.uppercase() }) },
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Category selector
            if (categories.isNotEmpty()) {
                Text(
                    text = "Category",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    categories.take(4).forEach { category ->
                        FilterChip(
                            selected = selectedCategoryId == category.id,
                            onClick = {
                                selectedCategoryId = if (selectedCategoryId == category.id) null else category.id
                            },
                            label = { Text(category.name) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }

            // Due date & time row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (dueDate != null) DateUtils.formatDate(dueDate) else "Due date")
                }
                OutlinedButton(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f),
                    enabled = dueDate != null
                ) {
                    Text(if (dueTime != null) DateUtils.formatTime(dueTime) else "Due time")
                }
            }

            // Reminder toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Reminder",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Switch(
                    checked = reminderEnabled,
                    onCheckedChange = { reminderEnabled = it }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel")
                }
                Button(
                    onClick = {
                        if (title.isNotBlank()) {
                            val now = System.currentTimeMillis()
                            onSave(
                                Task(
                                    id = editingTask?.id ?: 0,
                                    title = title.trim(),
                                    description = description.trim(),
                                    categoryId = selectedCategoryId,
                                    status = editingTask?.status ?: com.taskmanager.app.data.model.TaskStatus.TODO,
                                    priority = selectedPriority,
                                    dueDate = dueDate,
                                    dueTime = dueTime,
                                    reminderEnabled = reminderEnabled,
                                    createdAt = editingTask?.createdAt ?: now,
                                    updatedAt = now,
                                    completedAt = editingTask?.completedAt
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = title.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(if (editingTask != null) "Update" else "Save")
                }
            }
        }
    }

    // Date Picker
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = dueDate
        )
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let {
                            dueDate = DateUtils.getStartOfDay(it)
                        }
                        showDatePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Time Picker — custom dialog with TimePicker
    if (showTimePicker) {
        val cal = Calendar.getInstance()
        if (dueTime != null) cal.timeInMillis = dueTime!!
        val timePickerState = rememberTimePickerState(
            initialHour = cal.get(Calendar.HOUR_OF_DAY),
            initialMinute = cal.get(Calendar.MINUTE),
            is24Hour = false
        )
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val timeCal = Calendar.getInstance().apply {
                            set(Calendar.HOUR_OF_DAY, timePickerState.hour)
                            set(Calendar.MINUTE, timePickerState.minute)
                            set(Calendar.SECOND, 0)
                            set(Calendar.MILLISECOND, 0)
                        }
                        dueTime = timeCal.timeInMillis
                        showTimePicker = false
                    }
                ) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }
}
