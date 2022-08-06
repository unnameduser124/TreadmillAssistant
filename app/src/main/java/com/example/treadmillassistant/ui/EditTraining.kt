package com.example.treadmillassistant.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.treadmillassistant.backend.Treadmill
import com.example.treadmillassistant.backend.setUpDatePicker
import com.example.treadmillassistant.backend.setUpTimePicker
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.databinding.AddWorkoutLayoutBinding
import com.example.treadmillassistant.ui.addTrainingPlan.AddTrainingPlan
import com.example.treadmillassistant.ui.trainingDetails.TrainingDetailsPage
import java.util.*

class EditTraining: AppCompatActivity() {

    var item: Training? = PlannedTraining()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AddWorkoutLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trainingTime.setIs24HourView(true)

        var selectedTreadmill: Treadmill = user.treadmillList.first()
        var selectedTrainingPlan: TrainingPlan = user.trainingPlanList.trainingPlanLists.first()

        val chosenDate = Calendar.getInstance()



        item = user.trainingSchedule.getTraining(intent.getLongExtra("id", -1))

        if (item == null) {
            onBackPressed()
        } else {
            chosenDate.time = item!!.trainingTime.time
            binding.mediaLink.setText(item!!.mediaLink)
            selectedTreadmill = item!!.treadmill
            selectedTrainingPlan = item!!.trainingPlan
            binding.trainingPlanSelection.setSelection(
                user.trainingPlanList.trainingPlanLists.indexOf(
                    selectedTrainingPlan
                )
            )
            binding.treadmillSelection.setSelection(user.treadmillList.indexOf(selectedTreadmill))
        }

        //treadmill dropdown
        val treadmillDropdownAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                user.getTreadmillNames()
            )
        treadmillDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.treadmillSelection.adapter = treadmillDropdownAdapter

        //training plan dropdown
        val trainingPlanDropdownAdapter =
            ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                user.trainingPlanList.getTrainingPlanNames()
            )
        trainingPlanDropdownAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.trainingPlanSelection.adapter = trainingPlanDropdownAdapter

        setUpDatePicker(binding.trainingDate, chosenDate)
        setUpTimePicker(binding.trainingTime, chosenDate)



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

        binding.trainingPlanSelection.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
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


        binding.saveNewTrainingButton.setOnClickListener {
            val dateCal = Calendar.getInstance()
            dateCal.set(Calendar.YEAR, binding.trainingDate.year)
            dateCal.set(Calendar.MONTH, binding.trainingDate.month)
            dateCal.set(Calendar.DAY_OF_MONTH, binding.trainingDate.dayOfMonth)
            dateCal.set(Calendar.HOUR_OF_DAY, binding.trainingTime.hour)
            dateCal.set(Calendar.MINUTE, binding.trainingTime.minute)

            val newTraining = PlannedTraining(
                dateCal,
                selectedTreadmill,
                binding.mediaLink.text.toString(),
                TrainingStatus.Upcoming,
                selectedTrainingPlan,
                ID = user.trainingSchedule.trainingLists.last().ID + 1
            )
            user.trainingSchedule.updateTraining(item, newTraining)
            user.trainingSchedule.sortCalendar()

            val intent = Intent(this, TrainingDetailsPage::class.java)
            intent.putExtra("id", item!!.ID)
            finish()
            startActivity(intent)
        }

        binding.addTrainingPlanButton.setOnClickListener {
            val intent = Intent(this, AddTrainingPlan::class.java)
            intent.putExtra("fromTraining", true)
            val dateCal = Calendar.getInstance()
            dateCal.set(Calendar.YEAR, binding.trainingDate.year)
            dateCal.set(Calendar.MONTH, binding.trainingDate.month)
            dateCal.set(Calendar.DAY_OF_MONTH, binding.trainingDate.dayOfMonth)
            dateCal.set(Calendar.HOUR, binding.trainingTime.hour)
            dateCal.set(Calendar.MINUTE, binding.trainingTime.minute)

            intent.putExtra("date", dateCal.timeInMillis)
            startActivity(intent)
        }
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