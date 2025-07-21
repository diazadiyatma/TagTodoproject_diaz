package com.example.tagtodoproject

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.tagtodoproject.authentication.LoginActivity
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.model.UserEntity
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
    private lateinit var etGender: EditText
    private lateinit var etBio: EditText

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

        ivProfile = view.findViewById(R.id.ivProfile)
        btnEditProfile = view.findViewById(R.id.btnEditProfile)
        btnLogout = view.findViewById(R.id.btnLogout)
        etUsername = view.findViewById(R.id.etUsername)
        tvEmail = view.findViewById(R.id.tvEmail)

        val itemContact = view.findViewById<View>(R.id.itemContact)
        val itemLocation = view.findViewById<View>(R.id.itemLocation)
        val itemBirthDate = view.findViewById<View>(R.id.itemBirthDate)
        val itemGender = view.findViewById<View>(R.id.itemGender)
        val itemBio = view.findViewById<View>(R.id.itemBio)

        etContact = itemContact.findViewById(R.id.etField)
        etLocation = itemLocation.findViewById(R.id.etField)
        etBirthDate = itemBirthDate.findViewById(R.id.etField)
        etGender = itemGender.findViewById(R.id.etField)
        etBio = itemBio.findViewById(R.id.etField)

        itemContact.findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_phone)
        itemLocation.findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_location)
        itemBirthDate.findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_calendar)
        itemGender.findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_gender)
        itemBio.findViewById<ImageView>(R.id.ivIcon).setImageResource(R.drawable.ic_info)

        etContact.hint = "Contact"
        etLocation.hint = "Location"
        etBirthDate.hint = "Birth Date"
        etGender.hint = "Gender"
        etBio.hint = "Bio"

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
                etGender.setText(it.gender ?: "")
                etBio.setText(it.bio ?: "")

                if (!it.profilePhotoUri.isNullOrEmpty()) {
                    Glide.with(this@ProfileFragment)
                        .load(it.profilePhotoUri)
                        .apply(RequestOptions.circleCropTransform())
                        .placeholder(R.drawable.account_avatar_profile_user_svgrepo_com)
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
                gender = etGender.text.toString(),
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
        etGender.isEnabled = enabled
        etBio.isEnabled = enabled
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
                .placeholder(R.drawable.account_avatar_profile_user_svgrepo_com)
                .into(imageView)
        } else {
            ivProfile.drawable?.let {
                imageView.setImageDrawable(it)
            } ?: imageView.setImageResource(R.drawable.account_avatar_profile_user_svgrepo_com)
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
