package com.example.tagtodoproject
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.tagtodoproject.model.UserEntity

@Dao
interface UserDao {

    // Insert user baru saat registrasi
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserEntity)

    // Ambil user berdasarkan email
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    // Ambil user berdasarkan username
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    // Validasi login berdasarkan username/email dan password
    @Query("SELECT * FROM users WHERE (username = :input OR email = :input) AND password = :password LIMIT 1")
    suspend fun login(input: String, password: String): UserEntity?

    @Query("SELECT * FROM users ORDER BY id DESC LIMIT 1")
    suspend fun getLatestUser(): UserEntity?

}
