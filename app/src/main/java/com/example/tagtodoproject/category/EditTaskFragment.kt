package com.example.tagtodoproject.category

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tagtodoproject.databinding.FragmentEditTaskBinding
import com.example.tagtodoproject.task.TaskEntity
import com.example.tagtodoproject.task.TaskViewModel
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*

class EditTaskFragment : Fragment() {

    private var _binding: FragmentEditTaskBinding? = null
    private val binding get() = _binding!!
    private val taskViewModel: TaskViewModel by viewModels()
    private var task: TaskEntity? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditTaskBinding.inflate(inflater, container, false)

        // Ambil task dari argument
        task = arguments?.getParcelable("task")

        // Setup spinner priority
        val priorities = listOf("Low", "Medium", "High")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, priorities)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerPriority.adapter = adapter

        // Set nilai awal ke form
        task?.let {
            binding.etTaskName.setText(it.title)
            binding.etTag.setText(it.tags)
            binding.etDueDate.setText(it.date)
            binding.spinnerPriority.setSelection(priorities.indexOf(it.priority))
        }

        // Event klik kalender (baik EditText maupun ikon kalender)
        binding.etDueDate.setOnClickListener { showDatePickerDialog() }
        binding.ivCalendar.setOnClickListener { showDatePickerDialog() }

        // Tombol update
        binding.btnUpdate.setOnClickListener {
            val title = binding.etTaskName.text.toString().trim()
            val tag = binding.etTag.text.toString().trim()
            val dueDate = binding.etDueDate.text.toString().trim()
            val selectedPriority = binding.spinnerPriority.selectedItem.toString()


            if (title.isEmpty()) {
                binding.etTaskName.error = "Task name cannot be empty"
                return@setOnClickListener
            }

            val updatedTask = task?.copy(
                title = title,
                tags = tag,
                date = dueDate,
                priority = selectedPriority
            )

            updatedTask?.let {
                taskViewModel.update(it)
                Toast.makeText(requireContext(), "Task updated successfully", Toast.LENGTH_SHORT)
                    .show()
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }
        }

        return binding.root
    }

    private fun showDatePickerDialog() {
        val constraintsBuilder = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.now())

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select due date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraintsBuilder.build())
            .build()

        datePicker.show(parentFragmentManager, "MATERIAL_DATE_PICKER")

        datePicker.addOnPositiveButtonClickListener { selection ->
            val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.timeInMillis = selection

            val formatter = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val formattedDate = formatter.format(calendar.time)

            binding.etDueDate.setText(formattedDate)
        }
    }

}

