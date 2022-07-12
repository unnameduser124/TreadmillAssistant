package com.example.treadmillassistant.backend.workout

import com.example.treadmillassistant.backend.Treadmill
import com.example.treadmillassistant.backend.user
import java.util.*

enum class WorkoutStatus{
    Upcoming,
    InProgress,
    Paused,
    Finished,
    Abandoned
}

class Workout(var workoutTime: Date = Date(),
              var workoutDuration: Int = 0,
              var treadmill: Treadmill = Treadmill(),
              var mediaLink: String = "",
              var workoutStatus: WorkoutStatus = WorkoutStatus.InProgress,
              var workoutPlan: WorkoutPlan = WorkoutPlan(),
              val ID: Int = 0,
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
        treadmill.setSpeed(10.0)
        treadmill.setTilt(0.0)
        trainingStartTime = Calendar.getInstance().timeInMillis
    }

    fun pauseWorkout(){
        workoutStatus = WorkoutStatus.Paused
        val lastPhaseTimeInSeconds = (Calendar.getInstance().timeInMillis/1000 - lastPhaseStart/1000).toInt()
        val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, workoutPlan.workoutPhaseList.size, true)
        workoutPlan.addNewPhase(phase)
    }

    fun resumeWorkout(){
        workoutStatus = WorkoutStatus.InProgress
        lastPhaseStart = Calendar.getInstance().timeInMillis
        trainingStartTime = Calendar.getInstance().timeInMillis
        treadmill.setSpeed(10.0)
        treadmill.setTilt(0.0)
    }

    fun finishWorkout(){
        workoutStatus = WorkoutStatus.Finished
    }

    fun speedUp(){
        val lastPhaseTimeInSeconds = (Calendar.getInstance().timeInMillis/1000 - lastPhaseStart/1000).toInt()
        val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, workoutPlan.workoutPhaseList.size, true)
        workoutPlan.addNewPhase(phase)
        lastPhaseStart = Calendar.getInstance().timeInMillis
        trainingStartTime = Calendar.getInstance().timeInMillis
        treadmill.increaseSpeed()
    }

    fun speedDown() {
        val lastPhaseTimeInSeconds = (Calendar.getInstance().timeInMillis/1000 - lastPhaseStart/1000).toInt()
        val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, workoutPlan.workoutPhaseList.size, true)
        workoutPlan.addNewPhase(phase)
        lastPhaseStart = Calendar.getInstance().timeInMillis
        trainingStartTime = Calendar.getInstance().timeInMillis
        treadmill.decreaseSpeed()
    }

    fun tiltUp(){
        val lastPhaseTimeInSeconds = (Calendar.getInstance().timeInMillis/1000 - lastPhaseStart/1000).toInt()
        val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, workoutPlan.workoutPhaseList.size, true)
        workoutPlan.addNewPhase(phase)
        lastPhaseStart = Calendar.getInstance().timeInMillis
        trainingStartTime = Calendar.getInstance().timeInMillis
        treadmill.increaseTilt()
    }

    fun tiltDown(){
        val lastPhaseTimeInSeconds = (Calendar.getInstance().timeInMillis/1000 - lastPhaseStart/1000).toInt()
        val phase = WorkoutPhase(lastPhaseTimeInSeconds, treadmill.getSpeed(), treadmill.getTilt(), 0, workoutPlan.workoutPhaseList.size, true)
        workoutPlan.addNewPhase(phase)
        lastPhaseStart = Calendar.getInstance().timeInMillis
        trainingStartTime = Calendar.getInstance().timeInMillis
        treadmill.decreaseTilt()
    }

    fun getTotalDistance(): Double{
        var distance = 0.0
        workoutPlan.workoutPhaseList.forEach {
            distance += (it.duration.toDouble()/3600.0)*it.speed
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
        val timeInSeconds = Calendar.getInstance().timeInMillis/1000 - trainingStartTime/1000
        return duration + timeInSeconds.toInt()
    }

    fun getCurrentDistance(): Double{
        var distance = 0.0
        workoutPlan.workoutPhaseList.forEach {
            if(it.isFinished){
                distance += (it.duration.toDouble()/3600.0)*it.speed
            }
        }

        val lastPhaseTimeInHours = (Calendar.getInstance().timeInMillis/1000 - lastPhaseStart/1000).toDouble()/3600.0
        return Math.round((distance + lastPhaseTimeInHours*treadmill.getSpeed())*100.0)/100.0

    }


}