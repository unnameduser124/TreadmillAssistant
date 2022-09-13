package com.example.treadmillassistant.ui.editTrainingPlan

import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPhase
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPlan
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingPhaseService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingPlanService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.StatusCode
import com.example.treadmillassistant.backend.training.TrainingPhase
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.databinding.AddTrainingPlanLayoutBinding
import com.example.treadmillassistant.ui.addTraining.AddTraining
import com.example.treadmillassistant.ui.editTraining.EditTraining
import java.util.*
import kotlin.concurrent.thread

class EditTrainingPlan: AppCompatActivity() {

    private var fromTraining: Boolean = false
    var date: Calendar = Calendar.getInstance()
    private lateinit var binding: AddTrainingPlanLayoutBinding
    private var trainingID = -1L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddTrainingPlanLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val trainingPlanID = intent.getLongExtra("ID", -1)
        trainingID = intent.getLongExtra("trainingID", -1L)
        fromTraining = intent.getBooleanExtra("fromTraining", false)
        if(fromTraining){
            date.timeInMillis = intent.getLongExtra("date", Calendar.getInstance().timeInMillis)
        }
        var trainingPlan: TrainingPlan? = null
        val loadedTrainingPlan: MutableLiveData<Boolean> by lazy{
            MutableLiveData<Boolean>()
        }
        thread {
            val plan = ServerTrainingPlanService().getTrainingPlan(trainingPlanID)
            if(plan.first == StatusCode.OK){
                trainingPlan = plan.second
                loadedTrainingPlan.postValue(true)
            }
            else{
                exitActivity()
            }
        }

        val loadedTrainingPlanObserver = androidx.lifecycle.Observer<Boolean>{
            if(trainingPlan==null){
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                return@Observer
            }
            binding.planNameInput.setText(trainingPlan!!.name)
            //phase list recycler view setup
            val phaseList = trainingPlan!!.copyPhaseList()
            updateTotalValues(phaseList)
            val phaseListItemAdapter = EditTrainingPlanPhaseItemAdapter(phaseList, binding.totalDurationTrainingPlanLabel, binding.totalDistanceTrainingPlanLabel)
            val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.addTrainingPlanPhaseList.layoutManager = linearLayoutManager
            binding.addTrainingPlanPhaseList.adapter = phaseListItemAdapter
            binding.addTrainingPlanPhaseList.setItemViewCacheSize(phaseList.size)


            binding.addNewPhase.setOnClickListener{
                //adding new phase
                if(phaseList.size>0){
                    phaseList.add(
                        TrainingPhase(
                            orderNumber = phaseList.last().orderNumber+1,
                            speed = DEFAULT_PHASE_SPEED,
                            duration = minutesToSeconds(DEFAULT_PHASE_DURATION),
                            trainingPlanID = trainingPlan!!.ID
                        )
                    )
                }
                else{
                    phaseList.add(
                        TrainingPhase(
                            orderNumber = 0,
                            speed = DEFAULT_PHASE_SPEED,
                            duration = minutesToSeconds(DEFAULT_PHASE_DURATION),
                            trainingPlanID = trainingPlan!!.ID
                        )
                    )
                }
                binding.addTrainingPlanPhaseList.adapter?.notifyItemInserted(phaseList.size-1)
                binding.addTrainingPlanPhaseList.setItemViewCacheSize(phaseList.size)

                updateTotalValues(phaseList)
            }

            binding.trainingPlanSaveButton.setOnClickListener {
                val name = binding.planNameInput.text.toString()
                comparePhaseLists(trainingPlan!!.trainingPhaseList, phaseList)
                if(name!="" && name!=" " && phaseList.size>0){
                    val newTrainingPlan = ServerTrainingPlan(name, trainingPlan!!.ID)
                    thread{
                        if(name!=trainingPlan!!.name){
                            ServerTrainingPlanService().updateTrainingPlan(newTrainingPlan, trainingPlan!!.ID)
                        }
                        val tpService = ServerTrainingPhaseService()
                        addedPhases.forEach {
                            tpService.createTrainingPhase(it)
                        }
                        updatedPhases.forEach{
                            tpService.updateTrainingPhase(it, it.ID)
                        }
                        removedPhaseIDs.forEach {
                            tpService.deleteTrainingPhase(it)
                        }
                    }
                    exitActivity()
                }
                else{
                    Toast.makeText(this, "Fill in the fields", Toast.LENGTH_SHORT).show()
                }
            }

            binding.cancelButton.setOnClickListener {
                exitActivity()
            }

            binding.trainingPlanRemoveButton.setOnClickListener {
                thread{
                    val removePlan = ServerTrainingPlanService().deleteTrainingPlan(trainingPlanID)
                    if(removePlan == StatusCode.OK){
                        Looper.prepare()
                        Toast.makeText(this, "Deleted successfully!", Toast.LENGTH_SHORT).show()
                        exitActivity()
                    }
                    else{
                        Looper.prepare()
                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        loadedTrainingPlan.observe(this, loadedTrainingPlanObserver)
    }

    private fun updateTotalValues(phaseList: MutableList<TrainingPhase>) {
        var duration = 0
        phaseList.forEach{
            duration += it.duration
        }
        binding.totalDurationTrainingPlanLabel.text = String.format(this.getString(R.string.total_duration_minutes), duration)

        var distance = 0.0
        phaseList.forEach {
            distance += (it.duration.toDouble()/3600.0)*it.speed
        }
        distance = round(distance, DISTANCE_ROUND_MULTIPLIER)
        binding.totalDistanceTrainingPlanLabel.text = String.format(this.getString(R.string.total_distance), distance)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        exitActivity()
    }

    companion object{
        val removedPhaseIDs = mutableListOf<Long>()
        val updatedPhases = mutableListOf<ServerTrainingPhase>()
        val addedPhases = mutableListOf<ServerTrainingPhase>()
    }

    private fun exitActivity(){
        if(fromTraining){
            val intent = Intent(this, AddTraining::class.java)
            intent.putExtra("date", date.timeInMillis)
            finish()
            startActivity(intent)
        }
        else{
            val intent = Intent(this, EditTraining::class.java)
            intent.putExtra("id", trainingID)
            finish()
            startActivity(intent)
        }
    }

    private fun comparePhaseLists(originalList: List<TrainingPhase>, modifiedList: List<TrainingPhase>){
        modifiedList.forEach {trainingPhase ->
            if(!originalList.contains(trainingPhase)){
                if(trainingPhase.ID == -1L){
                    addedPhases.add(ServerTrainingPhase(trainingPhase))
                }
                else if(originalList.any { it.ID == trainingPhase.ID }){
                    updatedPhases.add(ServerTrainingPhase(trainingPhase))
                }
            }
        }
    }
}