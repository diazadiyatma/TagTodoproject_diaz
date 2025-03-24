package com.example.tagtodoproject.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.tagtodoproject.UserDao
import com.example.tagtodoproject.model.UserEntity

@Database(
    entities = [UserEntity::class, TaskEntity::class],
    version = 2, // ✅ Naikkan versi DB ke 2
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
                    .fallbackToDestructiveMigration() // ✅ Tambahkan ini
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
