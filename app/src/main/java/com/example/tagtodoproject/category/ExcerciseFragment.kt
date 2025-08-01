package com.example.tagtodoproject.category

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.tagtodoproject.R
import com.example.tagtodoproject.task.TaskEntity
import com.example.tagtodoproject.task.TaskViewModel

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

        val sharedPref = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 0)

        if (userId == 0) {
            Toast.makeText(requireContext(), "User belum login.", Toast.LENGTH_SHORT).show()
        } else {
            viewModel.getByCategory("Exercise", userId).observe(viewLifecycleOwner) { taskList ->
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

        for (task in incompleteTasks) {
            val itemView = createTaskItemView(task)
            taskContainer.addView(itemView)
        }

        if (completedTasks.isNotEmpty()) {
            val header = TextView(requireContext()).apply {
                text = "Completed Tasks"
                textSize = 16f
                setTextColor(resources.getColor(android.R.color.darker_gray, null))
                setPadding(16, 32, 16, 8)
            }
            taskContainer.addView(header)
        }

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
        val tvPriority = itemView.findViewById<TextView>(R.id.tvPriority)


        tvTaskName.text = task.title
        tvTags.text = "#${task.tags}"
        tvDate.text = task.date
        cbCompleted.isChecked = task.isCompleted
        tvPriority.text = "Priority: ${task.priority}"

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
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to delete this task?")
                .setPositiveButton("Yes") { dialog, _ ->
                    val deletedTask = task.copy(isDeleted = true)
                    viewModel.update(deletedTask)
                    Toast.makeText(requireContext(), "Task Deleted", Toast.LENGTH_SHORT).show()
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ ->
                    dialog.dismiss()
                }
                .show()
        }

        // ✅ Navigasi ke EditTaskFragment
        itemView.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("task", task)
            }
            val editFragment = EditTaskFragment()
            editFragment.arguments = bundle
            navigateTo(editFragment)

        }

        return itemView
    }
    private fun navigateTo(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.main_content, fragment)
            .addToBackStack(null)
            .commit()
}


    }
