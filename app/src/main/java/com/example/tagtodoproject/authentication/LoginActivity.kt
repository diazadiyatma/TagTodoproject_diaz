package com.example.tagtodoproject.authentication

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tagtodoproject.MainDashboardActivity
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.databinding.ActivityLoginBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)

        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            binding.etPassword.inputType = if (isChecked)
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }

        binding.btnLogin.setOnClickListener {
            val input = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            var isValid = true

            if (input.isEmpty()) {
                binding.etEmail.error = "Email tidak boleh kosong"
                isValid = false
            } else {
                binding.etEmail.error = null
            }

            if (password.isEmpty()) {
                binding.etPassword.error = "Password tidak boleh kosong"
                isValid = false
            } else {
                binding.etPassword.error = null
            }

            if (!isValid) return@setOnClickListener

            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) {
                    db.userDao().login(input, password)
                }

                if (user != null) {
                    sharedPref.edit()
                        .putInt("user_id", user.id)
                        .putString("email", user.email)
                        .putBoolean("isLoggedIn", true)
                        .apply()

                    val taskCount = withContext(Dispatchers.IO) {
                        db.taskDao().countAllTasksForUser(user.id)
                    }

                    startActivity(Intent(this@LoginActivity, MainDashboardActivity::class.java).apply {
                        putExtra("open_menu", taskCount > 0)
                    })
                    finish()
                } else {
                    binding.etEmail.error = "Email atau password salah"
                    binding.etPassword.error = "Email atau password salah"
                }
            }
        }

        binding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
