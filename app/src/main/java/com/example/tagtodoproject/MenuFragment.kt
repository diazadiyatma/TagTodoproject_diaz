package com.example.tagtodoproject.ui.menu

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tagtodoproject.R
import com.example.tagtodoproject.TagsFragment
import com.example.tagtodoproject.TrashFragment
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.databinding.FragmentMenuBinding
import com.example.tagtodoproject.ui.category.TaskCategoryFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuFragment : Fragment() {

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var db: AppDatabase
    private var userId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Ambil userId dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)

        if (userId == -1) {
            Toast.makeText(requireContext(), "User belum login!", Toast.LENGTH_SHORT).show()
            return
        }

        db = AppDatabase.getDatabase(requireContext())
        loadPriorityCounts()

        // Navigasi ke TagsFragment
        binding.widgetTags.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_content, TagsFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigasi ke TaskCategoryFragment
        binding.widgetCategory.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_content, TaskCategoryFragment())
                .addToBackStack(null)
                .commit()
        }

        // Navigasi ke TrashFragment
        binding.widgetTrash.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.main_content, TrashFragment())
                .addToBackStack(null)
                .commit()
        }
    }

    private fun loadPriorityCounts() {
        lifecycleScope.launch {
            val low = withContext(Dispatchers.IO) {
                db.taskDao().countByPriorityForUser("Low", userId)
            }
            val medium = withContext(Dispatchers.IO) {
                db.taskDao().countByPriorityForUser("Medium", userId)
            }
            val high = withContext(Dispatchers.IO) {
                db.taskDao().countByPriorityForUser("High", userId)
            }

            binding.tvLowCount.text = "Low: $low"
            binding.tvMediumCount.text = "Medium: $medium"
            binding.tvHighCount.text = "High: $high"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
