package com.taskmanager.app.notifications

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.taskmanager.app.TaskManagerApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            // Re-schedule all task reminders after device reboot
            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val app = context.applicationContext as? TaskManagerApp ?: return@launch
                    val tasks = app.appModule.taskRepository.getTasksWithReminders()
                    tasks.forEach { task ->
                        ReminderScheduler.scheduleReminder(context, task)
                    }
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }
}
