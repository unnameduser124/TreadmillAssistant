package com.example.treadmillassistant.backend

import com.example.treadmillassistant.backend.workout.WorkoutCalendar

class User(var workoutSchedule: WorkoutCalendar = WorkoutCalendar(), var username: String, var password: String) {

}