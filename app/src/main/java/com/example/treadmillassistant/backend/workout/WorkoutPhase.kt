package com.example.treadmillassistant.backend.workout

class WorkoutPhase(var duration: Int = 0,
                   var speed: Double = 0.0,
                   var tilt: Double = 0.0,
                   var workoutPlanID: Int = -1,
                   var orderNumber: Int = -1,
                   val isFinished: Boolean = false,
                   val ID: Int=0) {
}