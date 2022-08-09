package com.example.treadmillassistant.backend

import com.example.treadmillassistant.backend.training.TrainingCalendar
import com.example.treadmillassistant.backend.training.TrainingPlanList

class User(
    var trainingSchedule: TrainingCalendar = TrainingCalendar(),
    var email: String,
    var password: String = "",
    var firstName: String,
    var lastName: String,
    var username: String,
    var age: Int,
    var weight: Double,
    var treadmillList: MutableList<Treadmill> = mutableListOf(),
    var trainingPlanList: TrainingPlanList = TrainingPlanList(),
    var ID: Long = 0) {


    fun getTotalDistance(): Double{
        var totalDistance = 0.0
        trainingSchedule.trainingList.forEach {
            totalDistance+=it.getTotalDistance()
        }
        return round(totalDistance, DISTANCE_ROUND_MULTIPLIER)
    }

    fun getTotalDuration(): Double{
        var totalDuration = 0
        trainingSchedule.trainingList.forEach {
            totalDuration+=it.getTotalDuration()
        }
        return secondsToHours(totalDuration.toDouble())
    }

    fun getTotalCalories(): Int{
        var totalCalories = 0
        trainingSchedule.trainingList.forEach {
            totalCalories+= it.calculateCalories()
        }
        return totalCalories
    }

    fun getLongestDistance(): Double {
        var longestDistance = 0.0

        trainingSchedule.trainingList.forEach {
            if(it.getTotalDistance()>longestDistance){

                longestDistance = it.getTotalDistance()
            }
        }
        return round(longestDistance, DISTANCE_ROUND_MULTIPLIER)
    }

    fun getLongestDuration(): Double{
        var longestDuration = 0
        trainingSchedule.trainingList.forEach {
            if(it.getTotalDuration()>longestDuration){
                longestDuration = it.getTotalDuration()
            }
        }
        return secondsToHours(longestDuration.toDouble())
    }

    fun getTreadmillNames(): MutableList<String>{
        val treadmillNames = mutableListOf<String>()
        treadmillList.forEach {
            treadmillNames.add(it.name)
        }
        return treadmillNames
    }
}