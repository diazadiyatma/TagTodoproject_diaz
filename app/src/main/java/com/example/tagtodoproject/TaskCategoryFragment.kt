package com.example.tagtodoproject.ui.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.tagtodoproject.ExcerciseFragment
import com.example.tagtodoproject.FinanceFragment
import com.example.tagtodoproject.HomeFragment
import com.example.tagtodoproject.SchoolFragment
import com.example.tagtodoproject.WorkFragment
import com.example.tagtodoproject.R
import com.example.tagtodoproject.databinding.FragmentTaskCategoryBinding

class TaskCategoryFragment : Fragment() {

    private var _binding: FragmentTaskCategoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupNavigation()
    }

    private fun setupNavigation() {
        binding.widgetWork.setOnClickListener {
            navigateTo(WorkFragment())
        }

        binding.widgetSchool.setOnClickListener {
            navigateTo(SchoolFragment())
        }

        binding.widgetExercise.setOnClickListener {
            navigateTo(ExcerciseFragment())
        }

        binding.widgetHome.setOnClickListener {
            navigateTo(HomeFragment())
        }

        binding.widgetFinance.setOnClickListener {
            navigateTo(FinanceFragment())
        }
    }

    private fun navigateTo(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_content, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
