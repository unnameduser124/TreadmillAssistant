package com.example.treadmillassistant.backend.training

import android.content.Context
import com.example.treadmillassistant.backend.*
import com.example.treadmillassistant.backend.localDatabase.TrainingService
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTraining
import java.text.SimpleDateFormat
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
    
    constructor(serverTraining: ServerTraining): this(){
        val cal = Calendar.getInstance()
        ID = serverTraining.ID
        if(serverTraining.Time != null){
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.ROOT)
            val date = simpleDateFormat.parse("${serverTraining.Date} ${serverTraining.Time}")
            if (date != null) {
                cal.time = date
            }
        }
        else{
            val simpleDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
            val date = simpleDateFormat.parse(serverTraining.Date)
            if (date != null) {
                cal.time = date
            }
        }

        trainingTime = cal

        trainingPlan = TrainingPlan(ID=serverTraining.TrainingPlanID)
        treadmill = Treadmill(ID = serverTraining.Treadmill)
        if(serverTraining.Link!=null){
            mediaLink = serverTraining.Link
        }
        
        trainingStatus = if(serverTraining.TrainingStatus=="Upcoming"){
            TrainingStatus.Upcoming
        } else if(serverTraining.TrainingStatus=="Finished"){
            TrainingStatus.Finished
        } else if(serverTraining.TrainingStatus=="InProgress"){
            TrainingStatus.InProgress
        } else if(serverTraining.TrainingStatus=="Paused"){
            TrainingStatus.Paused
        } else {
            TrainingStatus.Abandoned
        }
    }


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
        val distance = trainingPlan.getDistance()

        return distance + partialPhaseCompletionDistance
    }

    override fun getTotalDuration(): Int {
        return trainingPlan.getDuration()
    }

    override fun getCurrentMoment(): Int{
        var duration = 0
        trainingPlan.trainingPhaseList.forEach{
            if(it.isFinished){
                duration += it.duration
            }
        }
        var timeInSeconds = 0
        if(trainingStatus == TrainingStatus.InProgress){
            timeInSeconds = durationBetweenMillisToSeconds(Calendar.getInstance().timeInMillis, lastPhaseStart)
        }
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

    override fun addNewPhase(){ }
}