package com.example.treadmillassistant.ui.trainingDetails

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.localDatabase.TrainingService
import com.example.treadmillassistant.backend.workout.WorkoutStatus
import com.example.treadmillassistant.databinding.IndividualWorkoutPageBinding
import com.example.treadmillassistant.ui.EditWorkout
import java.text.SimpleDateFormat
import java.util.*

class TrainingDetailsPage: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = IndividualWorkoutPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.trainingDetailsEditButton.isGone = true
        val itemID = intent.getLongExtra("id", -1)
        val fromCalendarPage = intent.getBooleanExtra("fromCalendarPage", false)

        if(itemID == -1L){
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }

        val item = user.workoutSchedule.getWorkout(itemID)

        if(item==null){
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }
        else{
            if(item.workoutStatus != WorkoutStatus.Finished){
                binding.trainingDetailsEditButton.isGone = false
            }
            var date = item.workoutTime
            val simpleDateFormat = SimpleDateFormat("dd.MM.${date.get(Calendar.YEAR)}")

            binding.trainingDetailsStatusText.text = "${item.workoutStatus}"

            binding.trainingDetailsDateText.text = simpleDateFormat.format(date.time)
            binding.trainingDetailsDurationText.text = "${round(secondsToMinutes(item.getTotalDuration()), DURATION_ROUND_MULTIPLIER)} min"
            binding.trainingDetailsDistanceText.text = "${round(item.getTotalDistance(), DISTANCE_ROUND_MULTIPLIER)} km"
            binding.trainingDetailsCaloriesText.text = "${item.calculateCalories()} kcal"
            binding.trainingDetailsAvgSpeedText.text = "${round(item.getAverageSpeed(), SPEED_ROUND_MULTIPLIER)} km/h"
            binding.trainingDetailsAvgPaceText.text = "${round((SECONDS_IN_MINUTE.toDouble()/item.getAverageSpeed()), PACE_ROUND_MULTIPLIER)}'"
            binding.trainingDetailsAvgTiltText.text = "${round(item.getAverageTilt(), TILT_ROUND_MULTIPLIER)}"

            val itemAdapter = TrainingDetailsPhaseItemAdapter(item.workoutPlan.workoutPhaseList)
            val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.trainingDetailsPhaseList.layoutManager = linearLayoutManager
            binding.trainingDetailsPhaseList.adapter = itemAdapter
            binding.trainingDetailsPhaseList.setHasFixedSize(true)

            binding.trainingDetailsRemoveButton.setOnClickListener {
                if(!fromCalendarPage){
                    val intent = Intent(this, MainActivity::class.java)
                    user.workoutSchedule.removeWorkout(item)
                    intent.putExtra("navViewPosition", TRAINING_HISTORY_NAV_VIEW_POSITION)
                    finish()
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this, MainActivity::class.java)
                    user.workoutSchedule.removeWorkout(item)
                    intent.putExtra("navViewPosition", HOME_TAB_NAV_VIEW_POSITION)
                    finish()
                    startActivity(intent)
                }
            }

            binding.trainingDetailsEditButton.setOnClickListener {
                val intent = Intent(this, EditWorkout::class.java)
                intent.putExtra("id", item.ID)
                finish()
                startActivity(intent)
            }
        }
    }
}