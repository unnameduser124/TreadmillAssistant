package com.example.treadmillassistant.backend.training

import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPhase

class TrainingPhase(
    var duration: Int = 0,
    var speed: Double = 0.0,
    var tilt: Double = 0.0,
    var trainingPlanID: Long = -1,
    var orderNumber: Int = -1,
    var isFinished: Boolean = false,
    var ID: Long =-1) {
    constructor(serverPhase: ServerTrainingPhase) :
            this(
                serverPhase.Duration,
                serverPhase.Speed,
                serverPhase.Tilt,
                serverPhase.TrainingPlanID,
                serverPhase.OrderNumber,
                ID = serverPhase.ID)

    fun copy(): TrainingPhase{
        return TrainingPhase(duration, speed, tilt, trainingPlanID, orderNumber, ID = ID)
    }

    override fun equals(other: Any?): Boolean {
        if(other is TrainingPhase){
            if(
                duration == other.duration &&
                speed == other.speed &&
                tilt == other.tilt &&
                trainingPlanID == other.trainingPlanID &&
                orderNumber == other.orderNumber &&
                ID == other.ID
            ){ return true }
            return false
        }
        return super.equals(other)
    }
}