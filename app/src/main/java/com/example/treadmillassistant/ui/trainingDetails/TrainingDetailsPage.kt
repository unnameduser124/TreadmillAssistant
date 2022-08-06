package com.example.treadmillassistant.ui.trainingDetails

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.databinding.IndividualWorkoutPageBinding
import com.example.treadmillassistant.ui.EditTraining
import java.text.SimpleDateFormat
import java.util.*

class TrainingDetailsPage: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = IndividualWorkoutPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemID = intent.getLongExtra("id", -1L)
        val fromCalendarPage = intent.getBooleanExtra("fromCalendarPage", false)

        if(itemID == -1L){
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }

        val item = user.trainingSchedule.getTraining(itemID)

        if(item==null){
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }
        else{

            if(item.trainingStatus == TrainingStatus.Finished){
                binding.trainingDetailsEditButton.isGone = true
            }
            val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy", Locale.ROOT)

            //display all training data in text views
            binding.trainingDetailsStatusText.text = "${item.trainingStatus}"
            binding.trainingDetailsDateText.text = simpleDateFormat.format(item.trainingTime.time)
            binding.trainingDetailsDurationText.text = String.format(getString(R.string.duration_minutes),
                round(secondsToMinutes(item.getTotalDuration()), DURATION_ROUND_MULTIPLIER))
            binding.trainingDetailsDistanceText.text = String.format(getString(R.string.distance), round(item.getTotalDistance(), DISTANCE_ROUND_MULTIPLIER))
            binding.trainingDetailsCaloriesText.text = String.format(getString(R.string.calories), item.calculateCalories())
            binding.trainingDetailsAvgSpeedText.text = String.format(getString(R.string.speed), round(item.getAverageSpeed(), SPEED_ROUND_MULTIPLIER))
            binding.trainingDetailsAvgPaceText.text = String.format(getString(R.string.pace),
                round((SECONDS_IN_MINUTE.toDouble()/item.getAverageSpeed()), PACE_ROUND_MULTIPLIER))
            binding.trainingDetailsAvgTiltText.text = round(item.getAverageTilt(), TILT_ROUND_MULTIPLIER).toString()

            //set up training phase list recycler view
            val itemAdapter = TrainingDetailsPhaseItemAdapter(item.trainingPlan.trainingPhaseList)
            val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.trainingDetailsPhaseList.layoutManager = linearLayoutManager
            binding.trainingDetailsPhaseList.adapter = itemAdapter
            binding.trainingDetailsPhaseList.setHasFixedSize(true)

            binding.trainingDetailsRemoveButton.setOnClickListener {
                if(!fromCalendarPage){
                    val intent = Intent(this, MainActivity::class.java)
                    user.trainingSchedule.removeTraining(item)
                    intent.putExtra("navViewPosition", TRAINING_HISTORY_NAV_VIEW_POSITION)
                    finish()
                    startActivity(intent)
                }
                else{
                    val intent = Intent(this, MainActivity::class.java)
                    user.trainingSchedule.removeTraining(item)
                    intent.putExtra("navViewPosition", HOME_TAB_NAV_VIEW_POSITION)
                    finish()
                    startActivity(intent)
                }
            }

            binding.trainingDetailsEditButton.setOnClickListener {
                val intent = Intent(this, EditTraining::class.java)
                intent.putExtra("id", item.ID)
                finish()
                startActivity(intent)
            }
        }
    }
}