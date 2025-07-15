package com.example.tagtodoproject.authentication

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.tagtodoproject.UserDao
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.databinding.ActivityRegisterBinding
import com.example.tagtodoproject.model.UserEntity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: AppDatabase
    private lateinit var userDao: UserDao

    private var selectedBirthDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        db = AppDatabase.getDatabase(this)
        userDao = db.userDao()

        // Setup Dropdown Gender
        val genderOptions = listOf("Male", "Female")
        val genderAdapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.etGender.setAdapter(genderAdapter)
        binding.etGender.setOnClickListener {
            binding.etGender.showDropDown()
        }

        // Material Date Picker untuk Tanggal Lahir
        binding.etBirthDate.setOnClickListener {
            showMaterialDatePicker()
        }

        // ✅ Show/Hide Password Checkbox
        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            val passwordType = if (isChecked)
                InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else
                InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            binding.etPassword.inputType = passwordType
            binding.etConfirmPassword.inputType = passwordType

            // Supaya cursor tetap di akhir
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.text?.length ?: 0)
        }

        // Tombol Register
        binding.btnRegister.setOnClickListener {
            val username = binding.etUsername.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()
            val confirmPassword = binding.etConfirmPassword.text.toString().trim()
            val contact = binding.etContact.text.toString().trim()
            val location = binding.etLocation.text.toString().trim()
            val gender = binding.etGender.text.toString().trim()
            val photoUri = "" // placeholder, update di ProfileFragment

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() ||
                confirmPassword.isEmpty() || contact.isEmpty() || location.isEmpty() ||
                selectedBirthDate.isEmpty() || gender.isEmpty()
            ) {
                Toast.makeText(this, "Harap isi semua field!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Password tidak cocok!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val user = UserEntity(
                username = username,
                email = email,
                password = password,
                contact = contact,
                location = location,
                birthDate = selectedBirthDate,
                gender = gender,
                profilePhotoUri = photoUri,
                bio = "" // default kosong
            )

            CoroutineScope(Dispatchers.IO).launch {
                userDao.insertUser(user)

                withContext(Dispatchers.Main) {
                    val sharedPref = getSharedPreferences("user_session", MODE_PRIVATE)
                    sharedPref.edit().clear().apply()

                    Toast.makeText(
                        this@RegisterActivity,
                        "Registrasi berhasil! Silakan login.",
                        Toast.LENGTH_SHORT
                    ).show()

                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun showMaterialDatePicker() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointBackward.now()) // hanya tanggal masa lalu
            .build()

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Pilih Tanggal Lahir")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraints)
            .build()

        datePicker.show(supportFragmentManager, "birth_date_picker")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            selectedBirthDate = sdf.format(Date(selection))
            binding.etBirthDate.setText(selectedBirthDate)
        }
    }
}
