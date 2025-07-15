package com.example.tagtodoproject.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface TaskDao {

    // 🔹 Insert task baru, replace jika ID sudah ada
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity)

    // 🔹 Update task (edit, checklist, restore, dll)
    @Update
    suspend fun update(task: TaskEntity)

    // 🔹 Delete task permanen
    @Delete
    suspend fun delete(task: TaskEntity)

    // 🔹 Ambil task aktif (tidak dihapus) berdasarkan kategori & user
    @Query("""
        SELECT * FROM tasks 
        WHERE isDeleted = 0 
        AND category = :category 
        AND userId = :userId
        ORDER BY id DESC
    """)
    fun getTasksByCategory(category: String, userId: Int): LiveData<List<TaskEntity>>

    // 🔹 Ambil task dari trash (yang isDeleted = 1)
    @Query("""
        SELECT * FROM tasks 
        WHERE isDeleted = 1 
        AND userId = :userId
        ORDER BY id DESC
    """)
    fun getTrash(userId: Int): LiveData<List<TaskEntity>>

    // 🔹 Ambil task yang sudah selesai (isCompleted = 1) dan tidak terhapus
    @Query("""
        SELECT * FROM tasks 
        WHERE isCompleted = 1 
        AND isDeleted = 0 
        AND userId = :userId
        ORDER BY id DESC
    """)
    fun getCompleted(userId: Int): LiveData<List<TaskEntity>>

    // 🔹 Ambil task berdasarkan tag tertentu (mirip fitur search tag)
    @Query("""
        SELECT * FROM tasks 
        WHERE tags LIKE '%' || :tagName || '%' 
        AND isDeleted = 0 
        AND userId = :userId
        ORDER BY id DESC
    """)
    fun getByTag(tagName: String, userId: Int): LiveData<List<TaskEntity>>

    // 🔹 Ambil semua task yang punya tag (untuk fitur TagsFragment)
    @Query("""
        SELECT * FROM tasks 
        WHERE tags IS NOT NULL 
        AND tags != '' 
        AND isDeleted = 0 
        AND userId = :userId 
        ORDER BY tags ASC
    """)
    fun getAllGroupedByTags(userId: Int): LiveData<List<TaskEntity>>

    // 🔹 Ambil semua task milik user tertentu (untuk backup, kalender, dll)
    @Query("""
        SELECT * FROM tasks 
        WHERE userId = :userId
        ORDER BY id DESC
    """)
    fun getAllTasksByUser(userId: Int): LiveData<List<TaskEntity>>

    // 🔹 Hitung task berdasarkan prioritas
    @Query("""
        SELECT COUNT(*) FROM tasks 
        WHERE priority = :priority 
        AND isDeleted = 0 
        AND userId = :userId
    """)
    suspend fun countByPriorityForUser(priority: String, userId: Int): Int

    // 🔹 Restore task dari trash (ubah isDeleted = 0)
    @Query("""
        UPDATE tasks SET isDeleted = 0 WHERE id = :taskId
    """)
    suspend fun restoreTask(taskId: Int)

    // 🔹 Soft delete task (tidak langsung hapus dari DB, hanya tandai)
    @Query("""
        UPDATE tasks SET isDeleted = 1 WHERE id = :taskId
    """)
    suspend fun softDelete(taskId: Int)

    // 🔹 Delete semua task user (misal saat logout atau reset)
    @Query("DELETE FROM tasks WHERE userId = :userId")
    suspend fun deleteAllForUser(userId: Int)
}
