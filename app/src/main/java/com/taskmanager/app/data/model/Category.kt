package com.taskmanager.app.data.model

import com.taskmanager.app.data.local.entities.CategoryEntity

data class Category(
    val id: Long = 0,
    val name: String,
    val color: Long = 0xFF6750A4
)

fun CategoryEntity.toDomain(): Category = Category(id, name, color)
fun Category.toEntity(): CategoryEntity = CategoryEntity(id, name, color)
