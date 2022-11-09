package com.example.treadmillassistant.ui.trainingDetails

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Looper
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.treadmillassistant.MainActivity
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.localDatabase.NotificationService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingPlanService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.StatusCode
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.TrainingStatus
import com.example.treadmillassistant.databinding.IndividualTrainingPageBinding
import com.example.treadmillassistant.ui.ScheduledTrainingNotification
import com.example.treadmillassistant.ui.editTraining.EditTraining
import com.example.treadmillassistant.ui.notificationID
import com.example.treadmillassistant.ui.trainingNotificationTime
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


class TrainingDetailsPage: AppCompatActivity() {

    private var fromCalendarPage = false
    lateinit var binding: IndividualTrainingPageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = IndividualTrainingPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val itemID = intent.getLongExtra("id", -1L)
        fromCalendarPage = intent.getBooleanExtra("fromCalendarPage", true)

        val loadTraining: MutableLiveData<Boolean> by lazy{
            MutableLiveData<Boolean>(false)
        }
        var training: PlannedTraining? = null
        thread{
            val fromServer = ServerTrainingService().getTrainingByID(itemID)
            if(fromServer.first == StatusCode.OK){
                training = fromServer.second as PlannedTraining
                loadTraining.postValue(true)
            }
            else{
                val intent = Intent(this, MainActivity::class.java)
                finishAffinity()
                startActivity(intent)
            }
        }
        val trainingLoadedObserver = androidx.lifecycle.Observer<Boolean>{
            if(it){
                if(training==null){
                    val intent = Intent(this, MainActivity::class.java)
                    finishAffinity()
                    startActivity(intent)
                }
                else{

                    setUpTrainingData(training!!)

                    loadTrainingPlan(training!!)

                    binding.trainingDetailsMediaLinkText.setOnClickListener {
                        val sendIntent = Intent()
                        sendIntent.action = "android.intent.action.SEND"
                        sendIntent.putExtra("android.intent.extra.TEXT", "${binding.trainingDetailsMediaLinkText.text}")
                        sendIntent.type = "text/plain"
                        startActivity(Intent.createChooser(sendIntent, "media link"))
                    }

                    binding.trainingDetailsRemoveButton.setOnClickListener {
                        thread{
                            val code = ServerTrainingService().deleteTraining(training!!.ID)
                            if(code == StatusCode.OK){
                                cancelNotification(training!!.ID)
                                user.trainingSchedule.removeTraining(training!!)
                                Looper.prepare()
                                Toast.makeText(this, "Deleted successfully!", Toast.LENGTH_SHORT).show()
                                val intent = Intent(this, MainActivity::class.java)
                                finish()
                                startActivity(intent)
                            }
                            else{
                                println(code)
                                Looper.prepare()
                                Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }

                    binding.trainingDetailsEditButton.setOnClickListener {
                        val intent = Intent(this, EditTraining::class.java)
                        intent.putExtra("id", itemID)
                        finish()
                        startActivity(intent)
                    }
                }
            }
        }

        loadTraining.observe(this, trainingLoadedObserver)
    }

    private fun loadTrainingPlan(training: PlannedTraining) {
        val loadedPlan: MutableLiveData<Boolean> by lazy {
            MutableLiveData<Boolean>(false)
        }
        thread{
            val plan = ServerTrainingPlanService().getTrainingPlan(training.trainingPlan.ID)
            if(plan.first == StatusCode.OK){
                training.trainingPlan = plan.second
                loadedPlan.postValue(true)
            }
        }

        val planObserver = androidx.lifecycle.Observer<Boolean>{ loadedPlanBoolean ->
            if(loadedPlanBoolean){
                setUpTrainingPlanData(training)
            }
            else{
                setUpBlankPlanData()
            }
        }
        loadedPlan.observe(this, planObserver)
    }

    private fun setUpBlankPlanData() {
        binding.trainingDetailsDurationText.text = String.format(getString(R.string.duration_minutes),
            0)
        binding.trainingDetailsDistanceText.text = String.format(getString(R.string.distance), 0.0)
        binding.trainingDetailsCaloriesText.text = String.format(getString(R.string.calories), 0)
        binding.trainingDetailsAvgSpeedText.text = String.format(getString(R.string.speed), 0.0)
        binding.trainingDetailsAvgPaceText.text = String.format(getString(R.string.pace),
            0.0)
        binding.trainingDetailsAvgTiltText.text = round(0.0, TILT_ROUND_MULTIPLIER).toString()
    }

    private fun setUpTrainingPlanData(training: PlannedTraining) {
        val itemAdapter = TrainingDetailsPhaseItemAdapter(training.trainingPlan.trainingPhaseList)
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        binding.trainingDetailsPhaseList.layoutManager = linearLayoutManager
        binding.trainingDetailsPhaseList.adapter = itemAdapter
        binding.trainingDetailsPhaseList.setHasFixedSize(true)
        binding.trainingDetailsDurationText.text = String.format(getString(R.string.duration_minutes),
            round(secondsToMinutes(training.getTotalDuration()), DURATION_ROUND_MULTIPLIER))
        binding.trainingDetailsDistanceText.text = String.format(getString(R.string.distance), round(
            training.getTotalDistance(), DISTANCE_ROUND_MULTIPLIER))
        binding.trainingDetailsCaloriesText.text = String.format(getString(R.string.calories), training.calculateCalories())
        binding.trainingDetailsAvgSpeedText.text = String.format(getString(R.string.speed), round(
            training.getAverageSpeed(), SPEED_ROUND_MULTIPLIER))
        binding.trainingDetailsAvgPaceText.text = String.format(getString(R.string.pace),
            round((SECONDS_IN_MINUTE.toDouble()/ training.getAverageSpeed()), PACE_ROUND_MULTIPLIER))
        binding.trainingDetailsAvgTiltText.text = round(training.getAverageTilt(), TILT_ROUND_MULTIPLIER).toString()
    }

    private fun cancelNotification(trainingID: Long){
        val notificationPair = NotificationService(applicationContext).getNotificationByTrainingID(trainingID)
        val notificationIntent = Intent(applicationContext, ScheduledTrainingNotification::class.java)
        notificationIntent.putExtra(trainingNotificationTime, notificationPair.second)
        notificationIntent.putExtra(notificationID, notificationPair.first.toInt())

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            notificationPair.first.toInt(),
            notificationIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager.cancel(pendingIntent)
    }

    private fun setUpTrainingData(training: PlannedTraining) {
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


        binding.trainingDetailsMediaLinkText.text = training.mediaLink
        binding.trainingDetailsMediaLinkText.isSelected = true
        binding.trainingDetailsDateText.text = simpleDateFormat.format(training.trainingTime.time)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, MainActivity::class.java)
        finishAffinity()
        startActivity(intent)
    }
}