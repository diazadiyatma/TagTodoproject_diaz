package com.example.tagtodoproject

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tagtodoproject.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        lifecycleScope.launch {
            delay(2000)

            val sharedPref = getSharedPreferences("user_session", Context.MODE_PRIVATE)
            val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)
            val userId = sharedPref.getInt("user_id", -1)

            if (!isLoggedIn || userId == -1) {
                // ⛔ Belum login → ke MainActivity (Landing Page)
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            } else {
                // ✅ Sudah login → cek jumlah task
                val db = AppDatabase.getDatabase(applicationContext)
                val taskCount = withContext(Dispatchers.IO) {
                    db.taskDao().countAllTasksForUser(userId)
                }

                val intent = Intent(this@SplashActivity, MainDashboardActivity::class.java).apply {
                    putExtra("open_menu", taskCount > 0) // true kalau udah ada task
                }
                startActivity(intent)
            }
            finish()
        }
    }
}
