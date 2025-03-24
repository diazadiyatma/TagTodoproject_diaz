package com.example.tagtodoproject.model

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
    val profilePhotoUri: String? = null // bisa disimpan sebagai path/file uri
)
