package com.example.tagtodoproject.authentication

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Patterns
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.tagtodoproject.user.UserDao
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.databinding.ActivityRegisterBinding
import com.example.tagtodoproject.user.UserEntity
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
                binding.etBirthDate.error = null
            }
        }
    }

    private fun setupListeners() {
        binding.btnRegister.setOnClickListener {
            clearAllErrors()
            val user = collectUserInput() ?: return@setOnClickListener
            checkContactUniquenessAndRegister(user)
        }

        binding.tvLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun clearAllErrors() {
        binding.etUsername.error = null
        binding.etEmail.error = null
        binding.etPassword.error = null
        binding.etConfirmPassword.error = null
        binding.etContact.error = null
        binding.etLocation.error = null
        binding.etBirthDate.error = null
        binding.etGender.error = null
    }

    private fun collectUserInput(): UserEntity? {
        val username = binding.etUsername.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()
        val contact = binding.etContact.text.toString().trim()
        val location = binding.etLocation.text.toString().trim()
        val gender = binding.etGender.text.toString().trim()

        var isValid = true

        if (username.isEmpty()) {
            binding.etUsername.error = "Username wajib diisi"
            isValid = false
        }

        if (email.isEmpty()) {
            binding.etEmail.error = "Email wajib diisi"
            isValid = false
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Format email tidak valid"
            isValid = false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password wajib diisi"
            isValid = false
        } else if (!isValidPassword(password)) {
            binding.etPassword.error = "Minimal 5 karakter, kombinasi huruf & angka"
            isValid = false
        }

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Konfirmasi password wajib diisi"
            isValid = false
        } else if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Password tidak cocok"
            isValid = false
        }

        if (contact.isEmpty()) {
            binding.etContact.error = "Kontak wajib diisi"
            isValid = false
        }

        if (location.isEmpty()) {
            binding.etLocation.error = "Lokasi wajib diisi"
            isValid = false
        }

        if (selectedBirthDate.isEmpty()) {
            binding.etBirthDate.error = "Tanggal lahir wajib diisi"
            isValid = false
        }

        if (gender.isEmpty()) {
            binding.etGender.error = "Gender wajib diisi"
            isValid = false
        }

        if (!isValid) return null

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

    private fun isValidPassword(password: String): Boolean {
        val hasLetter = password.any { it.isLetter() }
        val hasDigit = password.any { it.isDigit() }
        return password.length >= 5 && hasLetter && hasDigit
    }

    private fun checkContactUniquenessAndRegister(user: UserEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            val existingUserByEmail = userDao.getUserByEmail(user.email)
            val existingUserByContact = userDao.getUserByContact(user.contact)

            withContext(Dispatchers.Main) {
                when {
                    existingUserByEmail != null -> {
                        binding.etEmail.error = "Email sudah terdaftar"
                    }
                    existingUserByContact != null -> {
                        binding.etContact.error = "Kontak sudah digunakan"
                    }
                    else -> {
                        registerUser(user)
                    }
                }
            }
        }
    }

    private fun registerUser(user: UserEntity) {
        lifecycleScope.launch(Dispatchers.IO) {
            userDao.insertUser(user)
            withContext(Dispatchers.Main) {
                startActivity(Intent(this@RegisterActivity, LoginActivity::class.java))
                finish()
            }
        }
    }
}
