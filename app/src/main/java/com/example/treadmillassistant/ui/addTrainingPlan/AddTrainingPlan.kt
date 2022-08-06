package com.example.treadmillassistant.ui.addTrainingPlan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.AddTraining
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.training.TrainingPhase
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.databinding.AddWorkoutPlanLayoutBinding
import java.util.*

class AddTrainingPlan: AppCompatActivity() {

    private var fromTraining: Boolean = false
    var date: Date = Date()
    private lateinit var binding: AddWorkoutPlanLayoutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = AddWorkoutPlanLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        fromTraining = intent.getBooleanExtra("fromTraining", false)
        if(fromTraining){
            date.time = intent.getLongExtra("date", Calendar.getInstance().timeInMillis)
        }

        //phase list recycler view setup
        val phaseList = mutableListOf<TrainingPhase>()
        val phaseListItemAdapter = TrainingPhaseItemAdapter(phaseList, binding.totalDurationTrainingPlanLabel, binding.totalDistanceTrainingPlanLabel)
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.addTrainingPlanPhaseList.layoutManager = linearLayoutManager
        binding.addTrainingPlanPhaseList.adapter = phaseListItemAdapter
        binding.addTrainingPlanPhaseList.setItemViewCacheSize(phaseList.size)


        binding.addNewPhase.setOnClickListener{
            //adding new phase with locally valid ID
            if(phaseList.size>0){
                phaseList.add(TrainingPhase(
                    orderNumber = phaseList.last().orderNumber+1, speed = DEFAULT_PHASE_SPEED,
                    duration = minutesToSeconds(DEFAULT_PHASE_DURATION)))
            }
            else{
                phaseList.add(TrainingPhase(orderNumber = 0, speed = DEFAULT_PHASE_SPEED, duration = minutesToSeconds(DEFAULT_PHASE_DURATION)))
            }
            binding.addTrainingPlanPhaseList.adapter?.notifyItemInserted(phaseList.size-1)
            binding.addTrainingPlanPhaseList.setItemViewCacheSize(phaseList.size)

            updateTotalValues(phaseList)
        }

        binding.trainingPlanSaveButton.setOnClickListener {
            val name = binding.planNameInput.text.toString()
            if(name!="" && name!=" " && phaseList.size>0){
                val intent: Intent
                user.trainingPlanList.addTrainingPlan(TrainingPlan(name, phaseList, user.ID))
                if(fromTraining){
                    intent = Intent(this, AddTraining::class.java)
                    intent.putExtra("date", date.time)
                    finish()
                    startActivity(intent)
                }
                else{
                    intent = Intent(this, MainActivity::class.java)
                    finishAffinity()
                    startActivity(intent)
                }
            }
            else{
                Toast.makeText(this, "Fill in the fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            if(fromTraining){
                intent = Intent(this, AddTraining::class.java)
                intent.putExtra("date", date.time)
                finish()
                startActivity(intent)
            }
            else{
                intent = Intent(this, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        }
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
        if(fromTraining){
            intent = Intent(this, AddTraining::class.java)
            intent.putExtra("date", date.time)
            finish()
            startActivity(intent)
        }
        else{
            intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }
    }
}