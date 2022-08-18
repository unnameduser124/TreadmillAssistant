package com.example.treadmillassistant.ui.addTraining


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.databinding.AddTrainingLayoutBinding
import com.example.treadmillassistant.databinding.TrainingPlanSelectionPopupBinding
import com.example.treadmillassistant.databinding.TreadmillSelectionPopupBinding
import com.example.treadmillassistant.ui.AddTreadmill
import com.example.treadmillassistant.ui.addTrainingPlan.AddTrainingPlan
import com.example.treadmillassistant.ui.editTraining.EditTraining.Companion.selectedTrainingPlan
import com.example.treadmillassistant.ui.editTraining.EditTrainingPlanPopupItemAdapter
import com.example.treadmillassistant.ui.home.trainingTab.TrainingTabPlaceholderFragment.Companion.training
import java.util.*

class AddTraining: AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AddTrainingLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trainingTime.setIs24HourView(true)

        val chosenDate = Calendar.getInstance()
        chosenDate.timeInMillis = intent.getLongExtra("date", Calendar.getInstance().timeInMillis)
        setUpDatePicker(binding.trainingDate, chosenDate)
        setUpTimePicker(binding.trainingTime, chosenDate)



        binding.selectedTrainingPlanName.text = if(user.trainingPlanList.trainingPlanList.isEmpty()){
            getString(R.string.no_training_plan)
        } else{
            selectedTrainingPlan = user.trainingPlanList.trainingPlanList.first()
            user.trainingPlanList.trainingPlanList.first().name
        }

        binding.selectedTreadmillName.text = if(user.treadmillList.isEmpty()){
            selectedTreadmill = Treadmill()
            getString(R.string.no_treadmill)
        }
        else{
            selectedTreadmill= user.treadmillList.first()
            user.treadmillList.first().name

        }

        binding.saveNewTrainingButton.setOnClickListener{
            if(selectedTreadmill.ID != -1L){
                val dateCal = Calendar.getInstance()
                dateCal.set(Calendar.YEAR, binding.trainingDate.year)
                dateCal.set(Calendar.MONTH, binding.trainingDate.month)
                dateCal.set(Calendar.DAY_OF_MONTH, binding.trainingDate.dayOfMonth)
                dateCal.set(Calendar.HOUR_OF_DAY, binding.trainingTime.hour)
                dateCal.set(Calendar.MINUTE, binding.trainingTime.minute)

                user.trainingSchedule.trainingList.sortBy{it.ID}
                val newTraining = PlannedTraining(
                    dateCal,
                    selectedTreadmill,
                    binding.mediaLink.text.toString(),
                    TrainingStatus.Upcoming,
                    selectedTrainingPlan,
                    ID =user.trainingSchedule.trainingList.last().ID+1
                )
                user.trainingSchedule.addNewTraining(newTraining)
                user.trainingSchedule.sortCalendar()

                val intent = Intent(this, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Pick a treadmill", Toast.LENGTH_SHORT).show()
            }
        }

        binding.addTreadmillButton.setOnClickListener{
            val popupBinding = TreadmillSelectionPopupBinding.inflate(layoutInflater)

            val width = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.MATCH_PARENT
            val focusable = true
            treadmillPopup = PopupWindow(popupBinding.root, width, height, focusable)
            treadmillPopup.contentView = popupBinding.root
            treadmillPopup.showAtLocation(binding.addTreadmillButton, Gravity.CENTER, 0, 0)

            val itemAdapter = AddTreadmillPopupItemAdapter(user.treadmillList, true)
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
                intent.putExtra("fromTraining", true)
                val dateCal = Calendar.getInstance()
                dateCal.set(Calendar.YEAR, binding.trainingDate.year)
                dateCal.set(Calendar.MONTH, binding.trainingDate.month)
                dateCal.set(Calendar.DAY_OF_MONTH, binding.trainingDate.dayOfMonth)
                dateCal.set(Calendar.HOUR_OF_DAY, binding.trainingTime.hour)
                dateCal.set(Calendar.MINUTE, binding.trainingTime.minute)
                intent.putExtra("date", dateCal.time)
                startActivity(intent)
            }

            popupBinding.treadmillSearchInput.addTextChangedListener {typedText: Editable? ->
                val filteredList = user.treadmillList.filter{ it.name.lowercase().contains(typedText.toString().lowercase()) }.toMutableList()
                val newAdapter = AddTreadmillPopupItemAdapter(filteredList, true)
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


            var start = 0
            var baseList = user.trainingPlanList.trainingPlanList
            var list = getTrainingPlansWithPagination(start, SELECT_TRAINING_PLAN_LIST_SIZE, baseList)
            val itemAdapter = AddTrainingPlanPopupItemAdapter(
                list,
                true
            )
            start= SELECT_TRAINING_PLAN_LIST_SIZE
            val linearLayoutManager = LinearLayoutManager(popupBinding.trainingPlanSearchList.context, RecyclerView.VERTICAL, false)
            popupBinding.trainingPlanSearchList.adapter = itemAdapter
            popupBinding.trainingPlanSearchList.layoutManager = linearLayoutManager
            popupBinding.trainingPlanSearchList.setHasFixedSize(false)


            popupBinding.trainingPlanSearchList.setOnScrollChangeListener { _, _, _, _, _ ->
                if(list.size>= SELECT_TRAINING_PLAN_LIST_SIZE && list.size<baseList.size){
                    if((popupBinding.trainingPlanSearchList.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == list.size - 1){
                        val addList = getTrainingPlansWithPagination(
                            start,
                            SELECT_TRAINING_PLAN_LIST_LOAD_LIMIT,
                            baseList
                        ).toMutableList()

                        if(addList.size>0){
                            list += addList
                            start += SELECT_TRAINING_PLAN_LIST_LOAD_LIMIT

                            popupBinding.trainingPlanSearchList.post {
                                popupBinding.trainingPlanSearchList.adapter?.notifyItemRangeInserted(
                                    list.size - addList.size,
                                    addList.size
                                )
                                println(list.size)
                            }
                        }
                    }
                }
            }


            popupBinding.trainingPlanSelectionCancelButton.setOnClickListener { popupWindow.dismiss() }

            popupWindow.setOnDismissListener {
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
                baseList = user.trainingPlanList.trainingPlanList.filter{
                    it.name.lowercase().contains(typedText.toString().lowercase())
                }.toMutableList()
                start = 0
                list = getTrainingPlansWithPagination(
                    start,
                    SELECT_TRAINING_PLAN_LIST_SIZE,
                    baseList
                )
                start = SELECT_TRAINING_PLAN_LIST_SIZE
                val newAdapter = EditTrainingPlanPopupItemAdapter(list, false, training.ID)

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
        val intent = Intent(this, MainActivity::class.java)
        finish()
        startActivity(intent)
    }
}