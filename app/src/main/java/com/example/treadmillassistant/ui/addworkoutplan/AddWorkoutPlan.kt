package com.example.treadmillassistant.ui.addworkoutplan

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.workout.WorkoutPhase
import com.example.treadmillassistant.backend.workout.WorkoutPlan
import com.example.treadmillassistant.databinding.AddWorkoutPlanLayoutBinding

class AddWorkoutPlan: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = AddWorkoutPlanLayoutBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val phaseList = mutableListOf<WorkoutPhase>()

        val phaseListItemAdater = WorkoutPhaseItemAdapter(phaseList, binding.totalDurationWorkoutPlanLabel, binding.totalDistanceWorkoutPlanLabel)
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.addWorkoutPlanPhaseList.layoutManager = linearLayoutManager
        binding.addWorkoutPlanPhaseList.adapter = phaseListItemAdater
        binding.addWorkoutPlanPhaseList.setItemViewCacheSize(phaseList.size)


        binding.addNewPhase.setOnClickListener{
            if(phaseList.size>0){
                phaseList.add(WorkoutPhase(orderNumber = phaseList.last().orderNumber+1, speed = DEFAULT_PHASE_SPEED,
                    duration = minutesToSeconds(DEFAULT_PHASE_DURATION)))
            }
            else{
                phaseList.add(WorkoutPhase(orderNumber = 0, speed = DEFAULT_PHASE_SPEED, duration = minutesToSeconds(DEFAULT_PHASE_DURATION)))
            }
            binding.addWorkoutPlanPhaseList.adapter?.notifyItemInserted(phaseList.size-1)
            binding.addWorkoutPlanPhaseList.setItemViewCacheSize(phaseList.size)

            var duration = 0
            phaseList.forEach{
                duration += it.duration
            }
            binding.totalDurationWorkoutPlanLabel.text = "Total duration: ${round(secondsToMinutes(duration), DURATION_ROUND_MULTIPLIER)} min"
            var distance = 0.0
            phaseList.forEach {
                distance += (it.duration.toDouble()/3600.0)*it.speed
            }
            binding.totalDistanceWorkoutPlanLabel.text = "Total distance: ${round(distance, DISTANCE_ROUND_MULTIPLIER)} km"
        }

        binding.workoutPlanSaveButton.setOnClickListener {
            val name = binding.planNameInput.text.toString()
            if(name!="" && name!=" " && phaseList.size>0){
                user.workoutPlanList.addWorkoutPlan(WorkoutPlan(name, phaseList, user.ID))
                val intent = Intent(this, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
            else{
                Toast.makeText(this, "Fill in the fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.cancelButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }
    }
}