package com.example.treadmillassistant.ui.editTraining

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
import com.example.treadmillassistant.ui.addTraining.AddTraining
import com.example.treadmillassistant.ui.addTrainingPlan.AddTrainingPlan
import com.example.treadmillassistant.ui.trainingDetails.TrainingDetailsPage
import java.util.*

class EditTraining: AppCompatActivity() {

    var item: Training? = PlannedTraining()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AddTrainingLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trainingTime.setIs24HourView(true)

        var selectedTreadmill: Treadmill
        val chosenDate = Calendar.getInstance()

        item = user.trainingSchedule.getTraining(intent.getLongExtra("id", -1))

        if (item == null) {
            onBackPressed()
            return
        } else {
            chosenDate.time = item!!.trainingTime.time
            binding.mediaLink.setText(item!!.mediaLink)
            selectedTreadmill = item!!.treadmill
            selectedTrainingPlan = item!!.trainingPlan
            binding.selectedTrainingPlanName.text = item!!.trainingPlan.name
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
                ID = user.trainingSchedule.trainingList.last().ID + 1
            )
            user.trainingSchedule.updateTraining(item, newTraining)
            user.trainingSchedule.sortCalendar()

            val intent = Intent(this, TrainingDetailsPage::class.java)
            intent.putExtra("id", item!!.ID)
            finish()
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

            val itemAdapter = EditTrainingPlanPopupItemAdapter(user.trainingPlanList.trainingPlanList, false, item!!.ID)
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
                intent.putExtra("fromTraining", false)

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
                val newAdapter = EditTrainingPlanPopupItemAdapter(filteredList, false, item!!.ID)

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
        val intent = Intent(this, TrainingDetailsPage::class.java)
        if(item!=null){
            intent.putExtra("id", item!!.ID)
        }
        finish()
        startActivity(intent)
    }
}