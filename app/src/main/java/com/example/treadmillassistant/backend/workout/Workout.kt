package com.example.treadmillassistant.backend.workout

import com.example.treadmillassistant.backend.*
import java.util.*

enum class WorkoutStatus{
    Upcoming,
    InProgress,
    Paused,
    Finished,
    Abandoned
}

class Workout(
    var workoutTime: Calendar = Calendar.getInstance(),
    var treadmill: Treadmill = Treadmill(),
    var mediaLink: String = "",
    var workoutStatus: WorkoutStatus = WorkoutStatus.InProgress,
    var workoutPlan: WorkoutPlan = WorkoutPlan(),
    var ID: Long = 0,
    val planned: Boolean = false) {


    var lastPhaseStart: Long = 0L
    var phasePartialCompletion: Int = 0
    var partialPhaseCompletionDistance: Double = 0.0
    var lastPhase: WorkoutPhase = WorkoutPhase()

    fun getCurrentPhase(): WorkoutPhase{
        var counter = 0
        workoutPlan.workoutPhaseList.sortBy { it.orderNumber }
        workoutPlan.workoutPhaseList.forEach {
            if(counter + phasePartialCompletion>getCurrentMoment()){
                return workoutPlan.workoutPhaseList[workoutPlan.workoutPhaseList.indexOf(it)-1]
            }
            counter+=it.duration
        }
        return workoutPlan.workoutPhaseList.last()
    }

    fun startWorkout(){
        workoutStatus = WorkoutStatus.InProgress
        lastPhaseStart = Calendar.getInstance().timeInMillis
        treadmill.setSpeed(DEFAULT_WORKOUT_START_SPEED)
        treadmill.setTilt(DEFAULT_WORKOUT_START_TILT)
    }

    fun pauseWorkout(){
        workoutStatus = WorkoutStatus.Paused
        if(!planned){
            val lastPhaseTimeInSeconds = (millisecondsToSeconds(Calendar.getInstance().timeInMillis) - millisecondsToSeconds(lastPhaseStart)).toInt()
            val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, workoutPlan.workoutPhaseList.size, true)
            workoutPlan.addNewPhase(phase)
        }
        else{
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
    }

    fun resumeWorkout(){
        workoutStatus = WorkoutStatus.InProgress
        lastPhaseStart = Calendar.getInstance().timeInMillis
        treadmill.setSpeed(DEFAULT_WORKOUT_START_SPEED)
        treadmill.setTilt(DEFAULT_WORKOUT_START_TILT)
    }

    fun finishWorkout(){
        workoutStatus = WorkoutStatus.Finished
        phasePartialCompletion = 0
        partialPhaseCompletionDistance = 0.0
    }

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

    fun getTotalDistance(): Double{
        var distance = 0.0

        if(workoutStatus != WorkoutStatus.Finished || workoutStatus == WorkoutStatus.Upcoming || workoutStatus == WorkoutStatus.Abandoned){
            workoutPlan.workoutPhaseList.forEach {
                distance += (it.duration.toDouble()/ SECONDS_IN_HOUR.toDouble())*it.speed
            }
        }
        else{
            workoutPlan.workoutPhaseList.filter{ it.isFinished }.forEach {
                distance += (it.duration.toDouble()/ SECONDS_IN_HOUR.toDouble())*it.speed
            }
        }

        return distance + partialPhaseCompletionDistance
    }

    fun getTotalDuration(): Int{
        var duration = 0
        if(workoutStatus != WorkoutStatus.Finished || workoutStatus == WorkoutStatus.Upcoming || workoutStatus == WorkoutStatus.Abandoned){
            workoutPlan.workoutPhaseList.forEach {
                duration += it.duration
            }
        }
        else{
            workoutPlan.workoutPhaseList.filter{ it.isFinished }.forEach {
                duration += it.duration
            }
        }
        return duration + phasePartialCompletion
    }

    fun getCurrentMoment(): Int{
        var duration = 0
        workoutPlan.workoutPhaseList.forEach{
            if(it.isFinished){
                duration += it.duration
            }
        }
        val timeInSeconds = durationBetweenMillisToSeconds(Calendar.getInstance().timeInMillis, lastPhaseStart)
        return duration + timeInSeconds + phasePartialCompletion
    }

    fun getCurrentDistance(): Double{
        var distance = 0.0
        workoutPlan.workoutPhaseList.forEach {
            if(it.isFinished){
                distance += (it.duration.toDouble()/ SECONDS_IN_HOUR.toDouble())*it.speed
            }
        }

        val lastPhaseTimeInHours = durationBetweenMillisToSeconds(Calendar.getInstance().timeInMillis,lastPhaseStart).toDouble()/ SECONDS_IN_HOUR.toDouble()
        return round(distance + partialPhaseCompletionDistance + lastPhaseTimeInHours*treadmill.getSpeed(), DISTANCE_ROUND_MULTIPLIER)

    }

    private fun addNewPhase(){
        val lastPhaseTimeInSeconds = (millisecondsToSeconds(Calendar.getInstance().timeInMillis) - millisecondsToSeconds(lastPhaseStart)).toInt()
        val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), workoutPlan.ID, workoutPlan.workoutPhaseList.size, true)
        workoutPlan.addNewPhase(phase)
        lastPhaseStart = Calendar.getInstance().timeInMillis
    }

    fun getAverageSpeed(): Double {
        return getTotalDistance() / secondsToHoursNotRounded(getTotalDuration())
    }

    fun getAverageTilt(): Double{
        var tiltSum = 0.0

        workoutPlan.workoutPhaseList.forEach {
            tiltSum += it.tilt*it.duration.toDouble()
        }

        return tiltSum/getTotalDuration().toDouble()
    }

    fun calculateCalories(): Int{
        val MET = getMETvalue(getTotalDistance() / secondsToHoursNotRounded(getTotalDuration()))
        return (((MET * 3.5 * user.weight)/200).toInt() * secondsToMinutes(getTotalDuration())).toInt()
    }

    private fun getMETvalue(speed: Double): Double{
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