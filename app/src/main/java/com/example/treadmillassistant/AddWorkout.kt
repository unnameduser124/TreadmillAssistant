package com.example.treadmillassistant


import android.content.Intent
import android.os.Build.VERSION_CODES.P
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.treadmillassistant.backend.Treadmill
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.workout.Workout
import com.example.treadmillassistant.backend.workout.WorkoutPlan
import com.example.treadmillassistant.backend.workout.WorkoutStatus
import com.example.treadmillassistant.backend.workoutPlanList
import com.example.treadmillassistant.databinding.AddWorkoutLayoutBinding
import java.util.*

class AddWorkout: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AddWorkoutLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.workoutTime.setIs24HourView(true)

        var selectedTreadmill: Treadmill
        var selectedWorkoutPlan: WorkoutPlan

        val treadmillDropdownAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, user.getTreadmillNames())
        treadmillDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.treadmillSelection.adapter = treadmillDropdownAdapter
        selectedTreadmill = user.treadmillList.first()

        val workoutPlanDropdownAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, workoutPlanList.getWorkoutPlanNames())
        workoutPlanDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.workoutPlanSelection.adapter = workoutPlanDropdownAdapter
        selectedWorkoutPlan = workoutPlanList.workoutPlanList.first()

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
                selectedWorkoutPlan = workoutPlanList.workoutPlanList[position]
            }

            override fun onNothingSelected(parentView: AdapterView<*>?) {}
        }


        binding.saveNewWorkoutButton.setOnClickListener{
            var dateCal = Calendar.getInstance()
            dateCal.set(Calendar.YEAR, binding.workoutDate.year)
            dateCal.set(Calendar.MONTH, binding.workoutDate.month)
            dateCal.set(Calendar.DAY_OF_MONTH, binding.workoutDate.dayOfMonth)
            dateCal.set(Calendar.HOUR, binding.workoutTime.hour)
            dateCal.set(Calendar.MINUTE, binding.workoutTime.minute)
            val date = dateCal.time
            date.year = Calendar.getInstance().get(Calendar.YEAR)
            println(date)


            var newWorkout = Workout(date, selectedTreadmill, binding.mediaLink.text.toString(), WorkoutStatus.Upcoming, selectedWorkoutPlan)
            user.workoutSchedule.addNewWorkout(newWorkout)
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }
    }
}