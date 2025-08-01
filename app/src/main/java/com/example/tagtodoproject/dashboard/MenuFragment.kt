package com.example.tagtodoproject.dashboard

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.tagtodoproject.menu.CalendarFragment
import com.example.tagtodoproject.R
import com.example.tagtodoproject.menu.TagsFragment
import com.example.tagtodoproject.menu.TrashFragment
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.task.TaskEntity
import com.example.tagtodoproject.task.TaskViewModel
import com.example.tagtodoproject.menu.TaskCategoryFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MenuFragment : Fragment() {

    private val taskViewModel: TaskViewModel by viewModels()

    private lateinit var tvUserName: TextView
    private lateinit var ivProfilePhoto: ImageView
    private var userId: Int = -1

    // Permission request launcher
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            loadUserProfile()
        } else {
            ivProfilePhoto.setImageResource(R.drawable.user_svgrepo_com)
            Toast.makeText(requireContext(), "Permission diperlukan untuk mengakses foto profil", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        tvUserName = view.findViewById(R.id.textViewUserName)
        ivProfilePhoto = view.findViewById(R.id.imageViewProfilePhoto)

        // 👉 Tambahkan ini untuk klik avatar
        ivProfilePhoto.setOnClickListener {
            showZoomedAvatar()
        }

        val sharedPref = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return view
        }

        // Cek permission dan load profil user
        checkPermissionAndLoadUser()

        // 🔹 Load task dan update tampilan
        taskViewModel.getAllTasks(userId).observe(viewLifecycleOwner) { allTasks ->
            val today = getTodayDate()
            val todayTasks = allTasks.filter {
                it.date == today && !it.isDeleted
            }
            val activeTasks = allTasks.filter { !it.isDeleted }

            loadTodayTasks(todayTasks, view)
            updatePrioritySummary(activeTasks, view)
        }

        // 🔹 Navigasi ke fragment lain
        view.findViewById<View>(R.id.widgetTags).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_content, TagsFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<View>(R.id.widgetTrash).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_content, TrashFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<View>(R.id.widgetCategory).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_content, TaskCategoryFragment())
                .addToBackStack(null)
                .commit()
        }

        view.findViewById<View>(R.id.widgetCalendar).setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_content, CalendarFragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }

    private fun checkPermissionAndLoadUser() {
        val permission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        when {
            ContextCompat.checkSelfPermission(requireContext(), permission) == PackageManager.PERMISSION_GRANTED -> {
                loadUserProfile()
            }
            shouldShowRequestPermissionRationale(permission) -> {
                // Bisa tampilkan dialog penjelasan jika perlu
                requestPermissionLauncher.launch(permission)
            }
            else -> {
                requestPermissionLauncher.launch(permission)
            }
        }
    }

    private fun loadUserProfile() {
        lifecycleScope.launch {
            val userDao = AppDatabase.Companion.getDatabase(requireContext()).userDao()
            val user = withContext(Dispatchers.IO) {
                userDao.getUserById(userId)
            }

            user?.let {
                tvUserName.text = it.username

                if (!it.profilePhotoUri.isNullOrBlank()) {
                    val uri = Uri.parse(it.profilePhotoUri)
                    try {
                        val inputStream = requireContext().contentResolver.openInputStream(uri)
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        ivProfilePhoto.setImageBitmap(bitmap)
                        ivProfilePhoto.scaleType = ImageView.ScaleType.CENTER_CROP
                        inputStream?.close()
                    } catch (e: SecurityException) {
                        e.printStackTrace()
                        ivProfilePhoto.setImageResource(R.drawable.user_svgrepo_com)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        ivProfilePhoto.setImageResource(R.drawable.user_svgrepo_com)
                    }
                } else {
                    ivProfilePhoto.setImageResource(R.drawable.user_svgrepo_com)
                }
            } ?: run {
                tvUserName.text = "User"
                ivProfilePhoto.setImageResource(R.drawable.user_svgrepo_com)
            }
        }
    }

    private fun getTodayDate(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun loadTodayTasks(tasks: List<TaskEntity>, rootView: View) {
        val taskContainer = rootView.findViewById<LinearLayout>(R.id.layoutTodayTasks)
        taskContainer.removeAllViews()

        for (task in tasks) {
            val taskView = layoutInflater.inflate(R.layout.item_task_plain, taskContainer, false)

            val tvTaskName = taskView.findViewById<TextView>(R.id.tvTaskName)
            val tvCategoryName = taskView.findViewById<TextView>(R.id.tvCategoryName)
            val tvPriority = taskView.findViewById<TextView>(R.id.tvPriority)
            val tvTags = taskView.findViewById<TextView>(R.id.tvTags)
            val ivCategoryIcon = taskView.findViewById<ImageView>(R.id.ivCategoryIcon)
            val cbComplete = taskView.findViewById<CheckBox>(R.id.cbComplete)


            tvTaskName.text = task.title
            tvCategoryName.text = task.category
            tvPriority.text = "${task.priority.replaceFirstChar { it.uppercase() }} Priority"

            val formattedTags = task.tags.split(",").joinToString(" ") { "#${it.trim()}" }
            tvTags.text = formattedTags

            val poppins = ResourcesCompat.getFont(requireContext(), R.font.font_poppins)
            tvTaskName.typeface = poppins
            tvCategoryName.typeface = poppins
            tvPriority.typeface = poppins
            tvTags.typeface = poppins

            val iconRes = when (task.category.lowercase(Locale.getDefault())) {
                "work" -> R.drawable.work_case
                "school" -> R.drawable.school_svg
                "home" -> R.drawable.home_svg
                "exercise" -> R.drawable.excercise_svg
                "finance" -> R.drawable.finance_minimalist
                else -> R.drawable.tags_svgrepo_com
            }
            ivCategoryIcon.setImageResource(iconRes)

            cbComplete.isChecked = task.isCompleted
            cbComplete.setOnCheckedChangeListener { _, isChecked ->
                val updatedTask = task.copy(isCompleted = isChecked)
                taskViewModel.update(updatedTask)

                val message = if (isChecked)
                    "Task '${task.title}' ditandai selesai!"
                else
                    "Task '${task.title}' dikembalikan ke kategori!"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                loadTodayTasks(tasks.filter { !it.isCompleted }, rootView)
            }

            if (!task.isCompleted) {
                taskContainer.addView(taskView)
            }
        }
    }

    private fun updatePrioritySummary(tasks: List<TaskEntity>, rootView: View) {
        val lowCount = tasks.count { it.priority.equals("low", ignoreCase = true) }
        val mediumCount = tasks.count { it.priority.equals("medium", ignoreCase = true) }
        val highCount = tasks.count { it.priority.equals("high", ignoreCase = true) }

        rootView.findViewById<TextView>(R.id.tvLowCount).text = "Low: $lowCount"
        rootView.findViewById<TextView>(R.id.tvMediumCount).text = "Medium: $mediumCount"
        rootView.findViewById<TextView>(R.id.tvHighCount).text = "High: $highCount"

    }

    private fun showZoomedAvatar() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_fullscreen_image, null)
        val ivFullImage = dialogView.findViewById<ImageView>(R.id.ivFullImage)

        lifecycleScope.launch {
            val userDao = AppDatabase.Companion.getDatabase(requireContext()).userDao()
            val user = withContext(Dispatchers.IO) {
                userDao.getUserById(userId)
            }

            val imageLoaded = if (!user?.profilePhotoUri.isNullOrBlank()) {
                try {
                    val uri = Uri.parse(user!!.profilePhotoUri)
                    val inputStream = requireContext().contentResolver.openInputStream(uri)
                    val bitmap = BitmapFactory.decodeStream(inputStream)
                    inputStream?.close()
                    ivFullImage.setImageBitmap(bitmap)
                    true
                } catch (e: Exception) {
                    false
                }
            } else {
                false
            }

            if (!imageLoaded) {
                ivFullImage.setImageResource(R.drawable.user_svgrepo_com)
            }

            val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
            dialog.setContentView(dialogView)
            dialog.show()
        }
    }
}