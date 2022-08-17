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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.databinding.FragmentTrainingHistoryBinding
import com.example.treadmillassistant.databinding.MonthPickerPopupBinding
import java.text.SimpleDateFormat
import java.util.*

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
        var itemAdapter = TrainingHistoryItemAdapter(user.trainingSchedule.getHistoryTrainingsForMonth(cal))

        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.VERTICAL, false)

        binding.trainingHistoryTrainingList.adapter = itemAdapter
        binding.trainingHistoryTrainingList.layoutManager = linearLayoutManager
        binding.trainingHistoryTrainingList.setHasFixedSize(true)

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
                itemAdapter = TrainingHistoryItemAdapter(user.trainingSchedule.getHistoryTrainingsForMonth(cal))
                binding.trainingHistoryTrainingList.adapter = itemAdapter

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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}