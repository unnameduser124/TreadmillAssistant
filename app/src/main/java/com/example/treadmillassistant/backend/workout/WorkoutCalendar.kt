package com.example.treadmillassistant.backend.workout

import java.util.*

class WorkoutCalendar(var currentDate: Date = Date(), var workoutList: MutableList<Workout> = mutableListOf<Workout>()) {

    fun getWorkoutsForDateRange(start: Date, end: Date): MutableList<Workout>{
        return workoutList.filter{ it.workoutTime>start && it.workoutTime<end}.toMutableList()
    }

    fun addNewWorkout(workout: Workout){
        workoutList.add(workout)
    }

    fun removeWorkout(workout: Workout){
        workoutList.remove(workout)
    }

    fun getWorkout(workoutID: Int): Workout{
        return workoutList.first{ it.ID == workoutID}
    }
}