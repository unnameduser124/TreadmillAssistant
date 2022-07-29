package com.example.treadmillassistant.ui.home.calendarTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.workout.Workout
import com.example.treadmillassistant.backend.workoutCalendar
import com.example.treadmillassistant.databinding.CalendarTabBinding
import com.example.treadmillassistant.ui.home.PageViewModel
import kotlinx.coroutines.runBlocking
import java.security.MessageDigest
import java.time.LocalDateTime
import java.util.*
import java.util.Calendar.DATE

class CalendarPlaceholderFragment: Fragment() {
    private lateinit var pageViewModel: PageViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pageViewModel = ViewModelProvider(this).get(PageViewModel::class.java).apply {
            setIndex(arguments?.getInt(SECTION_NUMBER) ?: 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = CalendarTabBinding.inflate(layoutInflater)

        val calendar = Calendar.getInstance()

        var dataset = user.workoutSchedule.workoutList.filter{ it.workoutTime.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
                && it.workoutTime.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && it.workoutTime.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)}

        binding.trainingScheduleCalendar.setOnDateChangeListener { calendarView, year, month, day ->
            dataset = user.workoutSchedule.workoutList.filter{ it.workoutTime.get(Calendar.DAY_OF_MONTH) == day
                    && it.workoutTime.get(Calendar.MONTH) == month
                    && it.workoutTime.get(Calendar.YEAR) == year}
            val itemAdapter = CalendarTrainingItemAdapter(dataset)
            binding.dayWorkoutsList.adapter = itemAdapter
        }

        val itemAdapter = CalendarTrainingItemAdapter(dataset)
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.dayWorkoutsList.layoutManager = linearLayoutManager
        binding.dayWorkoutsList.adapter = itemAdapter
        binding.dayWorkoutsList.setHasFixedSize(true)

        return binding.root
    }

    companion object{

        private const val SECTION_NUMBER = "section number"

        @JvmStatic
        fun newInstance(sectionNumber: Int): CalendarPlaceholderFragment{
            return CalendarPlaceholderFragment().apply{
                arguments = Bundle().apply{
                    putInt(SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }

}