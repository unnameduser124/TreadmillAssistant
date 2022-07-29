package com.example.treadmillassistant.backend.workout

import java.util.*

class WorkoutCalendar(var currentDate: Date = Date(), var workoutList: MutableList<Workout> = mutableListOf<Workout>()) {

    fun getWorkoutsForDateRange(start: Date, end: Date): MutableList<Workout>{
        return workoutList.filter{ it.workoutTime.time>start && it.workoutTime.time<end}.toMutableList()
    }

    fun addNewWorkout(workout: Workout){
        workoutList.add(workout)
        sortCalendar()
    }

    fun removeWorkout(workout: Workout){
        workoutList.remove(workout)
    }

    fun updateWorkout(oldWorkout: Workout, newWorkout: Workout){
        var workout = workoutList.find{it.ID == oldWorkout.ID} ?: oldWorkout
        workout.workoutTime = newWorkout.workoutTime
        workout.treadmill = newWorkout.treadmill
        workout.workoutPlan = newWorkout.workoutPlan
        workout.mediaLink = newWorkout.mediaLink
    }

    fun getWorkout(workoutID: Int): Workout{
        return workoutList.first{ it.ID == workoutID}
    }

    fun sortCalendar(){
        workoutList.sortBy{ it.workoutTime }
    }
}