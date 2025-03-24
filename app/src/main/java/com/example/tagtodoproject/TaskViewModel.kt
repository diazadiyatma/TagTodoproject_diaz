package com.example.tagtodoproject.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.data.TaskEntity
import com.example.tagtodoproject.data.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        // ✅ Ganti getInstance ➜ getDatabase
        val dao = AppDatabase.getDatabase(application).taskDao()
        repository = TaskRepository(dao)
    }

    fun insert(task: TaskEntity) = viewModelScope.launch {
        repository.insert(task)
    }

    fun update(task: TaskEntity) = viewModelScope.launch {
        repository.update(task)
    }

    fun delete(task: TaskEntity) = viewModelScope.launch {
        repository.delete(task)
    }

    fun getByCategory(category: String, userId: Int) =
        repository.getTasksByCategory(category, userId)


    fun getTrash(userId: Int) =
        repository.getTrash(userId)

    fun getCompleted(userId: Int) =
        repository.getCompleted(userId)

    fun getByTag(tag: String, userId: Int) =
        repository.getByTag(tag, userId)

    fun getGroupedByTags(userId: Int) =
        repository.getAllGroupedByTags(userId)
}
