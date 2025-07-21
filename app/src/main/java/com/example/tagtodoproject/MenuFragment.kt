package com.example.tagtodoproject.ui.menu

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tagtodoproject.*
import com.example.tagtodoproject.data.TaskEntity
import com.example.tagtodoproject.ui.category.TaskCategoryFragment
import com.example.tagtodoproject.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.*

class MenuFragment : Fragment() {

    private val taskViewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_menu, container, false)

        val userId = getUserIdFromSession()
        if (userId == null) {
            Toast.makeText(requireContext(), "User ID tidak ditemukan!", Toast.LENGTH_SHORT).show()
            return view
        }

        taskViewModel.getAllTasks(userId).observe(viewLifecycleOwner) { allTasks ->
            val today = getTodayDate()
            val todayTasks = allTasks.filter {
                it.date == today && !it.isDeleted
            }
            val activeTasks = allTasks.filter { !it.isDeleted } // ✅ Semua task yang belum dihapus

            loadTodayTasks(todayTasks, view)
            updatePrioritySummary(activeTasks, view) // ✅ dihitung dari semua task aktif

        }

        // Navigation
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

    private fun getUserIdFromSession(): Int? {
        val sharedPref = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1)
        return if (userId != -1) userId else null
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
            val tvTags = taskView.findViewById<TextView>(R.id.tvTags) // ⬅️ NEW
            val ivCategoryIcon = taskView.findViewById<ImageView>(R.id.ivCategoryIcon)
            val cbComplete = taskView.findViewById<CheckBox>(R.id.cbComplete)

            // Set data
            tvTaskName.text = task.title
            tvCategoryName.text = task.category
            tvPriority.text = "${task.priority.replaceFirstChar { it.uppercase() }} Priority"

            // Handle tags string → "#urgent #personal"
            val formattedTags = task.tags.split(",").joinToString(" ") { "#${it.trim()}" }
            tvTags.text = formattedTags

            // Font styling
            val poppins = ResourcesCompat.getFont(requireContext(), R.font.font_poppins)
            tvTaskName.typeface = poppins
            tvCategoryName.typeface = poppins
            tvPriority.typeface = poppins
            tvTags.typeface = poppins

            // Icon category
            val iconRes = when (task.category.lowercase(Locale.getDefault())) {
                "work" -> R.drawable.work_case
                "school" -> R.drawable.school_svg
                "home" -> R.drawable.home_svg
                "exercise" -> R.drawable.excercise_svg
                "finance" -> R.drawable.finance_minimalist
                else -> R.drawable.tags_svgrepo_com
            }
            ivCategoryIcon.setImageResource(iconRes)

            // Checkbox handler
            cbComplete.isChecked = task.isCompleted
            cbComplete.setOnCheckedChangeListener { _, isChecked ->
                val updatedTask = task.copy(isCompleted = isChecked)
                taskViewModel.update(updatedTask)

                val message = if (isChecked)
                    "Task '${task.title}' ditandai selesai!"
                else
                    "Task '${task.title}' dikembalikan ke kategori!"
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()

                // Refresh view
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
}
