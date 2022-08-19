package com.example.treadmillassistant.backend.training

import android.content.Context
import com.example.treadmillassistant.backend.*
import java.util.*

interface Training {

    var trainingPlan: TrainingPlan
    var treadmill: Treadmill
    var trainingStatus: TrainingStatus
    var trainingTime: Calendar
    var mediaLink: String
    var ID: Long

    var lastPhaseStart: Long
    var phasePartialCompletion: Int
    var partialPhaseCompletionDistance: Double
    var lastPhase: TrainingPhase

    fun startTraining(){
        trainingStatus = TrainingStatus.InProgress
        lastPhaseStart = Calendar.getInstance().timeInMillis
        treadmill.setSpeed(DEFAULT_WORKOUT_START_SPEED)
        treadmill.setTilt(DEFAULT_WORKOUT_START_TILT)
    }

    fun resumeTraining(){
        trainingStatus = TrainingStatus.InProgress
        lastPhaseStart = Calendar.getInstance().timeInMillis
        treadmill.setSpeed(DEFAULT_WORKOUT_START_SPEED)
        treadmill.setTilt(DEFAULT_WORKOUT_START_TILT)
    }

    fun finishTraining(context: Context){
        trainingStatus = TrainingStatus.Finished
        phasePartialCompletion = 0
        partialPhaseCompletionDistance = 0.0
    }

    fun pauseTraining()

    fun speedUp(){
        addNewPhase()
        treadmill.increaseSpeed()
    }

    fun speedDown() {
        addNewPhase()
        treadmill.decreaseSpeed()
    }

    fun tiltUp(){
        addNewPhase()
        treadmill.increaseTilt()
    }

    fun tiltDown(){
        addNewPhase()
        treadmill.decreaseTilt()
    }

    fun getTotalDistance(): Double

    fun getTotalDuration(): Int

    fun getCurrentMoment(): Int

    fun getCurrentDistance(): Double

    fun addNewPhase()

    fun getAverageSpeed(): Double {
        return getTotalDistance() / secondsToHoursNotRounded(getTotalDuration())
    }

    fun getAverageTilt(): Double{
        var tiltSum = 0.0

        trainingPlan.trainingPhaseList.forEach {
            tiltSum += it.tilt*it.duration.toDouble()
        }

        return tiltSum/getTotalDuration().toDouble()
    }

    fun calculateCalories(): Int{
        val MET = getMETvalue(getTotalDistance() / secondsToHoursNotRounded(getTotalDuration()))
        return (((MET * 3.5 * user.weight)/200).toInt() * secondsToMinutes(getTotalDuration())).toInt()
    }

    fun getMETvalue(speed: Double): Double{
        if(speed<6.43){
            return 6.0
        }
        else if(speed<8.0){
            return 8.3
        }
        else if(speed<9.6){
            return 9.8
        }
        else if(speed<11.27){
            return 11.0
        }
        else if(speed<12.87){
            return 11.8
        }
        else if(speed<14.48){
            return 12.8
        }
        else if(speed<16.09){
            return 14.5
        }
        else if(speed<17.7){
            return 16.0
        }
        else if(speed<19.31){
            return 19.0
        }
        else if(speed<20.92){
            return 19.8
        }
        else{
            return 23.0
        }
    }
}