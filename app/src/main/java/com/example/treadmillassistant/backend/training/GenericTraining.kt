package com.example.treadmillassistant.backend.training

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.localDatabase.TrainingPhaseService
import com.example.treadmillassistant.backend.localDatabase.TrainingPlanService
import com.example.treadmillassistant.backend.localDatabase.TrainingService
import java.util.*

class GenericTraining(
    override var trainingTime: Calendar = Calendar.getInstance(),
    override var treadmill: Treadmill = Treadmill(),
    override var mediaLink: String = "",
    override var trainingStatus: TrainingStatus = TrainingStatus.InProgress,
    override var trainingPlan: TrainingPlan = TrainingPlan(),
    override var ID: Long = 0): Training {

    override var lastPhaseStart: Long = 0L
    override var phasePartialCompletion: Int = 0
    override var partialPhaseCompletionDistance: Double = 0.0
    override var lastPhase: TrainingPhase = TrainingPhase()

    override fun pauseTraining() {
        trainingStatus = TrainingStatus.Paused
        val lastPhaseTimeInSeconds = (millisecondsToSeconds(Calendar.getInstance().timeInMillis) - millisecondsToSeconds(lastPhaseStart)).toInt()
        val phase = TrainingPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, trainingPlan.trainingPhaseList.size, true)
        trainingPlan.addNewPhase(phase)
    }

    override fun finishTraining(context: Context) {
        super.finishTraining(context)
        user.trainingSchedule.addNewTraining(this)
        trainingPlan.trainingPhaseList.last().isFinished = true
        val newTrainingPlan = TrainingPlan("GenericTrainingPlan", trainingPlan.trainingPhaseList, userID = user.ID)
        newTrainingPlan.ID = TrainingPlanService(context).insertNewTrainingPlan(newTrainingPlan)
        val tpService = TrainingPhaseService(context)
        newTrainingPlan.trainingPhaseList.forEach {
            it.trainingPlanID = newTrainingPlan.ID
            it.ID = tpService.insertNewTrainingPhase(it)
        }
        this.trainingPlan = newTrainingPlan
        this.ID = TrainingService(context).insertNewTraining(this)

    }

    override fun getTotalDistance(): Double {
        var distance = 0.0

        if(trainingStatus != TrainingStatus.Finished || trainingStatus == TrainingStatus.Upcoming || trainingStatus == TrainingStatus.Abandoned){
            trainingPlan.trainingPhaseList.forEach {
                distance += (it.duration.toDouble()/ SECONDS_IN_HOUR.toDouble())*it.speed
            }
        }
        else{
            trainingPlan.trainingPhaseList.filter{ it.isFinished }.forEach {
                distance += (it.duration.toDouble()/ SECONDS_IN_HOUR.toDouble())*it.speed
            }
        }

        return distance
    }

    override fun getTotalDuration(): Int {
        var duration = 0
        if(trainingStatus != TrainingStatus.Finished || trainingStatus == TrainingStatus.Upcoming || trainingStatus == TrainingStatus.Abandoned){
            trainingPlan.trainingPhaseList.forEach {
                duration += it.duration
            }
        }
        else{
            trainingPlan.trainingPhaseList.filter{ it.isFinished }.forEach {
                duration += it.duration
            }
        }
        return duration
    }

    override fun getCurrentMoment(): Int{
        var duration = 0
        trainingPlan.trainingPhaseList.forEach{
            if(it.isFinished){
                duration += it.duration
            }
        }
        val timeInSeconds = durationBetweenMillisToSeconds(Calendar.getInstance().timeInMillis, lastPhaseStart)
        return duration + timeInSeconds
    }

    override fun getCurrentDistance(): Double{
        var distance = 0.0
        trainingPlan.trainingPhaseList.forEach {
            if(it.isFinished){
                distance += (it.duration.toDouble()/ SECONDS_IN_HOUR.toDouble())*it.speed
            }
        }

        val lastPhaseTimeInHours = durationBetweenMillisToSeconds(Calendar.getInstance().timeInMillis,lastPhaseStart).toDouble()/ SECONDS_IN_HOUR.toDouble()
        return round(distance + lastPhaseTimeInHours*treadmill.getSpeed(), DISTANCE_ROUND_MULTIPLIER)

    }

    override fun addNewPhase() {
        val lastPhaseTimeInSeconds = (millisecondsToSeconds(Calendar.getInstance().timeInMillis) - millisecondsToSeconds(lastPhaseStart)).toInt()
        if(lastPhaseTimeInSeconds>5 || trainingPlan.trainingPhaseList.isEmpty()){
            val phase = TrainingPhase(lastPhaseTimeInSeconds,
                treadmill.getSpeed(),
                treadmill.getTilt(),
                trainingPlan.ID,
                trainingPlan.trainingPhaseList.size,
                true)
            trainingPlan.addNewPhase(phase)
            lastPhaseStart = Calendar.getInstance().timeInMillis
        }
        else{
            val handler = Handler(Looper.getMainLooper())
            handler.postDelayed({
                if(lastPhaseTimeInSeconds>5){
                    val phase = TrainingPhase(lastPhaseTimeInSeconds,
                        treadmill.getSpeed(),
                        treadmill.getTilt(),
                        trainingPlan.ID,
                        trainingPlan.trainingPhaseList.size,
                        true)
                    trainingPlan.addNewPhase(phase)
                    lastPhaseStart = Calendar.getInstance().timeInMillis
                }
            }, 5000)
        }
    }
}