package com.example.tagtodoproject.menu

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.tagtodoproject.R
import com.example.tagtodoproject.data.AppDatabase
import com.example.tagtodoproject.databinding.FragmentCalendarBinding
import com.example.tagtodoproject.task.TaskEntity
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import com.prolificinteractive.materialcalendarview.spans.DotSpan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CalendarFragment : Fragment() {

    private var _binding: FragmentCalendarBinding? = null
    private val binding get() = _binding!!

    private var taskList: List<TaskEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCalendarBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launch {
            val userId = getCurrentUserId()

            taskList = withContext(Dispatchers.IO) {
                AppDatabase.Companion.getDatabase(requireContext())
                    .taskDao()
                    .getAllTasksByUserNow(userId)
            }

            val taskDates = taskList.mapNotNull { parseDateToCalendarDay(it.date) }.toSet()
            binding.calendarView.addDecorator(TaskDateDecorator(taskDates))

            binding.calendarView.setOnDateChangedListener { _, date, _ ->
                val selectedDate = formatDate(date)
                val tasksOnDate = taskList.filter { it.date == selectedDate }

                binding.tvSelectedDateHeader.apply {
                    text = "Task pada tanggal: $selectedDate"
                    visibility = View.VISIBLE
                }

                binding.taskListContainer.removeAllViews()

                if (tasksOnDate.isNotEmpty()) {
                    for (task in tasksOnDate) {
                        val taskView = layoutInflater.inflate(
                            R.layout.item_task_no_checkbox,
                            binding.taskListContainer,
                            false
                        )

                        taskView.findViewById<TextView>(R.id.tvTaskName).text = task.title
                        taskView.findViewById<TextView>(R.id.tvCategoryName).text = task.category
                        taskView.findViewById<TextView>(R.id.tvPriority).text = task.tags

                        binding.taskListContainer.addView(taskView)
                    }
                } else {
                    val noTaskText = TextView(requireContext()).apply {
                        text = "Tidak ada task pada tanggal ini."
                        setPadding(24, 24, 24, 24)
                    }
                    binding.taskListContainer.addView(noTaskText)
                }
            }
        }
    }

    private fun getCurrentUserId(): Int {
        val sharedPref = requireActivity().getSharedPreferences("user_session", Context.MODE_PRIVATE)
        return sharedPref.getInt("user_id", 0)
    }

    private fun formatDate(date: CalendarDay): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val calendar = Calendar.getInstance().apply {
            set(date.year, date.month, date.day) // Tidak perlu -1 karena CalendarDay pakai 0-based
        }
        return sdf.format(calendar.time)
    }

    private fun parseDateToCalendarDay(dateString: String): CalendarDay? {
        return try {
            val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            val date = sdf.parse(dateString)
            val cal = Calendar.getInstance().apply { time = date }

            CalendarDay.from(
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH), // sudah 0-based
                cal.get(Calendar.DAY_OF_MONTH)
            )
        } catch (e: Exception) {
            Log.e("CalendarFragment", "Date parse error: $e")
            null
        }
    }

    class TaskDateDecorator(private val dates: Set<CalendarDay>) : DayViewDecorator {
        override fun shouldDecorate(day: CalendarDay): Boolean = dates.contains(day)
        override fun decorate(view: DayViewFacade) {
            view.addSpan(DotSpan(8f, Color.parseColor("#FFD700"))) // Titik emas
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}