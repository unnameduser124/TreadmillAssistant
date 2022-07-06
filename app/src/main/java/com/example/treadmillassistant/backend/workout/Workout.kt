package com.example.treadmillassistant.backend.workout

import com.example.treadmillassistant.backend.Treadmill
import java.util.*

enum class WorkoutStatus{
    upcoming,
    inProgress,
    finished,
    abandoned
}

class Workout(var workoutTime: Date = Date(),
              var workoutDuration: Int = 0,
              var treadmill: Treadmill = Treadmill(),
              var mediaLink: String = "",
              var workoutStatus: WorkoutStatus = WorkoutStatus.inProgress,
              var currentMoment: Int = 0,
              var workoutPlan: WorkoutPlan = WorkoutPlan(),
              val ID: Int = 0) {

    fun getCurrentPhase(): WorkoutPhase{
        var counter = 0
        workoutPlan.workoutPhaseList.sortBy { it.orderNumber }
        workoutPlan.workoutPhaseList.forEach {
            counter+=it.duration
            if(counter>currentMoment){
                return workoutPlan.workoutPhaseList[workoutPlan.workoutPhaseList.indexOf(it)-1]
            }
        }
        return workoutPlan.workoutPhaseList.first()
    }

}