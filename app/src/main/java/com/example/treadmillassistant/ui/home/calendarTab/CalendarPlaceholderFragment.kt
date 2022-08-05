package com.example.treadmillassistant.ui.home.calendarTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.CalendarTabBinding
import com.example.treadmillassistant.ui.home.PageViewModel
import java.util.*

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
    ): View {
        val binding = CalendarTabBinding.inflate(layoutInflater)
        val calendar = Calendar.getInstance()
        var dataset = user.trainingSchedule.trainingLists.filter{ it.trainingTime.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
                && it.trainingTime.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && it.trainingTime.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)}

        binding.trainingScheduleCalendar.setOnDateChangeListener { _, year, month, day ->
            val newCalendar = Calendar.getInstance()
            newCalendar.set(Calendar.YEAR, year)
            newCalendar.set(Calendar.MONTH, month)
            newCalendar.set(Calendar.DAY_OF_MONTH, day)
            dataset = user.trainingSchedule.trainingLists.filter{ it.trainingTime.get(Calendar.DAY_OF_MONTH) == newCalendar.get(Calendar.DAY_OF_MONTH)
                    && it.trainingTime.get(Calendar.MONTH) == newCalendar.get(Calendar.MONTH)
                    && it.trainingTime.get(Calendar.YEAR) == newCalendar.get(Calendar.YEAR)}
            val itemAdapter = CalendarTrainingItemAdapter(dataset)
            binding.dayTrainingsList.adapter = itemAdapter
        }

        val itemAdapter = CalendarTrainingItemAdapter(dataset)
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.dayTrainingsList.layoutManager = linearLayoutManager
        binding.dayTrainingsList.adapter = itemAdapter
        binding.dayTrainingsList.setHasFixedSize(true)

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