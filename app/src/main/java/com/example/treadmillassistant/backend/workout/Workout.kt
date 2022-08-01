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

class Workout(var workoutTime: Calendar = Calendar.getInstance(),
              var treadmill: Treadmill = Treadmill(),
              var mediaLink: String = "",
              var workoutStatus: WorkoutStatus = WorkoutStatus.InProgress,
              var workoutPlan: WorkoutPlan = WorkoutPlan(),
              var ID: Int = 0,
              var trainingStartTime: Long = 0L,
              var lastPhaseStart: Long = 0L) {

    fun getCurrentPhase(): WorkoutPhase{
        var counter = 0
        workoutPlan.workoutPhaseList.sortBy { it.orderNumber }
        workoutPlan.workoutPhaseList.forEach {
            counter+=it.duration
            if(counter>getCurrentMoment()){
                return workoutPlan.workoutPhaseList[workoutPlan.workoutPhaseList.indexOf(it)-1]
            }
        }
        return workoutPlan.workoutPhaseList.first()
    }

    fun startWorkout(){
        workoutStatus = WorkoutStatus.InProgress
        lastPhaseStart = Calendar.getInstance().timeInMillis
        treadmill.setSpeed(DEFAULT_WORKOUT_START_SPEED)
        treadmill.setTilt(DEFAULT_WORKOUT_START_TILT)
        trainingStartTime = Calendar.getInstance().timeInMillis
    }

    fun pauseWorkout(){
        workoutStatus = WorkoutStatus.Paused
        val lastPhaseTimeInSeconds = (millisecondsToSeconds(Calendar.getInstance().timeInMillis) - millisecondsToSeconds(lastPhaseStart)).toInt()
        val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, workoutPlan.workoutPhaseList.size, true)
        workoutPlan.addNewPhase(phase)
    }

    fun resumeWorkout(){
        workoutStatus = WorkoutStatus.InProgress
        lastPhaseStart = Calendar.getInstance().timeInMillis
        trainingStartTime = Calendar.getInstance().timeInMillis
        treadmill.setSpeed(DEFAULT_WORKOUT_START_SPEED)
        treadmill.setTilt(DEFAULT_WORKOUT_START_TILT)
    }

    fun finishWorkout(){
        workoutStatus = WorkoutStatus.Finished
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
        workoutPlan.workoutPhaseList.forEach {
            distance += (it.duration.toDouble()/ SECONDS_IN_HOUR.toDouble())*it.speed
        }
        return distance
    }

    fun getTotalDuration(): Int{
        var duration = 0
        workoutPlan.workoutPhaseList.forEach{
            duration += it.duration
        }
        return duration
    }

    fun getCurrentMoment(): Int{
        var duration = 0
        workoutPlan.workoutPhaseList.forEach{
            if(it.isFinished){
                duration += it.duration
            }
        }
        val timeInSeconds = durationBetweenMillisToSeconds(Calendar.getInstance().timeInMillis, trainingStartTime)
        return duration + timeInSeconds
    }

    fun getCurrentDistance(): Double{
        var distance = 0.0
        workoutPlan.workoutPhaseList.forEach {
            if(it.isFinished){
                distance += (it.duration.toDouble()/ SECONDS_IN_HOUR.toDouble())*it.speed
            }
        }

        val lastPhaseTimeInHours = durationBetweenMillisToSeconds(Calendar.getInstance().timeInMillis,lastPhaseStart).toDouble()/ SECONDS_IN_HOUR.toDouble()
        return round(distance + lastPhaseTimeInHours*treadmill.getSpeed(), DISTANCE_ROUND_MULTIPLIER)

    }

    private fun addNewPhase(){
        val lastPhaseTimeInSeconds = (millisecondsToSeconds(Calendar.getInstance().timeInMillis) - millisecondsToSeconds(lastPhaseStart)).toInt()
        val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, workoutPlan.workoutPhaseList.size, true)
        workoutPlan.addNewPhase(phase)
        lastPhaseStart = Calendar.getInstance().timeInMillis
        trainingStartTime = Calendar.getInstance().timeInMillis
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