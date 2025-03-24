package com.example.tagtodoproject.authentication


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tagtodoproject.UserDao
import com.example.tagtodoproject.authentication.LoginActivity
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.databinding.ActivityRegisterBinding
import com.example.tagtodoproject.model.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.jvm.java

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString()
            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()
            val contact = binding.etContact.text.toString()
            val location = binding.etLocation.text.toString()
            val photoUri = "" // nanti ambil dari Image Picker jika tersedia

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty() || contact.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field!", Toast.LENGTH_SHORT).show()
            } else if (password != confirmPassword) {
                Toast.makeText(this, "Password tidak cocok!", Toast.LENGTH_SHORT).show()
            } else {
                val user = UserEntity(
                    username = username,
                    email = email,
                    password = password,
                    contact = contact,
                    location = location,
                    profilePhotoUri = photoUri
                )

                // Jalankan insert ke Room Database
                CoroutineScope(Dispatchers.IO).launch {
                    userDao.insertUser(user)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@RegisterActivity, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }
}
