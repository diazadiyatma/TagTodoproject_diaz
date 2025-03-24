package com.example.tagtodoproject

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tagtodoproject.data.TaskEntity
import com.example.tagtodoproject.viewmodel.TaskViewModel

class CompletedFragment : Fragment() {

    private lateinit var completedContainer: LinearLayout
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_completed, container, false)
        completedContainer = view.findViewById(R.id.completedContainer)

        val sharedPref = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 0)

        if (userId != 0) {
            viewModel.getCompleted(userId).observe(viewLifecycleOwner) { taskList ->
                if (!isAdded || !isVisible || view == null) return@observe
                completedContainer.removeAllViews()
                taskList.forEach { addTaskItem(it) }
            }
        } else {
            Toast.makeText(requireContext(), "User belum login.", Toast.LENGTH_SHORT).show()
        }

        return view
    }

    private fun addTaskItem(task: TaskEntity) {
        val itemView = layoutInflater.inflate(R.layout.fragment_item_task, completedContainer, false)

        val tvTaskName = itemView.findViewById<TextView>(R.id.tvTaskName)
        val tvTags = itemView.findViewById<TextView>(R.id.tvTags)
        val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        val cbCompleted = itemView.findViewById<CheckBox>(R.id.cbCompleted)
        val ivDeleteItem = itemView.findViewById<ImageView>(R.id.ivDeleteItem)

        tvTaskName.text = task.title
        tvTags.text = "#${task.tags}"
        tvDate.text = task.date
        cbCompleted.isChecked = true
        ivDeleteItem.visibility = View.GONE

        cbCompleted.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked && isAdded && view != null) {
                val updated = task.copy(isCompleted = false)
                viewModel.update(updated)
                Toast.makeText(requireContext(), "Dipindahkan ke ${task.category}", Toast.LENGTH_SHORT).show()
            }
        }

        completedContainer.addView(itemView)
    }
}
