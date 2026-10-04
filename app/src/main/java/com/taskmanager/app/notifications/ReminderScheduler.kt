package com.taskmanager.app.notifications

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import com.taskmanager.app.data.local.entities.TaskEntity
import com.taskmanager.app.util.Constants

object ReminderScheduler {

    fun scheduleReminder(context: Context, task: TaskEntity) {
        if (!task.reminderEnabled || task.dueDate == null) return

        // Use dueTime if set, otherwise use dueDate
        val triggerTime = task.dueTime ?: task.dueDate
        if (triggerTime == null || triggerTime < System.currentTimeMillis()) return

        val intent = Intent(context, TaskReminderReceiver::class.java).apply {
            putExtra(Constants.EXTRA_TASK_ID, task.id)
            putExtra(Constants.EXTRA_TASK_TITLE, task.title)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            // Fallback to inexact alarm if exact alarms not allowed
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    fun cancelReminder(context: Context, taskId: Long) {
        val intent = Intent(context, TaskReminderReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            taskId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }
}
