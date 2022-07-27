package com.example.treadmillassistant


import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.backend.Treadmill
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.workout.Workout
import com.example.treadmillassistant.backend.workout.WorkoutPlan
import com.example.treadmillassistant.backend.workout.WorkoutStatus
import com.example.treadmillassistant.databinding.AddWorkoutLayoutBinding
import com.example.treadmillassistant.ui.addworkoutplan.AddWorkoutPlan
import java.util.*

class AddWorkout: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AddWorkoutLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.workoutTime.setIs24HourView(true)

        var selectedTreadmill: Treadmill
        var selectedWorkoutPlan: WorkoutPlan
        val chosenDate = Date()
        chosenDate.time = intent.getLongExtra("date", Calendar.getInstance().timeInMillis)
        setUpDatePicker(binding.workoutDate, chosenDate)
        setUpTimePicker(binding.workoutTime, chosenDate)

        val treadmillDropdownAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, user.getTreadmillNames())
        treadmillDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.treadmillSelection.adapter = treadmillDropdownAdapter
        selectedTreadmill = user.treadmillList.first()

        val workoutPlanDropdownAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, user.workoutPlanList.getWorkoutPlanNames())
        workoutPlanDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.workoutPlanSelection.adapter = workoutPlanDropdownAdapter
        selectedWorkoutPlan = user.workoutPlanList.workoutPlanList.first()

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

        binding.workoutPlanSelection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>?,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                selectedWorkoutPlan = user.workoutPlanList.workoutPlanList[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }


        binding.saveNewWorkoutButton.setOnClickListener{
            var dateCal = Calendar.getInstance()
            dateCal.set(Calendar.YEAR, binding.workoutDate.year)
            dateCal.set(Calendar.MONTH, binding.workoutDate.month)
            dateCal.set(Calendar.DAY_OF_MONTH, binding.workoutDate.dayOfMonth-1)
            dateCal.set(Calendar.HOUR, binding.workoutTime.hour)
            dateCal.set(Calendar.MINUTE, binding.workoutTime.minute)
            val date = dateCal.time
            date.year = Calendar.getInstance().get(Calendar.YEAR)
            date.hours = binding.workoutTime.hour


            var newWorkout = Workout(date, selectedTreadmill, binding.mediaLink.text.toString(), WorkoutStatus.Upcoming, selectedWorkoutPlan)
            user.workoutSchedule.addNewWorkout(newWorkout)
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }

        binding.addWorkoutPlanButton.setOnClickListener {
            val intent = Intent(this, AddWorkoutPlan::class.java)
            intent.putExtra("fromWorkout", true)
            var dateCal = Calendar.getInstance()
            dateCal.set(Calendar.YEAR, binding.workoutDate.year)
            dateCal.set(Calendar.MONTH, binding.workoutDate.month)
            dateCal.set(Calendar.DAY_OF_MONTH, binding.workoutDate.dayOfMonth-1)
            dateCal.set(Calendar.HOUR, binding.workoutTime.hour)
            dateCal.set(Calendar.MINUTE, binding.workoutTime.minute)
            val date = dateCal.time
            date.year = Calendar.getInstance().get(Calendar.YEAR)
            date.hours = binding.workoutTime.hour
            intent.putExtra("date", date.time)
            startActivity(intent)
        }
    }

    fun setUpDatePicker(datePicker: DatePicker, date: Date){
        if(date.year<2000)
            datePicker.updateDate(date.year+1900, date.month, date.date)
        else
            datePicker.updateDate(date.year, date.month, date.date)
    }
    fun setUpTimePicker(timePicker: TimePicker, date: Date){
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