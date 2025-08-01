package com.example.tagtodoproject.task

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val tags: String,
    val date: String,
    val priority: String,
    val isCompleted: Boolean = false,
    val isDeleted: Boolean = false,
    val category: String = "Work",
    val userId: Int // ✅ tambahkan ini
) : Parcelable
