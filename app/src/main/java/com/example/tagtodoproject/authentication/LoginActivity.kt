package com.example.tagtodoproject.authentication

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tagtodoproject.SideActivity
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

        binding.btnLogin.setOnClickListener {
            val input = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua field!", Toast.LENGTH_SHORT).show()
            } else {
                lifecycleScope.launch {
                    val user = withContext(Dispatchers.IO) {
                        db.userDao().login(input, password)
                    }

                    if (user != null) {
                        // ✅ Simpan userId ke SharedPreferences
                        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
                        sharedPref.edit().putInt("user_id", user.id).putString("email", user.email).apply()

                        Toast.makeText(this@LoginActivity, "Login berhasil!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, SideActivity::class.java)
                        intent.putExtra("open_profile", true)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Username/email atau password salah!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        binding.tvRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }
}
