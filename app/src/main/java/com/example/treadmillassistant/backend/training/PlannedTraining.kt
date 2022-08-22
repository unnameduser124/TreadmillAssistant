package com.example.treadmillassistant.backend.training

import android.content.Context
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.localDatabase.TrainingService
import java.util.*

enum class TrainingStatus{
    Upcoming,
    InProgress,
    Paused,
    Finished,
    Abandoned
}

class PlannedTraining(
    override var trainingTime: Calendar = Calendar.getInstance(),
    override var treadmill: Treadmill = Treadmill(),
    override var mediaLink: String = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
    override var trainingStatus: TrainingStatus = TrainingStatus.InProgress,
    override var trainingPlan: TrainingPlan = TrainingPlan(),
    override var ID: Long = 0): Training {


    override var lastPhaseStart: Long = 0L
    override var phasePartialCompletion: Int = 0
    override var partialPhaseCompletionDistance: Double = 0.0
    override var lastPhase: TrainingPhase = TrainingPhase()

    fun getCurrentPhase(): TrainingPhase{
        var counter = 0
        trainingPlan.trainingPhaseList.sortBy { it.orderNumber }
        trainingPlan.trainingPhaseList.forEach {
            if(counter + phasePartialCompletion>getCurrentMoment()){
                return trainingPlan.trainingPhaseList[trainingPlan.trainingPhaseList.indexOf(it)-1]
            }
            counter+=it.duration
        }
        return trainingPlan.trainingPhaseList.last()
    }

    override fun finishTraining(context: Context){
        super.finishTraining(context)
        trainingPlan.trainingPhaseList.last().isFinished = true
        TrainingService(context).updateTraining(this, ID)
    }

    override fun pauseTraining(){
        trainingStatus = TrainingStatus.Paused

        if(getCurrentPhase() == lastPhase){
            phasePartialCompletion += durationBetweenMillisToSeconds(Calendar.getInstance().timeInMillis, lastPhaseStart)
            partialPhaseCompletionDistance += secondsToHoursNotRounded(durationBetweenMillisToSeconds(
                Calendar.getInstance().timeInMillis, lastPhaseStart
            ))*treadmill.getSpeed()
        }
        else{
            phasePartialCompletion = durationBetweenMillisToSeconds(Calendar.getInstance().timeInMillis, lastPhaseStart)
            partialPhaseCompletionDistance = getCurrentDistance()
            lastPhase = getCurrentPhase()
        }
    }

    override fun getTotalDistance(): Double{
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

        return distance + partialPhaseCompletionDistance
    }

    override fun getTotalDuration(): Int{
        var duration = 0
        trainingPlan.trainingPhaseList.forEach {
            duration += it.duration
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
        return duration + timeInSeconds + phasePartialCompletion
    }

    override fun getCurrentDistance(): Double{
        var distance = 0.0
        trainingPlan.trainingPhaseList.forEach {
            if(it.isFinished){
                distance += (it.duration.toDouble()/ SECONDS_IN_HOUR.toDouble())*it.speed
            }
        }

        val lastPhaseTimeInHours = durationBetweenMillisToSeconds(Calendar.getInstance().timeInMillis,lastPhaseStart).toDouble()/ SECONDS_IN_HOUR.toDouble()
        return round(distance + partialPhaseCompletionDistance + lastPhaseTimeInHours*treadmill.getSpeed(), DISTANCE_ROUND_MULTIPLIER)

    }

    override fun addNewPhase(){
        val lastPhaseTimeInSeconds = (millisecondsToSeconds(Calendar.getInstance().timeInMillis) - millisecondsToSeconds(lastPhaseStart)).toInt()
        val phase = TrainingPhase(lastPhaseTimeInSeconds,
            treadmill.getSpeed(),
            treadmill.getTilt(),
            trainingPlan.ID,
            trainingPlan.trainingPhaseList.size,
            true)
        trainingPlan.addNewPhase(phase)
        lastPhaseStart = Calendar.getInstance().timeInMillis
    }
}