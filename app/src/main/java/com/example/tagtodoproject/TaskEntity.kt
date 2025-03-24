package com.example.tagtodoproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val date: String,
    val category: String,
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false,
    val tags: String,
    val userId: Int = 0 // Wajib diisi saat buat task, untuk asosiasi ke user tertentu
)
