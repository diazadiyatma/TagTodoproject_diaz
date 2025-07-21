package com.example.tagtodoproject.authentication

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tagtodoproject.UserDao
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.databinding.ActivityRegisterBinding
import com.example.tagtodoproject.model.UserEntity
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointBackward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var userDao: UserDao
    private var selectedBirthDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userDao = AppDatabase.getDatabase(this).userDao()

        setupGenderDropdown()
        setupPasswordToggle()
        setupDatePicker()
        setupListeners()
    }

    private fun setupGenderDropdown() {
        val genderOptions = listOf("Male", "Female")
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, genderOptions)
        binding.etGender.setAdapter(adapter)
        binding.etGender.setOnClickListener { binding.etGender.showDropDown() }
    }

    private fun setupPasswordToggle() {
        binding.cbShowPassword.setOnCheckedChangeListener { _, isChecked ->
            val inputType = if (isChecked) InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            else InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD

            binding.etPassword.inputType = inputType
            binding.etConfirmPassword.inputType = inputType
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.text?.length ?: 0)
        }
    }

    private fun setupDatePicker() {
        binding.etBirthDate.setOnClickListener {
            val constraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointBackward.now())
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

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            val user = collectUserInput() ?: return@setOnClickListener
            registerUser(user)
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun collectUserInput(): UserEntity? {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val contact = binding.etContact.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val gender = binding.etGender.text.toString().trim()

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()
            || contact.isEmpty() || location.isEmpty() || selectedBirthDate.isEmpty() || gender.isEmpty()
        ) {
            Toast.makeText(this, "Harap isi semua field!", Toast.LENGTH_SHORT).show()
            return null
        }

        if (password != confirmPassword) {
            Toast.makeText(this, "Password tidak cocok!", Toast.LENGTH_SHORT).show()
            return null
        }

        return UserEntity(
            username = username,
            email = email,
            password = password,
            contact = contact,
            location = location,
            birthDate = selectedBirthDate,
            gender = gender,
            profilePhotoUri = "",
            bio = ""
        )
    }

    private fun registerUser(user: UserEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            val existingUser = userDao.getUserByEmail(user.email)
            if (existingUser != null) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Email sudah terdaftar!", Toast.LENGTH_SHORT).show()
                }
            } else {
                userDao.insertUser(user)
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@RegisterActivity, "Registrasi berhasil! Silakan login.", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                    finish()
                }
            }
        }
    }
}
