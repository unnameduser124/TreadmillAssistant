package com.example.treadmillassistant.ui.addTraining


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.Treadmill
import com.example.treadmillassistant.backend.setUpDatePicker
import com.example.treadmillassistant.backend.setUpTimePicker
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.databinding.AddTrainingLayoutBinding
import com.example.treadmillassistant.databinding.TrainingPlanSelectionPopupBinding
import com.example.treadmillassistant.ui.addTrainingPlan.AddTrainingPlan
import java.util.*

class AddTraining: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AddTrainingLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trainingTime.setIs24HourView(true)

        var selectedTreadmill: Treadmill
        val chosenDate = Calendar.getInstance()
        chosenDate.timeInMillis = intent.getLongExtra("date", Calendar.getInstance().timeInMillis)
        setUpDatePicker(binding.trainingDate, chosenDate)
        setUpTimePicker(binding.trainingTime, chosenDate)

        val treadmillDropdownAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, user.getTreadmillNames())
        treadmillDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.treadmillSelection.adapter = treadmillDropdownAdapter
        selectedTreadmill = if(user.treadmillList.isEmpty()){
            Treadmill()
        } else{
            user.treadmillList.first()
        }

        binding.treadmillSelection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                selectedTreadmill = user.treadmillList[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }

        binding.selectedTrainingPlanName.text = if(user.trainingPlanList.trainingPlanList.isEmpty()){
            getString(R.string.no_training_plan)
        } else{
            user.trainingPlanList.trainingPlanList.first().name
        }

        binding.saveNewTrainingButton.setOnClickListener{
            val dateCal = Calendar.getInstance()
            dateCal.set(Calendar.YEAR, binding.trainingDate.year)
            dateCal.set(Calendar.MONTH, binding.trainingDate.month)
            dateCal.set(Calendar.DAY_OF_MONTH, binding.trainingDate.dayOfMonth)
            dateCal.set(Calendar.HOUR_OF_DAY, binding.trainingTime.hour)
            dateCal.set(Calendar.MINUTE, binding.trainingTime.minute)


            user.trainingSchedule.trainingList.sortBy{it.ID}
            val newTraining = PlannedTraining(dateCal, selectedTreadmill, binding.mediaLink.text.toString(), TrainingStatus.Upcoming,
                selectedTrainingPlan, ID =user.trainingSchedule.trainingList.last().ID+1)
            user.trainingSchedule.addNewTraining(newTraining)
            user.trainingSchedule.sortCalendar()

            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }

        binding.addTrainingPlanButton.setOnClickListener {

            val popupBinding = TrainingPlanSelectionPopupBinding.inflate(layoutInflater)

            val width = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.MATCH_PARENT
            val focusable = true
            popupWindow = PopupWindow(popupBinding.root, width, height, focusable)
            popupWindow.contentView = popupBinding.root
            popupWindow.showAtLocation(binding.addTrainingPlanButton, Gravity.CENTER, 0, 0)
            val itemAdapter = AddTrainingPlanPopupItemAdapter(user.trainingPlanList.trainingPlanList, true)
            val linearLayoutManager = LinearLayoutManager(popupBinding.trainingPlanSearchList.context, RecyclerView.VERTICAL, false)
            popupBinding.trainingPlanSearchList.adapter = itemAdapter
            popupBinding.trainingPlanSearchList.layoutManager = linearLayoutManager
            popupBinding.trainingPlanSearchList.setHasFixedSize(false)

            popupBinding.trainingPlanSelectionCancelButton.setOnClickListener { popupWindow.dismiss() }

            popupWindow.setOnDismissListener {
                popupWindow.dismiss()
                if(selectedTrainingPlan.ID!=-1L){
                    binding.selectedTrainingPlanName.text = selectedTrainingPlan.name
                }
            }

            popupBinding.trainingPlanSelectionAddNewButton.setOnClickListener {
                val intent = Intent(this, AddTrainingPlan::class.java)
                intent.putExtra("fromTraining", true)
                val dateCal = Calendar.getInstance()
                dateCal.set(Calendar.YEAR, binding.trainingDate.year)
                dateCal.set(Calendar.MONTH, binding.trainingDate.month)
                dateCal.set(Calendar.DAY_OF_MONTH, binding.trainingDate.dayOfMonth)
                dateCal.set(Calendar.HOUR_OF_DAY, binding.trainingTime.hour)
                dateCal.set(Calendar.MINUTE, binding.trainingTime.minute)
                intent.putExtra("date", dateCal.time)
                startActivity(intent)
                popupWindow.dismiss()
            }

            popupBinding.trainingPlanSearchInput.addTextChangedListener {typedText: Editable? ->
                val filteredList = user.trainingPlanList.trainingPlanList.filter{ it.name.lowercase().contains(typedText.toString().lowercase()) }.toMutableList()
                val newAdapter = AddTrainingPlanPopupItemAdapter(filteredList, true)

                popupBinding.trainingPlanSearchList.adapter = newAdapter
            }
        }
    }

    companion object {
        var selectedTrainingPlan: TrainingPlan = TrainingPlan()
        var popupWindow: PopupWindow = PopupWindow()
    }
    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)
    }
}