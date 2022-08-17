package com.example.treadmillassistant.backend.training

class TrainingPhase(
    var duration: Int = 0,
    var speed: Double = 0.0,
    var tilt: Double = 0.0,
    var trainingPlanID: Long = -1,
    var orderNumber: Int = -1,
    var isFinished: Boolean = false,
    var ID: Long =-1)