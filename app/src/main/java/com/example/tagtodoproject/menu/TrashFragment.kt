package com.example.tagtodoproject.menu

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.tagtodoproject.R
import com.example.tagtodoproject.authentication.LoginActivity
import com.example.tagtodoproject.task.TaskEntity
import com.example.tagtodoproject.task.TaskViewModel

class TrashFragment : Fragment() {

    private lateinit var rvTrash: RecyclerView
    private lateinit var ivDeleteSelected: ImageView
    private lateinit var tvSelectAll: TextView
    private lateinit var ivEmptyTrash: ImageView
    private lateinit var tvEmptyTrash: TextView
    private lateinit var trashAdapter: TrashAdapter
    private lateinit var emptyStateLayout: LinearLayout
    private lateinit var btnDeleteSelected: ImageView
    private val viewModel: TaskViewModel by viewModels()
    private var selectedTasks: MutableList<TaskEntity> = mutableListOf()
    private var allTasks: List<TaskEntity> = listOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_trash, container, false)

        // Bind views
        rvTrash = view.findViewById(R.id.rvTrash)
        ivDeleteSelected = view.findViewById(R.id.btnDeleteSelected)
        tvSelectAll = view.findViewById(R.id.tvSelectAll)
        ivEmptyTrash = view.findViewById(R.id.ivEmptyTrash)
        tvEmptyTrash = view.findViewById(R.id.tvEmptyTrash)
        emptyStateLayout = view.findViewById(R.id.emptyStateLayout)
        btnDeleteSelected = view.findViewById(R.id.btnDeleteSelected)
        // Check login session
        val sharedPref = requireContext().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", 0)

        if (userId == 0) {
            Toast.makeText(requireContext(), "Session expired. Silakan login ulang.", Toast.LENGTH_SHORT).show()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
            return view
        }

        setupRecyclerView()
        observeData(userId)

        ivDeleteSelected.setOnClickListener {
            if (selectedTasks.isEmpty()) {
                Toast.makeText(requireContext(), "Tidak ada task yang dipilih.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            AlertDialog.Builder(requireContext())
                .setTitle("Delete Task")
                .setMessage("Are you sure you want to permanently delete selected task(s)?")
                .setPositiveButton("Delete") { _, _ ->
                    selectedTasks.forEach { viewModel.delete(it) }
                    Toast.makeText(requireContext(), "Deleted ${selectedTasks.size} task(s)", Toast.LENGTH_SHORT).show()
                    selectedTasks.clear()
                    trashAdapter.clearSelections()
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        tvSelectAll.setOnClickListener {
            if (selectedTasks.size == allTasks.size) {
                // Deselect all
                selectedTasks.clear()
                trashAdapter.clearSelections()
                tvSelectAll.text = "Select All"
            } else {
                // Select all
                selectedTasks.clear()
                selectedTasks.addAll(allTasks)
                trashAdapter.selectAll(allTasks.map { it.id })
                tvSelectAll.text = "Deselect All"
            }
        }

        return view
    }

    private fun setupRecyclerView() {
        trashAdapter = TrashAdapter { task, isChecked ->
            if (isChecked) {
                if (!selectedTasks.contains(task)) selectedTasks.add(task)
            } else {
                selectedTasks.remove(task)
            }

            tvSelectAll.text = if (selectedTasks.size == allTasks.size) "Deselect All" else "Select All"
        }

        rvTrash.layoutManager = LinearLayoutManager(requireContext())
        rvTrash.adapter = trashAdapter
    }

    private fun observeData(userId: Int) {
        viewModel.getTrash(userId).observe(viewLifecycleOwner) { tasks ->
            // Simpan semua task hasil query
            allTasks = tasks

            // Update isi adapter dengan task yang baru
            trashAdapter.submitList(tasks)

            // Reset seleksi
            selectedTasks.clear()
            tvSelectAll.text = "Select All"

            // Tampilkan atau sembunyikan elemen UI sesuai isi trash
            val isEmpty = tasks.isNullOrEmpty()

            rvTrash.visibility = if (isEmpty) View.GONE else View.VISIBLE
            tvSelectAll.visibility = if (isEmpty) View.GONE else View.VISIBLE
            btnDeleteSelected.visibility = if (isEmpty) View.GONE else View.VISIBLE
            emptyStateLayout.visibility = if (isEmpty) View.VISIBLE else View.GONE
        }
    }


}

