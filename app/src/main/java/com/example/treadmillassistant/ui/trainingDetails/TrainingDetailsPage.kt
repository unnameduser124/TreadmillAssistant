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
import com.example.treadmillassistant.backend.localDatabase.TrainingService
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.databinding.IndividualTrainingPageBinding
import com.example.treadmillassistant.ui.editTraining.EditTraining
import java.text.SimpleDateFormat
import java.util.*


class TrainingDetailsPage: AppCompatActivity() {

    private var fromCalendarPage = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = IndividualTrainingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemID = intent.getLongExtra("id", -1L)
        fromCalendarPage = intent.getBooleanExtra("fromCalendarPage", true)

        val training = user.trainingSchedule.getTraining(itemID)

        if(training==null){
            val intent = Intent(this, MainActivity::class.java)
            finishAffinity()
            startActivity(intent)
        }
        else{

            if(training.trainingStatus == TrainingStatus.Finished){
                binding.trainingDetailsEditButton.isGone = true
            }
            val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.ROOT)

            //display all training data in text views
            if(training.trainingStatus==TrainingStatus.Finished){
                binding.trainingDetailsStatusText.text = getString(R.string.finished_status)
            }
            else if(training.trainingStatus==TrainingStatus.Upcoming){
                binding.trainingDetailsStatusText.text = getString(R.string.upcoming_status)
            }
            else if(training.trainingStatus==TrainingStatus.Abandoned){
                binding.trainingDetailsStatusText.text = getString(R.string.abandoned_status)
            }
            else if(training.trainingStatus==TrainingStatus.Paused){
                binding.trainingDetailsStatusText.text = getString(R.string.paused_status)
            }
            else if(training.trainingStatus==TrainingStatus.InProgress){
                binding.trainingDetailsStatusText.text = getString(R.string.in_progress_status)
            }
            binding.trainingDetailsDateText.text = simpleDateFormat.format(training.trainingTime.time)
            binding.trainingDetailsDurationText.text = String.format(getString(R.string.duration_minutes),
                round(secondsToMinutes(training.getTotalDuration()), DURATION_ROUND_MULTIPLIER))
            binding.trainingDetailsDistanceText.text = String.format(getString(R.string.distance), round(training.getTotalDistance(), DISTANCE_ROUND_MULTIPLIER))
            binding.trainingDetailsCaloriesText.text = String.format(getString(R.string.calories), training.calculateCalories())
            binding.trainingDetailsAvgSpeedText.text = String.format(getString(R.string.speed), round(training.getAverageSpeed(), SPEED_ROUND_MULTIPLIER))
            binding.trainingDetailsAvgPaceText.text = String.format(getString(R.string.pace),
                round((SECONDS_IN_MINUTE.toDouble()/training.getAverageSpeed()), PACE_ROUND_MULTIPLIER))
            binding.trainingDetailsAvgTiltText.text = round(training.getAverageTilt(), TILT_ROUND_MULTIPLIER).toString()
            binding.trainingDetailsMediaLinkText.text = training.mediaLink
            binding.trainingDetailsMediaLinkText.isSelected = true

            binding.trainingDetailsMediaLinkText.setOnClickListener {
                val sendIntent = Intent()
                sendIntent.action = "android.intent.action.SEND"
                sendIntent.putExtra("android.intent.extra.TEXT", "${binding.trainingDetailsMediaLinkText.text}")
                sendIntent.type = "text/plain"
                startActivity(Intent.createChooser(sendIntent, "media link"))
            }

            //set up training phase list recycler view
            val itemAdapter = TrainingDetailsPhaseItemAdapter(training.trainingPlan.trainingPhaseList)
            val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
            binding.trainingDetailsPhaseList.layoutManager = linearLayoutManager
            binding.trainingDetailsPhaseList.adapter = itemAdapter
            binding.trainingDetailsPhaseList.setHasFixedSize(true)

            binding.trainingDetailsRemoveButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                finish()
                startActivity(intent)
                TrainingService(this).deleteTraining(training)
                user.trainingSchedule.removeTraining(training)
            }

            binding.trainingDetailsEditButton.setOnClickListener {
                val intent = Intent(this, EditTraining::class.java)
                intent.putExtra("id", training.ID)
                finish()
                startActivity(intent)
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }
}