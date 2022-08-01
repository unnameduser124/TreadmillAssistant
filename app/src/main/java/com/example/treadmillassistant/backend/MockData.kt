package com.example.treadmillassistant.backend

import android.content.Context
import com.example.treadmillassistant.backend.database.*
import com.example.treadmillassistant.backend.workout.*
import com.example.treadmillassistant.hashMessage
import java.util.*
import java.util.concurrent.ThreadLocalRandom

var workoutCalendar = WorkoutCalendar()
var workoutPlanList = WorkoutPlanList()
var user: User = User(workoutCalendar,"defaultUser@email.com", hashMessage("easyPassword"), "Jan", "Kowalski", "Janek", 20, 80.0, ID = 1)


fun generateMockData(context: Context){

    val trainingPhaseService = TrainingPhaseService(context)

    workoutCalendar = WorkoutCalendar()
    var workoutPhaseList = mutableListOf<WorkoutPhase>()
    var newWorkoutPlanList = WorkoutPlanList()
    var workoutList = mutableListOf<Workout>()
    for(i in 0..9){
        val workoutPhase = WorkoutPhase((50..600).random(),
            ThreadLocalRandom.current().nextDouble(1.0, 20.0),
            ThreadLocalRandom.current().nextDouble(1.0, 20.0),
            i%3,
            i,
            false,
            i)
        val newID = trainingPhaseService.insertNewTrainingPhase(workoutPhase)
        workoutPhase.ID = newID
        workoutPhaseList.add(workoutPhase)

    }

    val trainingPlanService = TrainingPlanService(context)

    for(i in 0..2){
        val newWorkoutPlan = WorkoutPlan("$i", userID = user.ID)
        val newID = trainingPlanService.insertNewTrainingPlan(newWorkoutPlan)
        newWorkoutPlan.ID = newID
        for(i in 0..4){
            newWorkoutPlan.workoutPhaseList.add(workoutPhaseList.random())
        }
        newWorkoutPlanList.addWorkoutPlan(newWorkoutPlan)
    }

    TreadmillService(context).insertNewTreadmill(Treadmill())

    for(i in 0..100){
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2022)
        calendar.set(Calendar.MONTH, 6)
        calendar.set(Calendar.DAY_OF_MONTH, i%28+1)
        calendar.set(Calendar.HOUR, (0..23).random())
        calendar.set(Calendar.MINUTE, (0..60).random())
        val newWorkout = Workout(calendar,
            Treadmill(ID = 1),
            "mediaLink",
            WorkoutStatus.Finished,
            newWorkoutPlanList.workoutPlanList[i%3],
            i)
        val newID = TrainingService(context).insertNewTraining(newWorkout)
        newWorkout.ID = newID
        workoutList.add(newWorkout)
    }

    workoutList.forEach {
        workoutCalendar.addNewWorkout(it)
    }
    workoutCalendar.workoutList.sortBy { it.workoutTime }
    workoutPlanList = newWorkoutPlanList
    user = User(workoutCalendar,"defaultUser@email.com", hashMessage("easyPassword"), "Jan", "Kowalski", "Janek", 20, 80.0, ID = 1)
    user.treadmillList.add(Treadmill(name = "treadmill one"))
    UserService(context).insertNewUser(user)
    user.workoutPlanList = newWorkoutPlanList
}