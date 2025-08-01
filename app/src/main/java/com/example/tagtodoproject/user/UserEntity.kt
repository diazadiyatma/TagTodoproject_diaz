package com.example.tagtodoproject.user

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val username: String,
    val email: String,
    val password: String,
    val contact: String,
    val location: String,
    val birthDate: String,
    val gender: String,
    val profilePhotoUri: String?,
    val bio: String? = null // ✅ Tambahan
)