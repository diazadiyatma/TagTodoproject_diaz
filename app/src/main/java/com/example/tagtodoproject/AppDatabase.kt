package com.example.tagtodoproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tagtodoproject.user.UserDao
import com.example.tagtodoproject.user.UserEntity
import com.example.tagtodoproject.task.TaskDao
import com.example.tagtodoproject.task.TaskEntity

@Database(
    entities = [UserEntity::class, TaskEntity::class],
    version = 3, // ✅ Versi DB dinaikkan untuk field 'priority'
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun taskDao(): TaskDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tagtodo_database"
                )
                    .fallbackToDestructiveMigration() // ⚠️ Gunakan hanya untuk development
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
