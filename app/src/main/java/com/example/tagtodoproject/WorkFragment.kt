package com.example.tagtodoproject

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tagtodoproject.data.TaskEntity
import com.example.tagtodoproject.viewmodel.TaskViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import java.text.SimpleDateFormat
import java.util.*
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward

class WorkFragment : Fragment() {

    private lateinit var etTask: EditText
    private lateinit var etTags: EditText
    private lateinit var ivDatePicker: ImageView
    private lateinit var btnConfirm: Button
    private lateinit var taskContainer: LinearLayout
    private lateinit var emptyStateLayout: LinearLayout

    private var selectedDate: String? = null
    private var editingTaskId: Int? = null
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_work, container, false)

        etTask = view.findViewById(R.id.etTask)
        etTags = view.findViewById(R.id.etTags)
        ivDatePicker = view.findViewById(R.id.ivDatePicker)
        btnConfirm = view.findViewById(R.id.btnConfirm)
        taskContainer = view.findViewById(R.id.taskContainer)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)

        ivDatePicker.setOnClickListener { showModernDatePicker() }
        btnConfirm.setOnClickListener { saveTask() }

        // ✅ Ambil userId dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 0)

        if (userId == 0) {
            Toast.makeText(requireContext(), "User belum login.", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.getByCategory("Work", userId).observe(viewLifecycleOwner) { taskList ->
                renderTasks(taskList)
            }
        }

        return view
    }


    private fun renderTasks(tasks: List<TaskEntity>) {
        taskContainer.removeAllViews()

        if (tasks.isEmpty()) {
            emptyStateLayout.visibility = View.VISIBLE
            return
        }

        for (task in tasks) {
            val itemView = layoutInflater.inflate(R.layout.fragment_item_task, taskContainer, false)

            val tvTaskName = itemView.findViewById<TextView>(R.id.tvTaskName)
            val tvTags = itemView.findViewById<TextView>(R.id.tvTags)
            val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
            val cbCompleted = itemView.findViewById<CheckBox>(R.id.cbCompleted)
            val ivDeleteItem = itemView.findViewById<ImageView>(R.id.ivDeleteItem)

            tvTaskName.text = task.title
            tvTags.text = "#${task.tags}"
            tvDate.text = task.date
            cbCompleted.isChecked = task.isCompleted

            cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                val updatedTask = task.copy(isCompleted = isChecked)
                viewModel.update(updatedTask)
                Toast.makeText(requireContext(), if (isChecked) "Task Completed" else "Task Unchecked", Toast.LENGTH_SHORT).show()
            }

            itemView.setOnClickListener {
                etTask.setText(task.title)
                etTags.setText(task.tags)
                selectedDate = task.date
                editingTaskId = task.id
            }

            ivDeleteItem.setOnClickListener {
                val deletedTask = task.copy(isDeleted = true)
                viewModel.update(deletedTask)
                Toast.makeText(requireContext(), "Task Deleted", Toast.LENGTH_SHORT).show()
            }

            taskContainer.addView(itemView)
        }

        emptyStateLayout.visibility = View.GONE
    }

    private fun showModernDatePicker() {
        val constraints = CalendarConstraints.Builder()
            .setValidator(DateValidatorPointForward.from(MaterialDatePicker.todayInUtcMilliseconds()))
            .build()

        val picker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select Date")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setCalendarConstraints(constraints)
            .build()

        picker.show(parentFragmentManager, picker.toString())

        picker.addOnPositiveButtonClickListener { selection ->
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            selectedDate = dateFormat.format(Date(selection))
            Toast.makeText(requireContext(), "Date Selected: $selectedDate", Toast.LENGTH_SHORT).show()
        }
    }

    private fun saveTask() {
        val taskName = etTask.text.toString().trim()
        val tags = etTags.text.toString().trim()

        // Ambil userId dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 0)

        if (userId == 0) {
            showToast("User belum login, tidak bisa simpan task.")
            return
        }

        when {
            taskName.isEmpty() -> showToast("Please fill the task field")
            tags.isEmpty() -> showToast("Please fill the tags field")
            selectedDate == null -> showToast("Please select the date")
            else -> {
                if (editingTaskId != null) {
                    val updated = TaskEntity(
                        id = editingTaskId!!,
                        title = taskName,
                        tags = tags,
                        date = selectedDate!!,
                        category = "Work",
                        isCompleted = false,
                        isDeleted = false,
                        userId = userId // ✅ tambahkan userId
                    )
                    viewModel.update(updated)
                    showToast("Task Updated")
                    editingTaskId = null
                } else {
                    val newTask = TaskEntity(
                        title = taskName,
                        tags = tags,
                        date = selectedDate!!,
                        category = "Work",
                        isCompleted = false,
                        isDeleted = false,
                        userId = userId // ✅ tambahkan userId
                    )
                    viewModel.insert(newTask)
                    showToast("Task Added")
                }
                resetForm()
            }
        }
    }


    private fun resetForm() {
        etTask.text.clear()
        etTags.text.clear()
        selectedDate = null
        editingTaskId = null
    }

    private fun showToast(msg: String) {
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
    }
}
