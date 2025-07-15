package com.example.tagtodoproject

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tagtodoproject.authentication.LoginActivity
import com.example.tagtodoproject.data.TaskEntity
import com.example.tagtodoproject.viewmodel.TaskViewModel

class TrashFragment : Fragment() {

    private lateinit var trashContainer: LinearLayout
    private lateinit var emptyStateLayout: LinearLayout
    private val viewModel: TaskViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_trash, container, false)

        // Ambil container & empty layout dari XML (pastikan di fragment_trash.xml sudah dibungkus dalam LinearLayout tunggal)
        trashContainer = view.findViewById(R.id.trashContainer)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)

        // Cek user ID dari SharedPreferences
        val sharedPref = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 0)

        if (userId == 0) {
            Toast.makeText(requireContext(), "Session expired. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
            return view
        }

        // Observe data trash
        viewModel.getTrash(userId).observe(viewLifecycleOwner) { tasks ->
            renderTrash(tasks)
        }

        return view
    }

    private fun renderTrash(tasks: List<TaskEntity>) {
        trashContainer.removeAllViews()

        if (tasks.isEmpty()) {
            emptyStateLayout.visibility = View.VISIBLE
            return
        } else {
            emptyStateLayout.visibility = View.GONE
        }

        for (task in tasks) {
            val itemView = layoutInflater.inflate(R.layout.item_task_trash, trashContainer, false)

            val tvTaskName = itemView.findViewById<TextView>(R.id.tvTaskName)
            val tvTags = itemView.findViewById<TextView>(R.id.tvTags)
            val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
            val tvPriority = itemView.findViewById<TextView>(R.id.tvPriority)
            val ivDeletePermanent = itemView.findViewById<ImageView>(R.id.ivDeletePermanent)

            tvTaskName.text = task.title
            tvTags.text = "#${task.tags}"
            tvDate.text = task.date
            tvPriority.text = "Priority: ${task.priority}"

            ivDeletePermanent.setOnClickListener {
                AlertDialog.Builder(requireContext())
                    .setTitle("Delete Task")
                    .setMessage("Are you sure you want to permanently delete this task?")
                    .setPositiveButton("Delete") { _, _ ->
                        viewModel.delete(task)
                        Toast.makeText(requireContext(), "Task permanently deleted", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }

            trashContainer.addView(itemView)
        }
    }
}
