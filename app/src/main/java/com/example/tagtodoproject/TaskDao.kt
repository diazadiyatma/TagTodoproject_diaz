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

    @Query("""
        SELECT * FROM tasks 
        WHERE isDeleted = 0 
        AND category = :category 
        AND userId = :userId
        ORDER BY id DESC
    """)
    fun getTasksByCategory(category: String, userId: Int): LiveData<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks 
        WHERE isDeleted = 1 
        AND userId = :userId
        ORDER BY id DESC
    """)
    fun getTrash(userId: Int): LiveData<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks 
        WHERE isCompleted = 1 
        AND isDeleted = 0 
        AND userId = :userId
        ORDER BY id DESC
    """)
    fun getCompleted(userId: Int): LiveData<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks 
        WHERE tags LIKE '%' || :tagName || '%' 
        AND isDeleted = 0 
        AND userId = :userId
        ORDER BY id DESC
    """)
    fun getByTag(tagName: String, userId: Int): LiveData<List<TaskEntity>>

    @Query("""
        SELECT * FROM tasks 
        WHERE tags IS NOT NULL 
        AND tags != '' 
        AND isDeleted = 0 
        AND userId = :userId 
        ORDER BY tags ASC
    """)
    fun getAllGroupedByTags(userId: Int): LiveData<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND userId = :userId")
    fun getAllTasksByUser(userId: Int): LiveData<List<TaskEntity>>

    @Query("SELECT COUNT(*) FROM tasks WHERE userId = :userId AND isDeleted = 0")
    suspend fun countAllTasksForUser(userId: Int): Int

    @Query("""
        SELECT COUNT(*) FROM tasks 
        WHERE priority = :priority 
        AND isDeleted = 0 
        AND userId = :userId
    """)
    suspend fun countByPriorityForUser(priority: String, userId: Int): Int

    @Query("UPDATE tasks SET isDeleted = 0 WHERE id = :taskId")
    suspend fun restoreTask(taskId: Int)

    @Query("UPDATE tasks SET isDeleted = 1 WHERE id = :taskId")
    suspend fun softDelete(taskId: Int)

    @Query("DELETE FROM tasks WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Int)

    @Query("""
        SELECT * FROM tasks 
        WHERE date = :selectedDate 
        AND isDeleted = 0 
        AND userId = :userId
        ORDER BY id DESC
    """)
    suspend fun getTasksByDate(selectedDate: String, userId: Int): List<TaskEntity>

    @Query("""
        SELECT DISTINCT date FROM tasks 
        WHERE isDeleted = 0 
        AND userId = :userId
    """)
    suspend fun getAllTaskDates(userId: Int): List<String>

    @Query("SELECT * FROM tasks WHERE isDeleted = 0 AND userId = :userId")
    suspend fun getAllTasksByUserNow(userId: Int): List<TaskEntity>

    @Query("SELECT * FROM tasks WHERE date = :todayDate AND userId = :userId AND isDeleted = 0")
    suspend fun getTodayTasksForUser(todayDate: String, userId: Int): List<TaskEntity>
}
