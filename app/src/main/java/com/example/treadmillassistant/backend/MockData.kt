package com.example.treadmillassistant.backend

import androidx.core.util.rangeTo
import com.example.treadmillassistant.backend.workout.*
import java.util.*
import java.util.concurrent.ThreadLocalRandom

var workoutCalendar = WorkoutCalendar()

fun generateMockData(){
    var workoutPhaseList = mutableListOf<WorkoutPhase>()
    var workoutPlanList = mutableListOf<WorkoutPlan>()
    var workoutList = mutableListOf<Workout>()
    for(i in 0..9){
        var workoutPhase = WorkoutPhase((1..50).random(),
            ThreadLocalRandom.current().nextDouble(1.0, 20.0),
            ThreadLocalRandom.current().nextDouble(1.0, 20.0),
            i%3,
            i,
            i)
        workoutPhaseList.add(workoutPhase)
    }
    for(i in 0..2){
        var newWorkoutPlan = WorkoutPlan()
        for(i in 0..4){
            newWorkoutPlan.workoutPhaseList.add(workoutPhaseList.random())
        }
        workoutPlanList.add(newWorkoutPlan)
    }
    for(i in 0..2){
        workoutList.add(Workout(Date(2022, 7, 1),
            (1..500).random(),
            Treadmill(),
            "mediaLink",
            WorkoutStatus.upcoming,
            0,
            workoutPlanList[i],
            i))
    }

    workoutList.forEach {
        workoutCalendar.addNewWorkout(it)
    }
}