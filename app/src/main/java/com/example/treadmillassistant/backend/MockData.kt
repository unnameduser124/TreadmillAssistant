package com.example.treadmillassistant.backend

import android.content.Context
import com.example.treadmillassistant.backend.localDatabase.*
import com.example.treadmillassistant.backend.training.*
import com.example.treadmillassistant.hashMessage
import java.util.*
import java.util.concurrent.ThreadLocalRandom

var trainingCalendar = TrainingCalendar()
var trainingPlanList = TrainingPlanList()
var tempUser: User? = null
var user = User(email = "defaultEmail@email.com",
    password = hashMessage("easyPassword"),
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

fun generateDBData(context: Context){
    val userService = UserService(context)

    tempUser = userService.loadUser()
    if(tempUser!=null){
        user = tempUser as User
        return
    }

    val newUser = User(email = "defaultEmail@email.com",
        password = hashMessage("easypassword"),
        firstName = "Jan",
        lastName = "Kowalski",
        username = "Janek",
        age = 20,
        weight = 80.0
    )
    userService.insertNewUser(newUser)

    val trainingPhaseService = TrainingPhaseService(context)
    val trainingPlanService = TrainingPlanService(context)
    val newTrainingPlanList = TrainingPlanList()

    for(i in 0..40){
        val newTrainingPlan = TrainingPlan("Plan ${i+1}", userID = user.ID)
        val newID = trainingPlanService.insertNewTrainingPlan(newTrainingPlan)
        newTrainingPlan.ID = newID
        for(j in 0..5){
            val trainingPhase = TrainingPhase((10..20).random(),
                ThreadLocalRandom.current().nextDouble(1.0, 20.0),
                ThreadLocalRandom.current().nextDouble(1.0, 20.0),
                -1,
                j,
                false)
            val newPhaseID = trainingPhaseService.insertNewTrainingPhase(trainingPhase)
            newTrainingPlan.trainingPhaseList.add(trainingPhase)
            trainingPhase.ID = newPhaseID
        }
        newTrainingPlanList.addTrainingPlan(newTrainingPlan)

        newTrainingPlanList.trainingPlanList.last().trainingPhaseList.forEach {
            it.trainingPlanID = newTrainingPlanList.trainingPlanList.last().ID
            trainingPhaseService.updateTrainingPhase(it, it.ID)
        }
    }

    val treadmillList = mutableListOf<Treadmill>()
    for(i in 0..1){
        val treadmillOne = Treadmill(name = "Treadmill${i+1}")
        val treadmillService = TreadmillService(context)
        treadmillOne.ID = treadmillService.insertNewTreadmill(treadmillOne)
        treadmillList.add(treadmillOne)
    }


    val trainingService = TrainingService(context)
    for(i in 0..100){
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.YEAR, 2022)
        calendar.set(Calendar.MONTH, if(i%2==0) 7 else 6)
        calendar.set(Calendar.DAY_OF_MONTH, if(i%2==0) i%30+1 else i%31+1)
        calendar.set(Calendar.HOUR, (0..23).random())
        calendar.set(Calendar.MINUTE, (0..60).random())

        val newTraining = PlannedTraining(calendar,
            treadmillList.random(),
            trainingStatus = if (calendar.get(Calendar.DAY_OF_YEAR)<Calendar.getInstance().get(Calendar.DAY_OF_YEAR)) TrainingStatus.Finished else TrainingStatus.Upcoming,
            trainingPlan = newTrainingPlanList.trainingPlanList.random())
        trainingService.insertNewTraining(newTraining)
    }
}

fun loadAllData(context: Context){
    user.treadmillList.clear()
    user.trainingPlanList.trainingPlanList.clear()
    user.trainingSchedule.trainingList.clear()

    val userService = UserService(context)
    user = userService.loadUser()!!

    val trainingService = TrainingService(context)
    user.trainingSchedule.trainingList = trainingService.getAllTrainings()

    val trainingPlanIDs = TrainingPlanService(context).getAllTrainingPlans()
    trainingPlanIDs.forEach {
        val trainingPlan = TrainingPlanService(context).getTrainingPlanByID(it.ID)
        user.trainingPlanList.addTrainingPlan(trainingPlan!!)
    }
    user.treadmillList = TreadmillService(context).getUserTreadmills()
    user.trainingSchedule.sortCalendar()
}
