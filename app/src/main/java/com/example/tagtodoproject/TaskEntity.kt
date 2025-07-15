package com.example.tagtodoproject.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,              // Judul task
    val date: String,              // Tanggal (format bebas: dd/MM/yyyy atau yyyy-MM-dd)
    val category: String,          // Work, School, Home, etc.
    val priority: String,          // Low, Medium, High
    val isCompleted: Boolean = false, // Ditandai sudah selesai
    val isDeleted: Boolean = false,   // Ditandai sebagai trash
    val tags: String,              // Misalnya: urgent, personal, dll
    val userId: Int = 0            // Relasi ke user
)
