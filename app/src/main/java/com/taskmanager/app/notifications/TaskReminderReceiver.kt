package com.taskmanager.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taskmanager.app.util.Constants

class TaskReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getLongExtra(Constants.EXTRA_TASK_ID, -1)
        val taskTitle = intent.getStringExtra(Constants.EXTRA_TASK_TITLE) ?: "Task reminder"

        if (taskId != -1L) {
            NotificationHelper.showTaskReminder(context, taskId, taskTitle)
        }
    }
}
