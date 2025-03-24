package com.example.tagtodoproject.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tagtodoproject.R
import com.example.tagtodoproject.data.TaskEntity

class TaskAdapter(
    private val onDelete: (TaskEntity) -> Unit = {},
    private val onCheck: ((TaskEntity, Boolean) -> Unit)? = null,
    private val isTrashMode: Boolean = false,
    private val isReadOnly: Boolean = false
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    private val tasks = mutableListOf<TaskEntity>()

    fun submitList(newList: List<TaskEntity>) {
        tasks.clear()
        tasks.addAll(newList)
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbCompleted: CheckBox = view.findViewById(R.id.cbCompleted)
        val tvTaskName: TextView = view.findViewById(R.id.tvTaskName)
        val tvTags: TextView = view.findViewById(R.id.tvTags)
        val tvDate: TextView = view.findViewById(R.id.tvDate)
        val ivDelete: ImageView = view.findViewById(R.id.ivDeleteItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]

        holder.tvTaskName.text = task.title
        holder.tvTags.text = task.tags
        holder.tvDate.text = task.date

        // Checkbox logic
        if (isReadOnly || isTrashMode) {
            holder.cbCompleted.visibility = View.GONE
        } else {
            holder.cbCompleted.visibility = View.VISIBLE
            holder.cbCompleted.isChecked = task.isCompleted
            holder.cbCompleted.setOnCheckedChangeListener { _, isChecked ->
                onCheck?.invoke(task, isChecked)
            }
        }

        // Delete icon logic
        if (isReadOnly) {
            holder.ivDelete.visibility = View.GONE
        } else {
            holder.ivDelete.visibility = View.VISIBLE
            holder.ivDelete.setOnClickListener {
                onDelete.invoke(task)
            }
        }
    }

    override fun getItemCount(): Int = tasks.size
}
