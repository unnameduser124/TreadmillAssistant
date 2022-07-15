package com.example.treadmillassistant.backend

import com.example.treadmillassistant.backend.workout.Workout
import com.example.treadmillassistant.backend.workout.WorkoutCalendar

class User(var workoutSchedule: WorkoutCalendar = WorkoutCalendar(),
           var email: String,
           var password: String,
           var firstName: String,
           var lastName: String,
           var username: String,
           var age: Int,
           var weight: Double,
           var treadmillList: MutableList<Treadmill> = mutableListOf<Treadmill>(),
           var ID: Int = 0) {


    fun getTotalDistance(): Double{
        var totalDistance = 0.0
        workoutSchedule.workoutList.forEach {
            totalDistance+=it.getTotalDistance()
        }
        return Math.round(totalDistance*10.0)/10.0
    }

    fun getTotalDuration(): Double{
        var totalDuration = 0
        workoutSchedule.workoutList.forEach {
            totalDuration+=it.getTotalDuration()
        }
        return Math.round((totalDuration.toDouble()/3600.toDouble())*10.0)/10.0
    }

    fun getLongestDistance(): Double {
        var longestDistance = 0.0

        workoutSchedule.workoutList.forEach {
            if(it.getTotalDistance()>longestDistance){

                longestDistance = it.getTotalDistance()
            }
        }
        return Math.round((longestDistance)*10.0)/10.0
    }

    fun getLongestDuration(): Double{
        var longestDuration = 0
        workoutSchedule.workoutList.forEach {
            if(it.getTotalDuration()>longestDuration){
                longestDuration = it.getTotalDuration()
            }
        }
        return Math.round((longestDuration.toDouble()/3600.toDouble())*10.0)/10.0
    }

    fun getTreadmillNames(): MutableList<String>{
        var treadmillNames = mutableListOf<String>()
        treadmillList.forEach {
            treadmillNames.add(it.name)
        }
        return treadmillNames
    }
}