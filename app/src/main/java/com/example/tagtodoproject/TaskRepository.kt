package com.example.tagtodoproject.data

import androidx.lifecycle.LiveData

class TaskRepository(private val dao: TaskDao) {

    suspend fun insert(task: TaskEntity) = dao.insert(task)

    suspend fun update(task: TaskEntity) = dao.update(task)

    suspend fun delete(task: TaskEntity) = dao.delete(task)

    fun getTasksByCategory(category: String, userId: Int): LiveData<List<TaskEntity>> =
        dao.getTasksByCategory(category, userId)

    fun getTrash(userId: Int): LiveData<List<TaskEntity>> =
        dao.getTrash(userId)

    fun getCompleted(userId: Int): LiveData<List<TaskEntity>> =
        dao.getCompleted(userId)

    fun getByTag(tag: String, userId: Int): LiveData<List<TaskEntity>> =
        dao.getByTag(tag, userId)

    fun getAllGroupedByTags(userId: Int): LiveData<List<TaskEntity>> =
        dao.getAllGroupedByTags(userId)

    fun getAllTasksByUser(userId: Int): LiveData<List<TaskEntity>> =
        dao.getAllTasksByUser(userId)
}
