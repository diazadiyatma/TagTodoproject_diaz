package com.example.tagtodoproject.menu

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.tagtodoproject.R
import com.example.tagtodoproject.task.TaskEntity

class TrashAdapter(
    private val onTaskChecked: (TaskEntity, Boolean) -> Unit
) : ListAdapter<TaskEntity, TrashAdapter.TrashViewHolder>(DiffCallback()) {

    private val checkedMap = mutableMapOf<Int, Boolean>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrashViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_trash, parent, false)
        return TrashViewHolder(view)
    }

    override fun onBindViewHolder(holder: TrashViewHolder, position: Int) {
        val task = getItem(position)
        holder.bind(task)
    }

    /** Select All Tasks */
    fun selectAll(taskIds: List<Int>) {
        checkedMap.clear()
        taskIds.forEach {
            checkedMap[it] = true
        }
        notifyDataSetChanged()
    }

    /** Deselect All Tasks */
    fun clearSelections() {
        checkedMap.clear()
        notifyDataSetChanged()
    }

    /** Toggle Select/Deselect All */
    fun toggleSelectAll(): Boolean {
        val allSelected = currentList.all { checkedMap[it.id] == true }
        if (allSelected) {
            clearSelections()
            return false // now deselected
        } else {
            selectAll(currentList.map { it.id })
            return true // now selected
        }
    }

    /** Get Selected Tasks */
    fun getSelectedTasks(): List<TaskEntity> {
        return currentList.filter { checkedMap[it.id] == true }
    }

    /** Uncheck Individual Task */
    fun uncheckTask(taskId: Int) {
        checkedMap[taskId] = false
        notifyDataSetChanged()
    }

    inner class TrashViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvTaskName = itemView.findViewById<TextView>(R.id.tvTaskName)
        private val tvTags = itemView.findViewById<TextView>(R.id.tvTags)
        private val tvDate = itemView.findViewById<TextView>(R.id.tvDate)
        private val tvPriority = itemView.findViewById<TextView>(R.id.tvPriority)
        private val cbSelect = itemView.findViewById<CheckBox>(R.id.checkboxSelect)

        fun bind(task: TaskEntity) {
            tvTaskName.text = task.title
            tvTags.text = "#${task.tags}"
            tvDate.text = task.date
            tvPriority.text = "Priority: ${task.priority}"

            cbSelect.setOnCheckedChangeListener(null)
            cbSelect.isChecked = checkedMap[task.id] == true

            cbSelect.setOnCheckedChangeListener { _, isChecked ->
                checkedMap[task.id] = isChecked
                onTaskChecked(task, isChecked)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<TaskEntity>() {
        override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
            return oldItem == newItem
        }
    }
}
