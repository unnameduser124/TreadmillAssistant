package com.example.treadmillassistant.ui.trainingHistory

import android.content.res.Resources
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.NumberPicker
import android.widget.PopupWindow
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingService
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.FragmentTrainingHistoryBinding
import com.example.treadmillassistant.databinding.MonthPickerPopupBinding
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread

class TrainingHistoryFragment : Fragment() {

    private var month = Calendar.getInstance().get(Calendar.MONTH)
    private var year = Calendar.getInstance().get(Calendar.YEAR)
    private var _binding: FragmentTrainingHistoryBinding? = null
    private var cal = Calendar.getInstance()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentTrainingHistoryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        updateDateButton(cal)

        //item adapter with training list sorted from newest to oldest as dataset
        val loaded: MutableLiveData<Boolean> by lazy{
            MutableLiveData<Boolean>()
        }

        thread{
            reloadTrainingList(Calendar.getInstance())
            loaded.postValue(true)
        }

        val observer = Observer<Boolean>{
            if(it){
                val itemAdapter = TrainingHistoryItemAdapter(user.trainingSchedule.trainingList)
                binding.trainingHistoryTrainingList.adapter = itemAdapter
                binding.trainingHistoryTrainingList.setHasFixedSize(true)
            }
        }
        loaded.observe(viewLifecycleOwner, observer)

        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)
        binding.trainingHistoryTrainingList.layoutManager = linearLayoutManager

        binding.monthPickedButton.setOnClickListener {
            val popupBinding = MonthPickerPopupBinding.inflate(layoutInflater)

            val width = LinearLayout.LayoutParams.WRAP_CONTENT
            val height = LinearLayout.LayoutParams.WRAP_CONTENT
            val focusable = true
            val popupWindow = PopupWindow(popupBinding.root, width, height, focusable)
            popupWindow.contentView = popupBinding.root
            popupBinding.datePicker.maxDate = Calendar.getInstance().timeInMillis
            popupBinding.datePicker.updateDate(year, month, 1)

            val daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android")
            if(daySpinnerId!=0){
                val daySpinner = popupBinding.datePicker.findViewById<NumberPicker>(daySpinnerId)
                daySpinner.isVisible = false
            }

            popupWindow.showAtLocation(binding.monthPickedButton, Gravity.CENTER, 0, 0)

            popupBinding.dateConfirmButton.setOnClickListener {
                cal.set(Calendar.YEAR, popupBinding.datePicker.year)
                cal.set(Calendar.MONTH, popupBinding.datePicker.month)
                val loaded: MutableLiveData<Boolean> by lazy{
                    MutableLiveData<Boolean>()
                }

                thread{
                    reloadTrainingList(cal)
                    loaded.postValue(true)
                }

                val observer = Observer<Boolean>{
                    if(it){
                        val itemAdapter = TrainingHistoryItemAdapter(user.trainingSchedule.trainingList)
                        binding.trainingHistoryTrainingList.adapter = itemAdapter
                        binding.trainingHistoryTrainingList.setHasFixedSize(true)
                    }
                }
                loaded.observe(viewLifecycleOwner, observer)

                popupWindow.dismiss()
                updateDateButton(cal)
            }
        }

        return root
    }

    private fun updateDateButton(calendar: Calendar){
        val monthFormat = SimpleDateFormat("MMMM", Locale.US)
        val monthName = monthFormat.format(calendar.time)
        binding.monthPickedButton.text = String.format(getString(R.string.month_and_year), monthName, calendar.get(Calendar.YEAR).toString())
    }

    private fun reloadTrainingList(newCalendar: Calendar){
        user.trainingSchedule.trainingList.clear()
        val allTrainingsPair = ServerTrainingService().getTrainingsForMonth(newCalendar, 0, 10)
        allTrainingsPair.second.forEach {
            user.trainingSchedule.trainingList.add(PlannedTraining(it))
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}