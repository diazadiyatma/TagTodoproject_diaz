package com.example.tagtodoproject

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tagtodoproject.data.TaskEntity
import com.example.tagtodoproject.viewmodel.TaskViewModel

class ExcerciseFragment : Fragment() {

    private lateinit var taskContainer: LinearLayout
    private lateinit var emptyStateLayout: LinearLayout

    private val viewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_excercise, container, false)

        taskContainer = view.findViewById(R.id.taskContainer)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)

        // Ambil user ID dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 0)

        if (userId == 0) {
            Toast.makeText(requireContext(), "User belum login.", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.getByCategory("Excercise", userId).observe(viewLifecycleOwner) { taskList ->
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

        val incompleteTasks = tasks.filter { !it.isCompleted }
        val completedTasks = tasks.filter { it.isCompleted }

        // Tambahkan task belum selesai dulu
        for (task in incompleteTasks) {
            val itemView = createTaskItemView(task)
            taskContainer.addView(itemView)
        }

        // Jika ada task completed, tampilkan judul "Completed Tasks"
        if (completedTasks.isNotEmpty()) {
            val header = TextView(requireContext()).apply {
                text = "Completed Tasks"
                textSize = 16f
                setTextColor(resources.getColor(android.R.color.darker_gray, null))
                setPadding(16, 32, 16, 8)
            }
            taskContainer.addView(header)
        }

        // Tambahkan task yang sudah selesai
        for (task in completedTasks) {
            val itemView = createTaskItemView(task)
            taskContainer.addView(itemView)
        }

        emptyStateLayout.visibility = View.GONE
    }

    private fun createTaskItemView(task: TaskEntity): View {
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
            Toast.makeText(
                requireContext(),
                if (isChecked) "Task Completed" else "Task Unchecked",
                Toast.LENGTH_SHORT
            ).show()
        }

        ivDeleteItem.setOnClickListener {
            val deletedTask = task.copy(isDeleted = true)
            viewModel.update(deletedTask)
            Toast.makeText(requireContext(), "Task Deleted", Toast.LENGTH_SHORT).show()
        }

        return itemView
    }
}
