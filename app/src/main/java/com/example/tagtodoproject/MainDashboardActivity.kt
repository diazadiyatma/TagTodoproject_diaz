package com.example.tagtodoproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tagtodoproject.authentication.LoginActivity
import com.example.tagtodoproject.databinding.ActivityMainDashboardBinding
import com.example.tagtodoproject.dashboard.AddGlobalTaskFragment
import com.example.tagtodoproject.category.HomeFragment
import com.example.tagtodoproject.dashboard.ProfileFragment
import com.example.tagtodoproject.dashboard.MenuFragment
import com.google.android.material.navigation.NavigationBarView

class MainDashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        if (userId == -1) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        binding = ActivityMainDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val username = intent.getStringExtra("username") ?: "Guest"
        val destination = intent.getStringExtra("destination") ?: "menu"

        // Set default fragment
        if (savedInstanceState == null) {
            val startFragment: Fragment = when (destination) {
                "add_task" -> AddGlobalTaskFragment()
                "menu" -> MenuFragment()
                else -> MenuFragment()
            }
            replaceFragment(startFragment)
        }

        // FAB Add Task
        binding.fabAddTask.setOnClickListener {
            replaceFragment(AddGlobalTaskFragment())
        }

        // Bottom Nav: Home & Profile only (FAB dihandle terpisah)
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(MenuFragment())
                    true
                }
                R.id.nav_profile -> {
                    val profileFragment = ProfileFragment().apply {
                        arguments = Bundle().apply {
                            putString("username", username)
                        }
                    }
                    replaceFragment(profileFragment)
                    true
                }
                else -> false
            }
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main_content, fragment)
            .commit()
    }
}
