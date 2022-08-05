package com.example.treadmillassistant


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.backend.Treadmill
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.databinding.AddWorkoutLayoutBinding
import com.example.treadmillassistant.ui.addTrainingPlan.AddTrainingPlan
import java.util.*

class AddTraining: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AddWorkoutLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.trainingTime.setIs24HourView(true)

        var selectedTreadmill: Treadmill
        var selectedTrainingPlan: TrainingPlan
        val chosenDate = Date()
        chosenDate.time = intent.getLongExtra("date", Calendar.getInstance().timeInMillis)
        setUpDatePicker(binding.trainingDate, chosenDate)
        setUpTimePicker(binding.trainingTime, chosenDate)

        val treadmillDropdownAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, user.getTreadmillNames())
        treadmillDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.treadmillSelection.adapter = treadmillDropdownAdapter
        if(user.treadmillList.isEmpty()){
            selectedTreadmill=Treadmill()
        }
        else{
            selectedTreadmill = user.treadmillList.first()
        }

        val workoutPlanDropdownAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, user.trainingPlanList.getTrainingPlanNames())
        workoutPlanDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.trainingPlanSelection.adapter = workoutPlanDropdownAdapter
        selectedTrainingPlan = user.trainingPlanList.trainingPlanLists.first()

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

        binding.trainingPlanSelection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                selectedTrainingPlan = user.trainingPlanList.trainingPlanLists[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }


        binding.saveNewTrainingButton.setOnClickListener{
            var dateCal = Calendar.getInstance()
            dateCal.set(Calendar.YEAR, binding.trainingDate.year)
            dateCal.set(Calendar.MONTH, binding.trainingDate.month)
            dateCal.set(Calendar.DAY_OF_MONTH, binding.trainingDate.dayOfMonth)
            dateCal.set(Calendar.HOUR_OF_DAY, binding.trainingTime.hour)
            dateCal.set(Calendar.MINUTE, binding.trainingTime.minute)


            user.trainingSchedule.trainingLists.sortBy{it.ID}
            var newTraining = PlannedTraining(dateCal, selectedTreadmill, binding.mediaLink.text.toString(), TrainingStatus.Upcoming,
                selectedTrainingPlan, ID =user.trainingSchedule.trainingLists.last().ID+1)
            user.trainingSchedule.addNewTraining(newTraining)
            user.trainingSchedule.sortCalendar()

            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }

        binding.addTrainingPlanButton.setOnClickListener {
            val intent = Intent(this, AddTrainingPlan::class.java)
            intent.putExtra("fromWorkout", true)
            var dateCal = Calendar.getInstance()
            dateCal.set(Calendar.YEAR, binding.trainingDate.year)
            dateCal.set(Calendar.MONTH, binding.trainingDate.month)
            dateCal.set(Calendar.DAY_OF_MONTH, binding.trainingDate.dayOfMonth)
            dateCal.set(Calendar.HOUR_OF_DAY, binding.trainingTime.hour)
            dateCal.set(Calendar.MINUTE, binding.trainingTime.minute)
            intent.putExtra("date", dateCal.time)
            startActivity(intent)
        }
    }

    private fun setUpDatePicker(datePicker: DatePicker, date: Date){
        if(date.year<2000)
            datePicker.updateDate(date.year+1900, date.month, date.date)
        else
            datePicker.updateDate(date.year, date.month, date.date)
    }
    private fun setUpTimePicker(timePicker: TimePicker, date: Date){
        timePicker.hour = date.hours
        timePicker.minute = date.minutes
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)
    }
}