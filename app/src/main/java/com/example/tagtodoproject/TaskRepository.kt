package com.example.tagtodoproject.data

import androidx.lifecycle.LiveData

class TaskRepository(private val dao: TaskDao) {

    // ✅ Insert task
    suspend fun insert(task: TaskEntity) = dao.insert(task)

    // ✅ Update task
    suspend fun update(task: TaskEntity) = dao.update(task)

    // ✅ Delete task permanently
    suspend fun delete(task: TaskEntity) = dao.delete(task)

    // ✅ Restore task (mark isDeleted = 0)
    suspend fun restoreTask(taskId: Int) = dao.restoreTask(taskId)

    // ✅ Get tasks by category
    fun getTasksByCategory(category: String, userId: Int): LiveData<List<TaskEntity>> =
        dao.getTasksByCategory(category, userId)

    // ✅ Get trashed (deleted) tasks
    fun getTrash(userId: Int): LiveData<List<TaskEntity>> =
        dao.getTrash(userId)

    // ✅ Get completed tasks
    fun getCompleted(userId: Int): LiveData<List<TaskEntity>> =
        dao.getCompleted(userId)

    // ✅ Get tasks by partial tag name
    fun getByTag(tag: String, userId: Int): LiveData<List<TaskEntity>> =
        dao.getByTag(tag, userId)

    // ✅ Get all grouped tags
    fun getAllGroupedByTags(userId: Int): LiveData<List<TaskEntity>> =
        dao.getAllGroupedByTags(userId)

    // ✅ Get all tasks for a user
    fun getAllTasksByUser(userId: Int): LiveData<List<TaskEntity>> =
        dao.getAllTasksByUser(userId)

    // ✅ Count by priority
    suspend fun countByPriorityForUser(priority: String, userId: Int): Int =
        dao.countByPriorityForUser(priority, userId)
}
