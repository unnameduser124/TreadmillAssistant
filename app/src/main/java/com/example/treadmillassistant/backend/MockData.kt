package com.example.treadmillassistant.backend

import android.content.Context
import com.example.treadmillassistant.backend.localDatabase.*
import com.example.treadmillassistant.backend.workout.*
import com.example.treadmillassistant.hashMessage
import java.util.*
import java.util.concurrent.ThreadLocalRandom

var workoutCalendar = WorkoutCalendar()
var workoutPlanList = WorkoutPlanList()
var tempUser: User? = null
var user = User(email = "defaultEmail@email.com",
    password = hashMessage("easypassword"),
    firstName = "Jan",
    lastName = "Kowalski",
    username = "Janek",
    age = 20,
    weight = 80.0
)


/*
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
            i.toLong())
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
            i.toLong())
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
}*/

fun generateDBdata(context: Context){
    val db = TrainingDatabaseService(context).writableDatabase
    val userService = UserService(context)

    val newUser = User(email = "defaultEmail@email.com",
        password = hashMessage("easypassword"),
        firstName = "Jan",
        lastName = "Kowalski",
        username = "Janek",
        age = 20,
        weight = 80.0
    )

    tempUser = userService.loadUser()
    if(tempUser!=null){
        user = tempUser as User
        return
    }
    userService.insertNewUser(newUser)
    val workoutPhaseList = mutableListOf<WorkoutPhase>()
    val trainingPhaseService = TrainingPhaseService(context)
    val trainingPlanService = TrainingPlanService(context)
    val newWorkoutPlanList = WorkoutPlanList()

    for(i in 0..4){
        val newWorkoutPlan = WorkoutPlan("Plan $i", userID = user.ID)
        val newID = trainingPlanService.insertNewTrainingPlan(newWorkoutPlan)
        newWorkoutPlan.ID = newID
        for(i in 0..5){
            val workoutPhase = WorkoutPhase((50..600).random(),
                ThreadLocalRandom.current().nextDouble(1.0, 20.0),
                ThreadLocalRandom.current().nextDouble(1.0, 20.0),
                (1L..5L).random(),
                i,
                false,
                i.toLong())
            val newID = trainingPhaseService.insertNewTrainingPhase(workoutPhase,db)
            workoutPhase.ID = newID
            workoutPhaseList.add(workoutPhase)
        }
        newWorkoutPlanList.addWorkoutPlan(newWorkoutPlan)
    }
    val treadmillService = TreadmillService(context)
    val treadmillOne = Treadmill(name = "TreadmillOne")
    val treadmillTwo = Treadmill(name = "TreadmillTwo")

    treadmillOne.ID = treadmillService.insertNewTreadmill(treadmillOne)
    treadmillTwo.ID = treadmillService.insertNewTreadmill(treadmillTwo)

    val trainingService = TrainingService(context)
    for(i in 0..100){
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2022)
        calendar.set(Calendar.MONTH, if(i%2==0) 7 else 6)
        calendar.set(Calendar.DAY_OF_MONTH, if(i%2==0) i%30+1 else i%31+1)
        calendar.set(Calendar.HOUR, (0..23).random())
        calendar.set(Calendar.MINUTE, (0..60).random())
        val newWorkout = Workout(calendar,
            if(i%2==0) treadmillOne else treadmillTwo,
            "mediaLink$i",
            if(calendar.get(Calendar.DAY_OF_YEAR)<Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) WorkoutStatus.Finished else WorkoutStatus.Upcoming,
            newWorkoutPlanList.workoutPlanList.random())
        trainingService.insertNewTraining(newWorkout,db)
    }
}

fun loadAllData(context: Context){
    val userService = UserService(context)
    user = userService.loadUser()!!

    val trainingService = TrainingService(context)

    val workoutIDs = trainingService.getAllTrainings()
    workoutIDs.forEach {
        val workout = trainingService.getTrainingByID(it.ID)
        user.workoutSchedule.addNewWorkout(workout!!)
    }
    val workoutPlanIDs = TrainingPlanService(context).getAllTrainingPlans()
    workoutPlanIDs.forEach {
        val workoutPlan = TrainingPlanService(context).getTrainingPlanForTraining(it.ID)
        user.workoutPlanList.addWorkoutPlan(workoutPlan!!)
    }
    user.treadmillList = TreadmillService(context).getUserTreadmills()
}
