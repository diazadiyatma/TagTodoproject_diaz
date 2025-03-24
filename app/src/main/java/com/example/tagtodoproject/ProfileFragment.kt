package com.example.tagtodoproject

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.tagtodoproject.authentication.LoginActivity
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.model.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ProfileFragment : Fragment() {

    private lateinit var ivProfile: ImageView
    private lateinit var tvUsername: TextView
    private lateinit var tvEmail: TextView
    private lateinit var tvContact: TextView
    private lateinit var tvLocation: TextView
    private lateinit var btnLogout: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        ivProfile = view.findViewById(R.id.ivProfile)
        tvUsername = view.findViewById(R.id.tvUsername)
        tvEmail = view.findViewById(R.id.tvEmail)
        tvContact = view.findViewById(R.id.tvContact)
        tvLocation = view.findViewById(R.id.tvLocation)
        btnLogout = view.findViewById(R.id.btnLogout)

        loadUserDataFromRoom()
        setupLogout()

        return view
    }

    private fun loadUserDataFromRoom() {
        val sharedPref = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val email = sharedPref.getString("email", null)

        if (email == null) {
            return
        }

        val userDao = AppDatabase.getDatabase(requireContext()).userDao()

        lifecycleScope.launch {
            val user: UserEntity? = withContext(Dispatchers.IO) {
                userDao.getUserByEmail(email)
            }

            user?.let {
                tvUsername.text = it.username
                tvEmail.text = it.email
                tvContact.text = it.contact
                tvLocation.text = it.location

                Glide.with(this@ProfileFragment)
                    .load(it.profilePhotoUri)
                    .placeholder(R.drawable.user_svgrepo_com)
                    .into(ivProfile)
            }
        }
    }

    private fun setupLogout() {
        btnLogout.setOnClickListener {
            val sharedPref = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}
