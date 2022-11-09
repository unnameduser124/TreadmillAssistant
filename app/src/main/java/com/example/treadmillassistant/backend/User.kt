package com.example.treadmillassistant.backend

import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerUser
import com.example.treadmillassistant.backend.training.TrainingCalendar
import com.example.treadmillassistant.backend.training.TrainingPlanList
import com.example.treadmillassistant.backend.training.TrainingStatus

class User(
    var email: String = "",
    var password: String = "",
    var firstName: String = "",
    var lastName: String = "",
    var username: String = "",
    var age: Int = 0,
    var weight: Double = 0.0,
    var treadmillList: MutableList<Treadmill> = mutableListOf(),
    var trainingPlanList: TrainingPlanList = TrainingPlanList(),
    var trainingSchedule: TrainingCalendar = TrainingCalendar(),
    var ID: Long = 0
) {

    constructor(serverUser: ServerUser, userID: Long) : this(
        serverUser.Email,
        "",
        serverUser.FirstName,
        serverUser.LastName,
        serverUser.Nick,
        serverUser.Age,
        serverUser.Weight,
        ID = userID)

    fun getTotalDistance(): Double{
        var totalDistance = 0.0
        trainingSchedule.trainingList.forEach {
            if(it.trainingStatus == TrainingStatus.Finished){
                totalDistance+=it.getTotalDistance()
            }
        }
        return round(totalDistance, DISTANCE_ROUND_MULTIPLIER)
    }

    fun getTotalDuration(): Double{
        var totalDuration = 0
        trainingSchedule.trainingList.forEach {
            if(it.trainingStatus == TrainingStatus.Finished){
                totalDuration+=it.getTotalDuration()
            }
        }
        return secondsToHours(totalDuration.toDouble())
    }

    fun getTotalCalories(): Int{
        var totalCalories = 0
        trainingSchedule.trainingList.forEach {
            if(it.trainingStatus == TrainingStatus.Finished){
                totalCalories+= it.calculateCalories()
            }
        }
        return totalCalories
    }

    fun getLongestDistance(): Double {
        var longestDistance = 0.0

        trainingSchedule.trainingList.forEach {
            if(it.getTotalDistance()>longestDistance && it.trainingStatus == TrainingStatus.Finished){
                longestDistance = it.getTotalDistance()
            }
        }
        return round(longestDistance, DISTANCE_ROUND_MULTIPLIER)
    }

    fun getLongestDuration(): Double{
        var longestDuration = 0
        trainingSchedule.trainingList.forEach {
            if(it.getTotalDuration()>longestDuration && it.trainingStatus == TrainingStatus.Finished){
                longestDuration = it.getTotalDuration()
            }
        }
        return secondsToHours(longestDuration.toDouble())
    }

    fun updateTreadmill(treadmill: Treadmill, treadmillID: Long) {
        val oldTreadmill = treadmillList.find{ it.ID == treadmillID }
        oldTreadmill!!.name = treadmill.name
        oldTreadmill.maxSpeed = treadmill.maxSpeed
        oldTreadmill.minSpeed = treadmill.minSpeed
        oldTreadmill.maxTilt = treadmill.maxTilt
        oldTreadmill.minTilt = treadmill.minTilt
    }

    fun removeTreadmill(treadmillID: Long){
        val deletedTreadmill = treadmillList.firstOrNull { it.ID == treadmillID }
        if(deletedTreadmill!=null){
            user.trainingSchedule.trainingList.filter { it.treadmill.ID == deletedTreadmill.ID}.forEach {
                it.treadmill = Treadmill(name = "select treadmill")
            }
            treadmillList.remove(deletedTreadmill)
        }
    }


}