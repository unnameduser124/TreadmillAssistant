package com.example.treadmillassistant.ui

import android.R
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
import com.example.treadmillassistant.ui.trainingDetails.TrainingDetailsPage
import java.util.*

class EditWorkout: AppCompatActivity() {

    var item: Workout? = Workout()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AddWorkoutLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.workoutTime.setIs24HourView(true)

        var selectedTreadmill: Treadmill = user.treadmillList.first()
        var selectedWorkoutPlan: WorkoutPlan = user.workoutPlanList.workoutPlanList.first()
        val chosenDate = Date()

        val treadmillDropdownAdapter =
            ArrayAdapter(
                this,
                R.layout.simple_spinner_item,
                user.getTreadmillNames()
            )
        treadmillDropdownAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.treadmillSelection.adapter = treadmillDropdownAdapter

        val workoutPlanDropdownAdapter =
            ArrayAdapter(
            this,
            R.layout.simple_spinner_item,
            user.workoutPlanList.getWorkoutPlanNames()
        )
        workoutPlanDropdownAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_item)
        binding.workoutPlanSelection.adapter = workoutPlanDropdownAdapter

        item = user.workoutSchedule.getWorkout(intent.getLongExtra("id", -1))

        if (item == null) {
            onBackPressed()
        } else {
            chosenDate.time = item!!.workoutTime.timeInMillis
            binding.mediaLink.setText(item!!.mediaLink)
            selectedTreadmill = item!!.treadmill
            selectedWorkoutPlan = item!!.workoutPlan
            binding.workoutPlanSelection.setSelection(
                user.workoutPlanList.workoutPlanList.indexOf(
                    selectedWorkoutPlan
                )
            )
            binding.treadmillSelection.setSelection(user.treadmillList.indexOf(selectedTreadmill))
        }

        setUpDatePicker(binding.workoutDate, chosenDate)
        setUpTimePicker(binding.workoutTime, chosenDate)



        binding.treadmillSelection.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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

        binding.workoutPlanSelection.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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


        binding.saveNewWorkoutButton.setOnClickListener {
            var dateCal = Calendar.getInstance()
            dateCal.set(Calendar.YEAR, binding.workoutDate.year)
            dateCal.set(Calendar.MONTH, binding.workoutDate.month)
            dateCal.set(Calendar.DAY_OF_MONTH, binding.workoutDate.dayOfMonth)
            dateCal.set(Calendar.HOUR_OF_DAY, binding.workoutTime.hour)
            dateCal.set(Calendar.MINUTE, binding.workoutTime.minute)

            var newWorkout = Workout(
                dateCal,
                selectedTreadmill,
                binding.mediaLink.text.toString(),
                WorkoutStatus.Upcoming,
                selectedWorkoutPlan,
                ID = user.workoutSchedule.workoutList.last().ID + 1
            )
            user.workoutSchedule.updateWorkout(item, newWorkout)
            user.workoutSchedule.sortCalendar()

            val intent = Intent(this, TrainingDetailsPage::class.java)
            intent.putExtra("id", item!!.ID)
            finish()
            startActivity(intent)
        }

        binding.addWorkoutPlanButton.setOnClickListener {
            val intent = Intent(this, AddWorkoutPlan::class.java)
            intent.putExtra("fromWorkout", true)
            var dateCal = Calendar.getInstance()
            dateCal.set(Calendar.YEAR, binding.workoutDate.year)
            dateCal.set(Calendar.MONTH, binding.workoutDate.month)
            dateCal.set(Calendar.DAY_OF_MONTH, binding.workoutDate.dayOfMonth)
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
        val intent = Intent(this, TrainingDetailsPage::class.java)
        if(item!=null){
            intent.putExtra("id", item!!.ID)
        }
        finish()
        startActivity(intent)
    }
}