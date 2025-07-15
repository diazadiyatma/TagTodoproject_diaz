package com.example.tagtodoproject.authentication

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.Toast
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

        // ✅ Toggle show password
        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            } else {
                binding.etPassword.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
            }
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }

        // 🔒 Login logic
        binding.btnLogin.setOnClickListener {
            val input = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                val user = withContext(Dispatchers.IO) {
                    db.userDao().login(input, password)
                }

                if (user != null) {
                    // ✅ Simpan sesi login
                    sharedPref.edit()
                        .putInt("user_id", user.id)
                        .putString("email", user.email)
                        .putBoolean("isRegistered", true)
                        .apply()

                    Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()

                    // ✅ Navigasi ke dashboard
                    startActivity(Intent(this@LoginActivity, MainDashboardActivity::class.java).apply {
                        putExtra("open_menu", true)
                    })
                    finish()
                } else {
                    Toast.makeText(this@LoginActivity, "Username/email atau password salah!", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.tvCreateAccount.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
