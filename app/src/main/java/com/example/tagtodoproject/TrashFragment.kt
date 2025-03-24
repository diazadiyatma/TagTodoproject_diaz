package com.example.tagtodoproject

import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tagtodoproject.data.TaskEntity
import com.example.tagtodoproject.viewmodel.TaskViewModel

class TrashFragment : Fragment() {

    private lateinit var trashContainer: LinearLayout
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trash, container, false)
        trashContainer = view.findViewById(R.id.trashContainer)

        // ✅ Ambil userId dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 0)

        if (userId == 0) {
            Toast.makeText(requireContext(), "User belum login.", Toast.LENGTH_SHORT).show()
        } else {
            // ✅ Kirim userId ke ViewModel
            viewModel.getTrash(userId).observe(viewLifecycleOwner) { tasks ->
                renderTrash(tasks)
            }
        }

        return view
    }

    private fun renderTrash(tasks: List<TaskEntity>) {
        trashContainer.removeAllViews()

        if (tasks.isEmpty()) {
            val emptyText = TextView(requireContext())
            emptyText.text = "No deleted tasks."
            emptyText.setPadding(32, 32, 32, 32)
            trashContainer.addView(emptyText)
            return
        }

        for (task in tasks) {
            val itemView = layoutInflater.inflate(R.layout.fragment_item_task, trashContainer, false)

            val tvTaskName = itemView.findViewById<TextView>(R.id.tvTaskName)
            val tvTags = itemView.findViewById<TextView>(R.id.tvTags)
            val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
            val ivDelete = itemView.findViewById<ImageView>(R.id.ivDeleteItem)

            tvTaskName.text = task.title
            tvTags.text = "#${task.tags}"
            tvDate.text = task.date

            itemView.findViewById<CheckBox>(R.id.cbCompleted).visibility = View.GONE

            ivDelete.setOnClickListener {
                viewModel.delete(task)
                Toast.makeText(requireContext(), "Task permanently deleted", Toast.LENGTH_SHORT).show()
            }

            trashContainer.addView(itemView)
        }
    }
}
