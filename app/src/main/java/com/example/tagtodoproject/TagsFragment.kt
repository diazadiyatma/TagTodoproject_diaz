package com.example.tagtodoproject

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.tagtodoproject.data.TaskEntity
import com.example.tagtodoproject.viewmodel.TaskViewModel

class TagsFragment : Fragment() {

    private lateinit var tagsContainer: LinearLayout
    private lateinit var etSearchTags: EditText
    private val viewModel: TaskViewModel by viewModels()

    private var allTasks: List<TaskEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tags, container, false)
        tagsContainer = view.findViewById(R.id.tagsContainer)
        etSearchTags = view.findViewById(R.id.etSearchTags)

        val sharedPref = requireContext().getSharedPreferences("user_session", AppCompatActivity.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 0)

        if (userId != 0) {
            viewModel.getGroupedByTags(userId).observe(viewLifecycleOwner) { tasks ->
                allTasks = tasks
                renderGroupedTags(tasks)
            }
        } else {
            Toast.makeText(requireContext(), "User belum login.", Toast.LENGTH_SHORT).show()
        }

        etSearchTags.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                filterTags(s.toString())
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        return view
    }

    private fun filterTags(query: String) {
        if (query.isEmpty()) {
            renderGroupedTags(allTasks)
        } else {
            val filtered = allTasks.filter { it.tags.contains(query, ignoreCase = true) }
            renderGroupedTags(filtered)
        }
    }

    private fun renderGroupedTags(tasks: List<TaskEntity>) {
        tagsContainer.removeAllViews()

        val grouped = tasks.groupBy { it.tags }

        for ((tagName, taskList) in grouped) {
            val header = TextView(requireContext()).apply {
                text = "#$tagName"
                textSize = 18f
                setTextColor(resources.getColor(R.color.white))
                setPadding(0, 24, 0, 12)
            }
            tagsContainer.addView(header)

            for (task in taskList) {
                val itemView = layoutInflater.inflate(R.layout.fragment_item_task, tagsContainer, false)

                itemView.findViewById<TextView>(R.id.tvTaskName).text = task.title
                itemView.findViewById<TextView>(R.id.tvTags).text = "#${task.tags}"
                itemView.findViewById<TextView>(R.id.tvDate).text = task.date

                itemView.findViewById<CheckBox>(R.id.cbCompleted).visibility = View.GONE
                itemView.findViewById<ImageView>(R.id.ivDeleteItem).visibility = View.GONE

                tagsContainer.addView(itemView)
            }
        }
    }
}
