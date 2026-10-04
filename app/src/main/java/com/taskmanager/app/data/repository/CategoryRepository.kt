package com.taskmanager.app.data.repository

import com.taskmanager.app.data.local.CategoryDao
import com.taskmanager.app.data.local.entities.CategoryEntity
import kotlinx.coroutines.flow.Flow

class CategoryRepository(private val categoryDao: CategoryDao) {

    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()

    suspend fun getCategoryById(id: Long): CategoryEntity? = categoryDao.getCategoryById(id)

    suspend fun insertCategory(category: CategoryEntity): Long =
        categoryDao.insertCategory(category)

    suspend fun updateCategory(category: CategoryEntity) = categoryDao.updateCategory(category)

    suspend fun deleteCategory(category: CategoryEntity) = categoryDao.deleteCategory(category)

    suspend fun deleteCategoryById(id: Long) = categoryDao.deleteCategoryById(id)

    suspend fun getTaskCountForCategory(categoryId: Long): Int =
        categoryDao.getTaskCountForCategory(categoryId)
}
