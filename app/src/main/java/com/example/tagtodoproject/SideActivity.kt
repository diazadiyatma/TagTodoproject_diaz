package com.example.tagtodoproject

import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView

class SideActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var toolbar: Toolbar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categoryside)

        drawerLayout = findViewById(R.id.drawer_layout)
        navView = findViewById(R.id.nav_view)
        toolbar = findViewById(R.id.toolbar)

        setSupportActionBar(toolbar)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navView.setNavigationItemSelectedListener(this)

        // Cek apakah dari LoginActivity mengirim intent untuk buka langsung ke User Profile
        val openProfile = intent.getBooleanExtra("open_profile", false)

        if (savedInstanceState == null) {
            if (openProfile) {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, ProfileFragment())
                    .commit()
                toolbar.title = "User Profile"
                navView.setCheckedItem(R.id.nav_user_profile)
            } else {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, HomeFragment())
                    .commit()
                toolbar.title = "Home"
                navView.setCheckedItem(R.id.nav_home)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_user_profile -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, ProfileFragment())
                    .commit()
                toolbar.title = "TAG TODO"
            }

            R.id.nav_home -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, HomeFragment())
                    .commit()
                toolbar.title = "TAG TODO"
            }

            R.id.nav_work -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, WorkFragment())
                    .commit()
                toolbar.title = "TAG TODO"
            }

            R.id.nav_school -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, SchoolFragment())
                    .commit()
                toolbar.title = "TAG TODO"
            }

            R.id.nav_excercise -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, ExcerciseFragment())
                    .commit()
                toolbar.title = "TAG TODO"
            }

            R.id.nav_finance -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, FinanceFragment())
                    .commit()
                toolbar.title = "TAG TODO"
            }

            R.id.nav_tags -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, TagsFragment())
                    .commit()
                toolbar.title = "TAG TODO"
            }

            R.id.nav_trash -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, TrashFragment())
                    .commit()
                toolbar.title = "TAG TODO"
            }

            R.id.nav_completed -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, CompletedFragment())
                    .commit()
                toolbar.title = "TAG TODO"
            }

            R.id.nav_calendar -> {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.main_content, CalendarFragment())
                    .commit()
                toolbar.title = "TAG TODO"
            }

            else -> {
                Toast.makeText(this, "Menu not found", Toast.LENGTH_SHORT).show()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun openDrawer() {
        drawerLayout.openDrawer(GravityCompat.START)
    }
}
