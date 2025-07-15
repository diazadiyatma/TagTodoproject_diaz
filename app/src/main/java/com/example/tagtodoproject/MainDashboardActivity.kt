package com.example.tagtodoproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tagtodoproject.authentication.LoginActivity
import com.example.tagtodoproject.databinding.ActivityMainDashboardBinding
import com.example.tagtodoproject.AddGlobalTaskFragment
import com.example.tagtodoproject.HomeFragment
import com.example.tagtodoproject.ProfileFragment
import com.example.tagtodoproject.ui.menu.MenuFragment

class MainDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ Cek apakah user sudah login
        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        if (userId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        // ✅ Set layout
        binding = ActivityMainDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username") ?: "Guest"
        val openMenu = intent.getBooleanExtra("open_menu", false)

        // ✅ Fragment pertama
        if (savedInstanceState == null) {
            if (openMenu) {
                replaceFragment(MenuFragment())
            } else {
                replaceFragment(HomeFragment())
            }
        }

        // ➕ Add Task
        binding.fabAddTask.setOnClickListener {
            replaceFragment(AddGlobalTaskFragment())
        }

        // 📋 Menu
        binding.fabMenu.setOnClickListener {
            replaceFragment(MenuFragment())
        }

        // 👤 Profile
        binding.fabProfile.setOnClickListener {
            val profileFragment = ProfileFragment().apply {
                arguments = Bundle().apply {
                    putString("username", username)
                }
            }
            replaceFragment(profileFragment)
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, fragment)
            .commit()
    }
}
