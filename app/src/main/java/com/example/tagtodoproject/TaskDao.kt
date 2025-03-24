package com.example.tagtodoproject.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    @Update
    suspend fun update(task: TaskEntity)

    @Delete
    suspend fun delete(task: TaskEntity)

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND category = :category AND isCompleted = 0 AND userId = :userId")
    fun getTasksByCategory(category: String, userId: Int): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 1 AND userId = :userId")
    fun getTrash(userId: Int): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1 AND isDeleted = 0 AND userId = :userId")
    fun getCompleted(userId: Int): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE tags = :tagName AND isDeleted = 0 AND userId = :userId")
    fun getByTag(tagName: String, userId: Int): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND userId = :userId ORDER BY tags ASC")
    fun getAllGroupedByTags(userId: Int): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE userId = :userId")
    fun getAllTasksByUser(userId: Int): LiveData<List<TaskEntity>>
}
