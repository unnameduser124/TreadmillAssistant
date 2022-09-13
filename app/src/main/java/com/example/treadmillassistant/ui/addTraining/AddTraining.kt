package com.example.treadmillassistant.ui.addTraining


import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.text.Editable
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTraining
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingPlanService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.StatusCode
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.TrainingPhase
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.databinding.AddTrainingLayoutBinding
import com.example.treadmillassistant.databinding.TrainingPlanSelectionPopupBinding
import com.example.treadmillassistant.databinding.TreadmillSelectionPopupBinding
import com.example.treadmillassistant.ui.AddTreadmill
import com.example.treadmillassistant.ui.addTrainingPlan.AddTrainingPlan
import com.example.treadmillassistant.ui.editTraining.WrapContentLinearLayoutManager
import com.example.treadmillassistant.ui.home.trainingTab.TrainingTabPlaceholderFragment.Companion.training
import java.util.*
import kotlin.concurrent.thread

class AddTraining: AppCompatActivity(){
    lateinit var binding: AddTrainingLayoutBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         binding = AddTrainingLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trainingTime.setIs24HourView(true)

        val chosenDate = Calendar.getInstance()
        chosenDate.timeInMillis = intent.getLongExtra("date", Calendar.getInstance().timeInMillis)
        setUpDatePicker(binding.trainingDate, chosenDate)
        setUpTimePicker(binding.trainingTime, chosenDate)
        val today = Calendar.getInstance()

        binding.trainingDate.minDate = today.timeInMillis

        loadDefaultTrainingPlanName()

        binding.selectedTreadmillName.text = if(user.treadmillList.isEmpty()){
            selectedTreadmill = Treadmill()
            getString(R.string.no_treadmill)
        }
        else{
            selectedTreadmill= user.treadmillList.first()
            user.treadmillList.first().name

        }

        binding.saveTrainingButton.setOnClickListener{
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

            thread{
                val responseCode = ServerTrainingService().createTraining(ServerTraining(newTraining))
                if(responseCode.first == StatusCode.Created){
                    val intent = Intent(this, MainActivity::class.java)
                    finishAffinity()
                    startActivity(intent)
                }
                else{
                    Looper.prepare()
                    Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                }
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

            popupBinding.treadmillSelectionCancelButton.setOnClickListener { treadmillPopup.dismiss() }

            treadmillPopup.setOnDismissListener {
                if(selectedTreadmill.ID!=-1L){
                    binding.selectedTreadmillName.text = selectedTreadmill.name
                }
            }

            var start = 0
            var baseList = user.treadmillList
            var list = getTreadmillsWithPagination(start, SEARCH_POPUP_LIST_SIZE, baseList)
            val itemAdapter = AddTreadmillPopupItemAdapter(
                list,
                true
            )
            start = SEARCH_POPUP_LIST_SIZE
            val linearLayoutManager = LinearLayoutManager(popupBinding.treadmillSearchList.context, RecyclerView.VERTICAL, false)
            popupBinding.treadmillSearchList.adapter = itemAdapter
            popupBinding.treadmillSearchList.layoutManager = linearLayoutManager
            popupBinding.treadmillSearchList.setHasFixedSize(false)

            popupBinding.treadmillSearchList.setOnScrollChangeListener { _, _, _, _, _ ->
                if(list.size>= SEARCH_POPUP_LIST_SIZE && list.size<baseList.size){
                    if((popupBinding.treadmillSearchList.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition() == list.size - 1){
                        val addList = getTreadmillsWithPagination(
                            start,
                            SELECT_TRAINING_PLAN_LIST_LOAD_LIMIT,
                            baseList
                        ).toMutableList()

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
                val newAdapter = AddTreadmillPopupItemAdapter(list, false, training.ID)

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
            val baseList = mutableListOf<TrainingPlan>()
            var list = mutableListOf<TrainingPlan>()
            val loaded: MutableLiveData<Boolean> by lazy{
                MutableLiveData<Boolean>(false)
            }

            thread{
                baseList.clear()
                loop@do{
                    if(loaded.value==false){
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
                    }
                }while(baseList.size<13)
                loaded.postValue(true)
            }

            val observer = androidx.lifecycle.Observer<Boolean>{
                if(it){
                    list = baseList
                    val itemAdapter = AddTrainingPlanPopupItemAdapter(
                        list,
                        true
                    )
                    val linearLayoutManager = WrapContentLinearLayoutManager(popupBinding.trainingPlanSearchList.context, RecyclerView.VERTICAL, false)
                    popupBinding.trainingPlanSearchList.adapter = itemAdapter
                    popupBinding.trainingPlanSearchList.layoutManager = linearLayoutManager
                    popupBinding.trainingPlanSearchList.setHasFixedSize(false)
                    loaded.value = false
                }
            }
            loaded.observe(this, observer)




            popupBinding.trainingPlanSearchList.setOnScrollChangeListener { _, _, _, _, _ ->
                if(list.size >= SEARCH_POPUP_LIST_SIZE){
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
                list = baseList.filter{
                    it.name.lowercase().contains(typedText.toString().lowercase())
                }.toMutableList()
                val newAdapter = AddTrainingPlanPopupItemAdapter(list, false, training.ID)

                popupBinding.trainingPlanSearchList.adapter = newAdapter
            }
        }
    }

    private fun loadDefaultTrainingPlanName() {
        thread{
            val firstPlan = ServerTrainingPlanService().getAllTrainingPlans(0, 10)
            if(firstPlan.first == StatusCode.OK){
                if(firstPlan.second.size>0){
                    run breaking@{
                        firstPlan.second.forEach{
                            val plan = ServerTrainingPlanService().getTrainingPlan(it.ID)
                            if(plan.first == StatusCode.OK){
                                if(plan.second.trainingPhaseList != emptyList<TrainingPhase>()){
                                    binding.selectedTrainingPlanName.post{
                                        binding.selectedTrainingPlanName.text = plan.second.name
                                    }
                                    selectedTrainingPlan = plan.second
                                    return@breaking
                                }
                            }
                        }
                    }
                }
                else{
                    binding.selectedTrainingPlanName.post{
                        binding.selectedTrainingPlanName.text = getString(R.string.no_training_plan)
                    }
                }
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