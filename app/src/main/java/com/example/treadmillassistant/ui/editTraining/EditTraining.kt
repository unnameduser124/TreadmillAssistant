package com.example.treadmillassistant.ui.editTraining

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.Treadmill
import com.example.treadmillassistant.backend.setUpDatePicker
import com.example.treadmillassistant.backend.setUpTimePicker
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.databinding.AddTrainingLayoutBinding
import com.example.treadmillassistant.databinding.TrainingPlanSelectionPopupBinding
import com.example.treadmillassistant.databinding.TreadmillSelectionPopupBinding
import com.example.treadmillassistant.ui.AddTreadmill
import com.example.treadmillassistant.ui.addTraining.AddTreadmillPopupItemAdapter
import com.example.treadmillassistant.ui.addTrainingPlan.AddTrainingPlan
import com.example.treadmillassistant.ui.trainingDetails.TrainingDetailsPage
import java.util.*

class EditTraining: AppCompatActivity() {

    var training: Training? = PlannedTraining()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AddTrainingLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trainingTime.setIs24HourView(true)
        val chosenDate = Calendar.getInstance()

        training = user.trainingSchedule.getTraining(intent.getLongExtra("id", -1))

        if (training == null) {
            onBackPressed()
            return
        } else {
            chosenDate.time = training!!.trainingTime.time
            binding.mediaLink.setText(training!!.mediaLink)
            if(training!!.treadmill.ID==-1L){
                binding.selectedTreadmillName.text = if(user.treadmillList.isEmpty()){
                    selectedTreadmill = training!!.treadmill
                    getString(R.string.no_treadmill)
                }
                else{
                    selectedTreadmill = user.treadmillList.first()
                    selectedTreadmill.name
                }
            }
            else{
                selectedTreadmill = training!!.treadmill
                binding.selectedTreadmillName.text = selectedTreadmill.name
            }
            if(training!!.trainingPlan.ID==-1L){
                binding.selectedTrainingPlanName.text = if(user.trainingPlanList.trainingPlanList.isEmpty()){
                    selectedTrainingPlan = training!!.trainingPlan
                    getString(R.string.no_training_plan)
                } else{
                    selectedTrainingPlan = user.trainingPlanList.trainingPlanList.first()
                    selectedTrainingPlan.name
                }
            }
            else{
                selectedTrainingPlan = training!!.trainingPlan
                binding.selectedTrainingPlanName.text = selectedTrainingPlan.name
            }
            setUpDatePicker(binding.trainingDate, chosenDate)
            setUpTimePicker(binding.trainingTime, chosenDate)
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
                ID = if(user.trainingSchedule.trainingList.isEmpty()) 1L else user.trainingSchedule.trainingList.last().ID + 1L
            )
            user.trainingSchedule.updateTraining(training, newTraining)
            user.trainingSchedule.sortCalendar()

            val intent = Intent(this, TrainingDetailsPage::class.java)
            intent.putExtra("id", training!!.ID)
            finish()
            startActivity(intent)
        }

        binding.addTreadmillButton.setOnClickListener{
            val popupBinding = TreadmillSelectionPopupBinding.inflate(layoutInflater)
            val width = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.MATCH_PARENT
            val focusable = true
            treadmillPopup = PopupWindow(popupBinding.root, width, height, focusable)
            treadmillPopup.contentView = popupBinding.root
            treadmillPopup.showAtLocation(binding.addTreadmillButton, Gravity.CENTER, 0, 0)

            val itemAdapter = AddTreadmillPopupItemAdapter(user.treadmillList, false, training!!.ID)
            val linearLayoutManager = LinearLayoutManager(popupBinding.treadmillSearchList.context, RecyclerView.VERTICAL, false)
            popupBinding.treadmillSearchList.adapter = itemAdapter
            popupBinding.treadmillSearchList.layoutManager = linearLayoutManager
            popupBinding.treadmillSearchList.setHasFixedSize(false)

            popupBinding.treadmillSelectionCancelButton.setOnClickListener { treadmillPopup.dismiss() }

            treadmillPopup.setOnDismissListener {
                if(selectedTreadmill.ID!=-1L){
                    binding.selectedTreadmillName.text = selectedTreadmill.name
                }
            }

            popupBinding.treadmillSelectionAddNewButton.setOnClickListener {
                val intent = Intent(this, AddTreadmill::class.java)
                intent.putExtra("fromEditTraining", true)
                intent.putExtra("id", training?.ID ?: -1)
                startActivity(intent)
            }

            popupBinding.treadmillSearchInput.addTextChangedListener {typedText: Editable? ->
                val filteredList = user.treadmillList.filter{ it.name.lowercase().contains(typedText.toString().lowercase()) }.toMutableList()
                val newAdapter = AddTreadmillPopupItemAdapter(filteredList, false, training!!.ID)
                popupBinding.treadmillSearchList.adapter = newAdapter
            }
        }

        binding.addTrainingPlanButton.setOnClickListener {

            val popupBinding = TrainingPlanSelectionPopupBinding.inflate(layoutInflater)
            val width = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.MATCH_PARENT
            val focusable = true
            popupWindow = PopupWindow(popupBinding.root, width, height, focusable)
            popupWindow.contentView = popupBinding.root
            popupWindow.showAtLocation(binding.addTrainingPlanButton, Gravity.CENTER, 0, 0)

            val itemAdapter = EditTrainingPlanPopupItemAdapter(user.trainingPlanList.trainingPlanList, false, training!!.ID)
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
                intent.putExtra("fromEditTraining", true)
                intent.putExtra("id", training?.ID ?: -1)
                startActivity(intent)
                popupWindow.dismiss()
            }

            popupBinding.trainingPlanSearchInput.addTextChangedListener {typedText: Editable? ->
                val filteredList = user.trainingPlanList.trainingPlanList.filter{ it.name.lowercase().contains(typedText.toString().lowercase()) }.toMutableList()
                val newAdapter = EditTrainingPlanPopupItemAdapter(filteredList, false, training!!.ID)

                popupBinding.trainingPlanSearchList.adapter = newAdapter
            }
        }
    }

    companion object {
        var selectedTrainingPlan: TrainingPlan = TrainingPlan()
        var selectedTreadmill: Treadmill = Treadmill()
        var popupWindow: PopupWindow = PopupWindow()
        var treadmillPopup: PopupWindow = PopupWindow()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, TrainingDetailsPage::class.java)
        if(training!=null){
            intent.putExtra("id", training!!.ID)
        }
        finish()
        startActivity(intent)
    }
}