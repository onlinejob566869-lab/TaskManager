package com.taskmanager.app

import android.app.Application
import com.taskmanager.app.data.local.TaskDatabase
import com.taskmanager.app.data.repository.CategoryRepository
import com.taskmanager.app.data.repository.TaskRepository

class TaskManagerApp : Application() {

    lateinit var appModule: AppModule

    override fun onCreate() {
        super.onCreate()
        instance = this
        val database = TaskDatabase.getDatabase(this)
        appModule = AppModule(
            taskRepository = TaskRepository(database.taskDao()),
            categoryRepository = CategoryRepository(database.categoryDao())
        )
    }

    companion object {
        lateinit var instance: TaskManagerApp
            private set
    }
}

data class AppModule(
    val taskRepository: TaskRepository,
    val categoryRepository: CategoryRepository
)
