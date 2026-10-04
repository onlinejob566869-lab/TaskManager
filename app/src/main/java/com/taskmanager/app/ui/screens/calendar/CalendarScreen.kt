package com.taskmanager.app.ui.screens.calendar

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.taskmanager.app.TaskManagerApp
import com.taskmanager.app.data.local.TaskWithCategory
import com.taskmanager.app.data.model.TaskStatus
import com.taskmanager.app.data.model.toDomain
import com.taskmanager.app.ui.components.EmptyState
import com.taskmanager.app.ui.components.TaskCard
import com.taskmanager.app.util.DateUtils
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalendarScreen(
    onEditTask: (Long) -> Unit,
    viewModel: CalendarViewModel = viewModel(factory = CalendarViewModel.Factory(
        TaskManagerApp.instance.appModule.taskRepository
    ))
) {
    val uiState by viewModel.uiState.collectAsState()
    val cal = remember { Calendar.getInstance() }
    var displayMonth by remember { mutableStateOf(cal.get(Calendar.MONTH)) }
    var displayYear by remember { mutableStateOf(cal.get(Calendar.YEAR)) }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }

    val todayCal = Calendar.getInstance()
    val todayDay = todayCal.get(Calendar.DAY_OF_MONTH)
    val todayMonth = todayCal.get(Calendar.MONTH)
    val todayYear = todayCal.get(Calendar.YEAR)

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Calendar", style = MaterialTheme.typography.titleLarge) })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Month navigation
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (displayMonth == 0) {
                        displayMonth = 11
                        displayYear--
                    } else displayMonth--
                }) {
                    Icon(Icons.Filled.KeyboardArrowLeft, contentDescription = "Previous month")
                }
                Text(
                    text = "${DateUtils.getMonthName(displayMonth)} $displayYear",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                IconButton(onClick = {
                    if (displayMonth == 11) {
                        displayMonth = 0
                        displayYear++
                    } else displayMonth++
                }) {
                    Icon(Icons.Filled.KeyboardArrowRight, contentDescription = "Next month")
                }
            }

            // Day headers
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("S", "M", "T", "W", "T", "F", "S").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            // Calendar grid
            val daysInMonth = DateUtils.getDaysInMonth(displayYear, displayMonth)
            val firstDayOffset = DateUtils.getFirstDayOfWeek(displayYear, displayMonth)
            val selectedCal = Calendar.getInstance().apply { timeInMillis = selectedDate }
            val selectedDay = selectedCal.get(Calendar.DAY_OF_MONTH)
            val selectedMonth = selectedCal.get(Calendar.MONTH)
            val selectedYear = selectedCal.get(Calendar.YEAR)

            val totalCells = firstDayOffset + daysInMonth
            val rows = (totalCells + 6) / 7

            Column(modifier = Modifier.padding(horizontal = 8.dp)) {
                for (row in 0 until rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        for (col in 0 until 7) {
                            val cellIndex = row * 7 + col
                            val day = cellIndex - firstDayOffset + 1

                            if (day in 1..daysInMonth) {
                                val isToday = day == todayDay && displayMonth == todayMonth && displayYear == todayYear
                                val isSelected = day == selectedDay && displayMonth == selectedMonth && displayYear == selectedYear

                                // Check if tasks exist for this day
                                val dateCal = Calendar.getInstance().apply {
                                    set(displayYear, displayMonth, day, 12, 0, 0)
                                }
                                val dateMillis = dateCal.timeInMillis
                                val hasTasks = uiState.tasks.any { task ->
                                    task.task.dueDate != null &&
                                    DateUtils.getStartOfDay(task.task.dueDate) == DateUtils.getStartOfDay(dateMillis)
                                }

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .aspectRatio(1f)
                                        .clickable {
                                            selectedDate = dateMillis
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(36.dp)
                                                .background(
                                                    color = if (isSelected) MaterialTheme.colorScheme.primary
                                                    else if (isToday) MaterialTheme.colorScheme.primaryContainer
                                                    else Color.Transparent,
                                                    shape = CircleShape
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = day.toString(),
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                else if (isToday) MaterialTheme.colorScheme.onPrimaryContainer
                                                else MaterialTheme.colorScheme.onSurface,
                                                fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal
                                            )
                                        }
                                        // Task indicator dot
                                        if (hasTasks) {
                                            Box(
                                                modifier = Modifier
                                                    .size(4.dp)
                                                    .background(
                                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimary
                                                        else MaterialTheme.colorScheme.primary,
                                                        shape = CircleShape
                                                    )
                                            )
                                        }
                                    }
                                }
                            } else {
                                Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                            }
                        }
                    }
                }
            }

            // Selected day's tasks
            val tasksForSelectedDate = uiState.tasks.filter {
                it.task.dueDate != null &&
                DateUtils.getStartOfDay(it.task.dueDate) == DateUtils.getStartOfDay(selectedDate)
            }

            Text(
                text = "Tasks for ${DateUtils.formatDate(selectedDate)}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
            )

            if (tasksForSelectedDate.isEmpty()) {
                EmptyState(message = "No tasks for this date")
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    items(tasksForSelectedDate, key = { it.task.id }) { task ->
                        TaskCard(
                            task = task.task.toDomain(task.categoryName, task.categoryColor),
                            onStatusChange = { },
                            onEdit = { onEditTask(task.task.id) }
                        )
                    }
                }
            }
        }
    }
}
