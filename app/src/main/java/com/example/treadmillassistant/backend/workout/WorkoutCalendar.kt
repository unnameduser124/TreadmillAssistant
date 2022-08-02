package com.example.treadmillassistant.backend.workout

import java.util.*

class WorkoutCalendar(var currentDate: Date = Date(), var workoutList: MutableList<Workout> = mutableListOf<Workout>()) {

    fun getWorkoutsForDateRange(start: Calendar, end: Calendar): MutableList<Workout>{
        return workoutList.filter{ it.workoutTime.time>start.time && it.workoutTime.time<end.time}.toMutableList()
    }

    fun addNewWorkout(workout: Workout){
        workoutList.add(workout)
        sortCalendar()
    }

    fun removeWorkout(workout: Workout){
        workoutList.remove(workout)
    }

    fun updateWorkout(oldWorkout: Workout?, newWorkout: Workout){
        var workout = getWorkout(oldWorkout!!.ID)
        workout!!.workoutTime = newWorkout.workoutTime
        workout!!.treadmill = newWorkout.treadmill
        workout!!.workoutPlan = newWorkout.workoutPlan
        workout!!.mediaLink = newWorkout.mediaLink
    }

    fun getWorkout(workoutID: Long): Workout? {
        return workoutList.find{ it.ID == workoutID}
    }

    fun sortCalendar(){
        workoutList.sortBy{ it.workoutTime }
    }
}