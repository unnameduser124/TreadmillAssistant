package com.example.treadmillassistant.ui.trainingDetails

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.databinding.IndividualWorkoutPageBinding
import java.text.SimpleDateFormat

class TrainingDetailsPage: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = IndividualWorkoutPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trainingDetailEditButton.isGone = true
        val itemID = intent.getIntExtra("id", -1)

        if(itemID == -1){
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }

        val item = user.workoutSchedule.workoutList.firstOrNull { it.ID == itemID }

        if(item==null){
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }
        else{
            var date = item.workoutTime
            val simpleDateFormat = SimpleDateFormat("dd.MM.${date.year}")
            binding.trainingDetailsDateText.text = simpleDateFormat.format(date)
            binding.trainingDetailsDurationText.text = "${round(secondsToMinutes(item.getTotalDuration()), DURATION_ROUND_MULTIPLIER)} min"
            binding.trainingDetailsDistanceText.text = "${round(item.getTotalDistance(), DISTANCE_ROUND_MULTIPLIER)} km"
            binding.trainingDetailsCaloriesText.text = "${calculateCaloriesForWorkout(item)} kcal"
            binding.trainingDetailsAvgSpeedText.text = "${round(item.getAverageSpeed(), SPEED_ROUND_MULTIPLIER)} km/h"
            binding.trainingDetailsAvgPaceText.text = "${round((SECONDS_IN_MINUTE.toDouble()/item.getAverageSpeed()), PACE_ROUND_MULTIPLIER)}'"
            binding.trainingDetailsAvgTiltText.text = "${round(item.getAverageTilt(), TILT_ROUND_MULTIPLIER)}"

            val itemAdapter = TrainingDetailsPhaseItemAdapter(item.workoutPlan.workoutPhaseList)
            val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.trainingDetailsPhaseList.layoutManager = linearLayoutManager
            binding.trainingDetailsPhaseList.adapter = itemAdapter
            binding.trainingDetailsPhaseList.setHasFixedSize(true)

            binding.trainingDetailsRemoveButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                user.workoutSchedule.removeWorkout(item)
                intent.putExtra("navViewPosition", 1)
                finish()
                startActivity(intent)
            }
        }
    }
}