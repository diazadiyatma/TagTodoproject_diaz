package com.example.tagtodoproject

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.data.TaskEntity
import com.example.tagtodoproject.databinding.FragmentAddGlobalTaskBinding
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.*

class AddGlobalTaskFragment : Fragment() {

    private var _binding: FragmentAddGlobalTaskBinding? = null
    private val binding get() = _binding!!

    private var selectedDate: String = ""
    private var selectedPriority: String = "Low"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddGlobalTaskBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 🔹 Setup Spinner kategori
        val categories = listOf("Work", "School", "Exercise", "Home", "Finance")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter

        // 🔹 Tanggal pakai MaterialDatePicker
        binding.ivDatePicker.setOnClickListener {
            val constraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now()) // hanya tanggal hari ini ke depan

            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Pilih Tanggal")
                .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
                .setCalendarConstraints(constraints.build())
                .build()

            datePicker.show(parentFragmentManager, "MaterialDatePicker")

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                selectedDate = sdf.format(Date(selection))
                binding.tvSelectedDate.text = "Tanggal dipilih: $selectedDate"
            }
        }

        // 🔹 RadioGroup Prioritas
        binding.rgPriority.setOnCheckedChangeListener { _, id ->
            selectedPriority = when (id) {
                R.id.rbLow -> "Low"
                R.id.rbMedium -> "Medium"
                R.id.rbHigh -> "High"
                else -> "Low"
            }
        }

        // 🔹 Tombol Simpan
        binding.btnSave.setOnClickListener {
            val title = binding.etTaskTitle.text.toString().trim()
            val tag = binding.etTag.text.toString().trim()
            val category = binding.spinnerCategory.selectedItem.toString()
            val userId = getCurrentUserId()

            when {
                title.isEmpty() -> {
                    showToast("Judul task tidak boleh kosong!")
                    return@setOnClickListener
                }
                tag.isEmpty() -> {
                    showToast("Tag harus diisi!")
                    return@setOnClickListener
                }
                selectedDate.isEmpty() -> {
                    showToast("Tanggal belum dipilih!")
                    return@setOnClickListener
                }
                userId == 0 -> {
                    showToast("Gagal menyimpan: User belum login.")
                    return@setOnClickListener
                }
            }

            val task = TaskEntity(
                title = title,
                date = selectedDate,
                category = category,
                priority = selectedPriority,
                tags = tag,
                userId = userId
            )

            // 🔃 Simpan ke database
            viewLifecycleOwner.lifecycleScope.launch {
                AppDatabase.getDatabase(requireContext()).taskDao().insert(task)

                withContext(Dispatchers.Main) {
                    showToast("Task berhasil disimpan!")

                    // 🔄 Reset form
                    binding.etTaskTitle.text?.clear()
                    binding.etTag.text?.clear()
                    binding.spinnerCategory.setSelection(0)
                    binding.rgPriority.check(R.id.rbLow)
                    selectedPriority = "Low"
                    selectedDate = ""
                    binding.tvSelectedDate.text = "Tanggal dipilih:"

                    // 🔝 Scroll ke atas
                    binding.scrollView.fullScroll(View.FOCUS_UP)
                }
            }
        }
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }

    private fun getCurrentUserId(): Int {
        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        return sharedPref.getInt("user_id", 0)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
