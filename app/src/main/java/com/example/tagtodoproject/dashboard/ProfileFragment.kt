package com.example.tagtodoproject.dashboard

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.tagtodoproject.R
import com.example.tagtodoproject.authentication.LoginActivity
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.user.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class ProfileFragment : Fragment() {

    private lateinit var ivProfile: ImageView
    private lateinit var btnEditProfile: Button
    private lateinit var btnLogout: Button
    private lateinit var etUsername: EditText
    private lateinit var tvEmail: TextView
    private lateinit var etContact: EditText
    private lateinit var etLocation: EditText
    private lateinit var etBirthDate: EditText
    private lateinit var spinnerGender: AppCompatSpinner
    private lateinit var etBio: EditText
    private lateinit var ivDropdownIcon: ImageView
    private lateinit var ivCalendarIcon: ImageView
    private var isEditMode = false
    private lateinit var currentUser: UserEntity
    private var selectedBirthDate: String = ""

    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null && isEditMode) {
            Glide.with(this)
                .load(uri)
                .apply(RequestOptions.circleCropTransform())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivProfile)

            updateUserProfilePhoto(uri.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Views
        ivProfile = view.findViewById(R.id.ivProfile)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnLogout = view.findViewById(R.id.btnLogout)
        etUsername = view.findViewById(R.id.etUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        etContact = view.findViewById(R.id.etContact)
        etLocation = view.findViewById(R.id.etLocation)
        etBirthDate = view.findViewById(R.id.ivCalendar)
        spinnerGender = view.findViewById(R.id.ivGenderDrop)
        etBio = view.findViewById(R.id.etBio)
        ivDropdownIcon = view.findViewById(R.id.ivDropdownIcon)
        ivCalendarIcon = view.findViewById(R.id.ivCalendarIcon)


        // Spinner gender setup
        val genderOptions = listOf("Male", "Female")
        val genderAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, genderOptions)
        spinnerGender.adapter = genderAdapter

        btnEditProfile.setOnClickListener { toggleEditMode() }

        ivProfile.setOnClickListener {
            if (isEditMode) {
                pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            } else {
                showZoomedAvatar()
            }
        }

        etBirthDate.setOnClickListener {
            if (isEditMode) showDatePicker()
        }

        btnLogout.setOnClickListener {
            val sharedPref = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
            sharedPref.edit().clear().apply()
            startActivity(Intent(requireContext(), LoginActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            })
        }

        loadUserData()
        return view
    }

    private fun loadUserData() {
        val sharedPref = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val email = sharedPref.getString("email", null) ?: return
        val userDao = AppDatabase.getDatabase(requireContext()).userDao()

        lifecycleScope.launch {
            val user = withContext(Dispatchers.IO) {
                userDao.getUserByEmail(email)
            }

            user?.let {
                currentUser = it
                etUsername.setText(it.username)
                tvEmail.text = it.email
                etContact.setText(it.contact)
                etLocation.setText(it.location)
                etBirthDate.setText(it.birthDate ?: "")
                selectedBirthDate = it.birthDate ?: ""
                etBio.setText(it.bio ?: "")

                // Set gender
                val genderIndex = listOf("Male", "Female").indexOf(it.gender ?: "Male")
                spinnerGender.setSelection(if (genderIndex != -1) genderIndex else 0)

                if (!it.profilePhotoUri.isNullOrEmpty()) {
                    Glide.with(this@ProfileFragment)
                        .load(it.profilePhotoUri)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.user_svgrepo_com)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(ivProfile)
                }

                setFieldsEnabled(false)
            }
        }
    }

    private fun toggleEditMode() {
        if (isEditMode) {
            val updated = currentUser.copy(
                username = etUsername.text.toString(),
                contact = etContact.text.toString(),
                location = etLocation.text.toString(),
                gender = spinnerGender.selectedItem.toString(),
                birthDate = selectedBirthDate,
                bio = etBio.text.toString()
            )

            lifecycleScope.launch(Dispatchers.IO) {
                AppDatabase.getDatabase(requireContext()).userDao().updateUser(updated)
                currentUser = updated
            }

            Toast.makeText(requireContext(), "Profile updated", Toast.LENGTH_SHORT).show()
        }

        isEditMode = !isEditMode
        setFieldsEnabled(isEditMode)
        btnEditProfile.text = if (isEditMode) "Save" else "Edit Profile"
    }

    private fun setFieldsEnabled(enabled: Boolean) {
        etUsername.isEnabled = enabled
        etContact.isEnabled = enabled
        etLocation.isEnabled = enabled
        etBirthDate.isEnabled = enabled
        etBio.isEnabled = enabled
        spinnerGender.isEnabled = enabled

        ivDropdownIcon.visibility = if (enabled) View.VISIBLE else View.GONE
        ivCalendarIcon.visibility = if (enabled) View.VISIBLE else View.GONE
    }


    private fun updateUserProfilePhoto(photoUri: String) {
        val updatedUser = currentUser.copy(profilePhotoUri = photoUri)
        lifecycleScope.launch(Dispatchers.IO) {
            AppDatabase.getDatabase(requireContext()).userDao().updateUser(updatedUser)
            currentUser = updatedUser
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selected = String.format("%02d/%02d/%04d", day, month + 1, year)
                selectedBirthDate = selected
                etBirthDate.setText(selected)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    private fun showZoomedAvatar() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_fullscreen_image)

        val imageView = dialog.findViewById<ImageView>(R.id.ivFullImage)
        val imageUri = currentUser.profilePhotoUri

        if (!imageUri.isNullOrEmpty()) {
            Glide.with(this)
                .load(imageUri)
                .apply(RequestOptions.circleCropTransform())
                .placeholder(R.drawable.user_svgrepo_com)
                .into(imageView)
        } else {
            ivProfile.drawable?.let {
                imageView.setImageDrawable(it)
            } ?: imageView.setImageResource(R.drawable.user_svgrepo_com)
        }

        imageView.setOnClickListener { dialog.dismiss() }

        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        dialog.window?.setBackgroundDrawableResource(android.R.color.black)
        dialog.show()
    }
}
