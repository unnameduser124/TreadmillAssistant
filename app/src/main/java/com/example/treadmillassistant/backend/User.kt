package com.example.treadmillassistant.backend

import com.example.treadmillassistant.backend.workout.WorkoutCalendar
import com.example.treadmillassistant.backend.workout.WorkoutPlanList

class User(
    var workoutSchedule: WorkoutCalendar = WorkoutCalendar(),
    var email: String,
    var password: String = "",
    var firstName: String,
    var lastName: String,
    var username: String,
    var age: Int,
    var weight: Double,
    var treadmillList: MutableList<Treadmill> = mutableListOf(),
    var workoutPlanList: WorkoutPlanList = WorkoutPlanList(),
    var ID: Long = 0) {


    fun getTotalDistance(): Double{
        var totalDistance = 0.0
        workoutSchedule.workoutList.forEach {
            totalDistance+=it.getTotalDistance()
        }
        return round(totalDistance, DISTANCE_ROUND_MULTIPLIER)
    }

    fun getTotalDuration(): Double{
        var totalDuration = 0
        workoutSchedule.workoutList.forEach {
            totalDuration+=it.getTotalDuration()
        }
        return secondsToHours(totalDuration.toDouble())
    }

    fun getTotalCalories(): Int{
        var totalCalories = 0
        workoutSchedule.workoutList.forEach {
            totalCalories+= it.calculateCalories()
        }
        return totalCalories
    }

    fun getLongestDistance(): Double {
        var longestDistance = 0.0

        workoutSchedule.workoutList.forEach {
            if(it.getTotalDistance()>longestDistance){

                longestDistance = it.getTotalDistance()
            }
        }
        return round(longestDistance, DISTANCE_ROUND_MULTIPLIER)
    }

    fun getLongestDuration(): Double{
        var longestDuration = 0
        workoutSchedule.workoutList.forEach {
            if(it.getTotalDuration()>longestDuration){
                longestDuration = it.getTotalDuration()
            }
        }
        return secondsToHours(longestDuration.toDouble())
    }

    fun getTreadmillNames(): MutableList<String>{
        var treadmillNames = mutableListOf<String>()
        treadmillList.forEach {
            treadmillNames.add(it.name)
        }
        return treadmillNames
    }
}