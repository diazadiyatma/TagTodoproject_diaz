package com.example.tagtodoproject.task

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.task.TaskRepository
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaskRepository

    init {
        val dao = AppDatabase.Companion.getDatabase(application).taskDao()
        repository = TaskRepository(dao)
    }

    // ✅ Insert task
    fun insert(task: TaskEntity) = viewModelScope.launch {
        repository.insert(task)
    }

    // ✅ Update task
    fun update(task: TaskEntity) = viewModelScope.launch {
        repository.update(task)
    }

    // ✅ Delete task permanently
    fun delete(task: TaskEntity) = viewModelScope.launch {
        repository.delete(task)
    }

    // ✅ Soft delete (move to trash)
    fun softDelete(taskId: Int) = viewModelScope.launch {
        repository.softDelete(taskId)
    }

    // ✅ Restore task from trash
    fun restore(taskId: Int) = viewModelScope.launch {
        repository.restoreTask(taskId)
    }

    // ✅ Delete all task for specific user
    fun deleteAllForUser(userId: Int) = viewModelScope.launch {
        repository.deleteAllForUser(userId)
    }

    // ✅ Get tasks by category
    fun getByCategory(category: String, userId: Int) =
        repository.getTasksByCategory(category, userId)

    // ✅ Get trashed (deleted) tasks
    fun getTrash(userId: Int) =
        repository.getTrash(userId)

    // ✅ Get completed tasks
    fun getCompleted(userId: Int) =
        repository.getCompleted(userId)

    // ✅ Get tasks by tag name
    fun getByTag(tag: String, userId: Int) =
        repository.getByTag(tag, userId)

    // ✅ Get all tags grouped
    fun getGroupedByTags(userId: Int) =
        repository.getAllGroupedByTags(userId)

    // ✅ Get all tasks for a user
    fun getAllTasks(userId: Int) =
        repository.getAllTasksByUser(userId)

    // ✅ Count by priority (result via callback)
    fun countByPriority(priority: String, userId: Int, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val result = repository.countByPriorityForUser(priority, userId)
            onResult(result)
        }
    }

    // ✅ Count all tasks (for statistics, etc)
    fun countAllTasks(userId: Int, onResult: (Int) -> Unit) {
        viewModelScope.launch {
            val result = repository.countAllTasksForUser(userId)
            onResult(result)
        }
    }

    // ✅ Get today’s tasks (custom use)
    fun getTodayTasks(userId: Int, onResult: (List<TaskEntity>) -> Unit) {
        viewModelScope.launch {
            val result = repository.getTodayTasks(userId)
            onResult(result)
        }
    }
}