package com.example.treadmillassistant.backend

import com.example.treadmillassistant.backend.workout.*
import com.example.treadmillassistant.hashMessage
import java.util.*
import java.util.concurrent.ThreadLocalRandom

var workoutCalendar = WorkoutCalendar()
var workoutPlanList = WorkoutPlanList()
lateinit var user: User

fun generateMockData(){
    workoutCalendar = WorkoutCalendar()
    var workoutPhaseList = mutableListOf<WorkoutPhase>()
    var newWorkoutPlanList = WorkoutPlanList()
    var workoutList = mutableListOf<Workout>()
    for(i in 0..9){
        var workoutPhase = WorkoutPhase((50..600).random(),
            ThreadLocalRandom.current().nextDouble(1.0, 20.0),
            ThreadLocalRandom.current().nextDouble(1.0, 20.0),
            i%3,
            i,
            false,
            i)
        workoutPhaseList.add(workoutPhase)
    }
    for(i in 0..2){
        var newWorkoutPlan = WorkoutPlan("$i")
        for(i in 0..4){
            newWorkoutPlan.workoutPhaseList.add(workoutPhaseList.random())
        }
        newWorkoutPlanList.addWorkoutPlan(newWorkoutPlan)
    }
    for(i in 0..100){
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2022)
        calendar.set(Calendar.MONTH, 6)
        calendar.set(Calendar.DAY_OF_MONTH, i%28+1)
        calendar.set(Calendar.HOUR, (0..23).random())
        calendar.set(Calendar.MINUTE, (0..60).random())
        workoutList.add(Workout(calendar,
            Treadmill(),
            "mediaLink",
            WorkoutStatus.Finished,
            newWorkoutPlanList.workoutPlanList[i%3],
            i))
    }

    workoutList.forEach {
        workoutCalendar.addNewWorkout(it)
    }
    workoutCalendar.workoutList.sortBy { it.workoutTime }
    workoutPlanList = newWorkoutPlanList
    user = User(workoutCalendar,"defaultUser@email.com", hashMessage("easyPassword"), "Jan", "Kowalski", "Janek", 20, 80.0)
    user.treadmillList.add(Treadmill(name = "treadmill one"))
    user.workoutPlanList = newWorkoutPlanList
}