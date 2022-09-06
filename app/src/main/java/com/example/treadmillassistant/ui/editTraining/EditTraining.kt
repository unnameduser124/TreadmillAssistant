package com.example.treadmillassistant.ui.editTraining

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.localDatabase.TrainingService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingPlanService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.StatusCode
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.databinding.AddTrainingLayoutBinding
import com.example.treadmillassistant.databinding.TrainingPlanSelectionPopupBinding
import com.example.treadmillassistant.databinding.TreadmillSelectionPopupBinding
import com.example.treadmillassistant.ui.AddTreadmill
import com.example.treadmillassistant.ui.addTraining.AddTraining
import com.example.treadmillassistant.ui.addTraining.AddTrainingPlanPopupItemAdapter
import com.example.treadmillassistant.ui.addTraining.AddTreadmillPopupItemAdapter
import com.example.treadmillassistant.ui.addTrainingPlan.AddTrainingPlan
import com.example.treadmillassistant.ui.home.trainingTab.TrainingTabPlaceholderFragment
import com.example.treadmillassistant.ui.trainingDetails.TrainingDetailsPage
import java.util.*
import kotlin.concurrent.thread

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
        }
        else {
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



        binding.saveTrainingButton.setOnClickListener {
            if(selectedTreadmill.ID!=-1L){
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
                    selectedTrainingPlan
                )
                TrainingService(this).updateTraining(newTraining, training!!.ID)
                user.trainingSchedule.updateTraining(training, newTraining)
                user.trainingSchedule.sortCalendar()

                val intent = Intent(this, TrainingDetailsPage::class.java)
                intent.putExtra("id", training!!.ID)
                finish()
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Pick a treadmill!", Toast.LENGTH_SHORT).show()
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

            var start = 0
            var baseList = user.treadmillList
            var list = getTreadmillsWithPagination(start, SEARCH_POPUP_LIST_SIZE, baseList)
            val itemAdapter = AddTreadmillPopupItemAdapter(
                list,
                false,
                training!!.ID
            )
            start = SEARCH_POPUP_LIST_SIZE

            val linearLayoutManager = LinearLayoutManager(popupBinding.treadmillSearchList.context, RecyclerView.VERTICAL, false)
            popupBinding.treadmillSearchList.adapter = itemAdapter
            popupBinding.treadmillSearchList.layoutManager = linearLayoutManager
            popupBinding.treadmillSearchList.setHasFixedSize(false)

            popupBinding.treadmillSearchList.setOnScrollChangeListener { _, _, _, _, _ ->
                if(list.size>=SEARCH_POPUP_LIST_SIZE && list.size<baseList.size){
                    if((popupBinding.treadmillSearchList.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == list.size - 1){
                        val addList = getTreadmillsWithPagination(
                            start,
                            SELECT_TRAINING_PLAN_LIST_LOAD_LIMIT,
                            baseList
                        )
                        if(addList.size>0){
                            list += addList
                            start += SELECT_TRAINING_PLAN_LIST_LOAD_LIMIT
                            popupBinding.treadmillSearchList.post {
                                popupBinding.treadmillSearchList.adapter?.notifyItemRangeInserted(
                                    list.size - addList.size,
                                    addList.size
                                )
                            }
                        }
                    }
                }
            }

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
                baseList = user.treadmillList.filter{
                    it.name.lowercase().contains(typedText.toString().lowercase())
                }.toMutableList()
                start = 0
                list = getTreadmillsWithPagination(
                    start,
                    SEARCH_POPUP_LIST_SIZE,
                    baseList
                )
                start = SEARCH_POPUP_LIST_SIZE
                val newAdapter = AddTreadmillPopupItemAdapter(list, false, training!!.ID)

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
            var baseList = mutableListOf<TrainingPlan>()
            var list = mutableListOf<TrainingPlan>()
            val loaded: MutableLiveData<Boolean> by lazy{
                MutableLiveData<Boolean>(false)
            }

            thread{
                baseList.clear()
                loop@do{
                    val planPair = ServerTrainingPlanService().getAllTrainingPlans(start, 5)
                    if(planPair.first == StatusCode.OK){
                        planPair.second.forEach {
                            val phaseListPair = ServerTrainingPlanService().getTrainingPlan(it.ID)

                            if(phaseListPair.second.trainingPhaseList.size>0 && phaseListPair.first == StatusCode.OK){
                                baseList.add(phaseListPair.second)
                            }
                        }
                        start+= SELECT_TRAINING_PLAN_LIST_LOAD_LIMIT
                    }
                    else{
                        break@loop
                    }
                }while(baseList.size<13)
                loaded.postValue(true)
            }

            val observer = androidx.lifecycle.Observer<Boolean>{
                try{
                    list = baseList
                    val itemAdapter = EditTrainingPlanPopupItemAdapter(
                        list,
                        true
                    )
                    val linearLayoutManager = WrapContentLinearLayoutManager(popupBinding.trainingPlanSearchList.context, RecyclerView.VERTICAL, false)
                    popupBinding.trainingPlanSearchList.adapter = itemAdapter
                    popupBinding.trainingPlanSearchList.layoutManager = linearLayoutManager
                    popupBinding.trainingPlanSearchList.setHasFixedSize(false)
                }
                catch(exception: IndexOutOfBoundsException){
                    println("${exception.message} ${exception.stackTrace}")
                    loaded.postValue(true)
                }
            }
            loaded.observe(this, observer)




            popupBinding.trainingPlanSearchList.setOnScrollChangeListener { _, _, _, _, _ ->
                if(list.size>= SEARCH_POPUP_LIST_SIZE){
                    if((popupBinding.trainingPlanSearchList.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == list.size - 1){
                        thread{
                            val sizeBefore = baseList.size
                            val planPair = ServerTrainingPlanService().getAllTrainingPlans(start, SELECT_TRAINING_PLAN_LIST_LOAD_LIMIT)
                            if(planPair.first == StatusCode.OK){
                                planPair.second.forEach {
                                    val phaseListPair = ServerTrainingPlanService().getTrainingPlan(it.ID)

                                    if(phaseListPair.second.trainingPhaseList.size>0 && phaseListPair.first == StatusCode.OK){
                                        baseList.add(phaseListPair.second)
                                    }
                                }
                            }
                            if(baseList.size>sizeBefore){
                                start += SELECT_TRAINING_PLAN_LIST_LOAD_LIMIT

                                popupBinding.trainingPlanSearchList.post {
                                    popupBinding.trainingPlanSearchList.adapter?.notifyItemRangeInserted(
                                        baseList.size - (baseList.size - sizeBefore),
                                        (baseList.size - sizeBefore)
                                    )
                                }
                                popupBinding.trainingPlanSearchList.post{
                                    popupBinding.trainingPlanSearchList.scrollBy(0,70)
                                }
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
                intent.putExtra("fromEditTraining", true)
                startActivity(intent)
                popupWindow.dismiss()
            }

            popupBinding.trainingPlanSearchInput.addTextChangedListener {typedText: Editable? ->
                list = baseList.filter{
                    it.name.lowercase().contains(typedText.toString().lowercase())
                }.toMutableList()
                val newAdapter = EditTrainingPlanPopupItemAdapter(list, false, TrainingTabPlaceholderFragment.training.ID)

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