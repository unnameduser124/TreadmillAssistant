package com.example.treadmillassistant.backend.serverDatabase.databaseClasses

import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.backend.training.TrainingPhase

data class ServerTrainingPhase(val Tilt: Double, val Speed: Double, val Duration: Int, val OrderNumber: Int, val TrainingPlanID: Long, val ID: Long = -1){

    constructor(trainingPhase: TrainingPhase): this(
        trainingPhase.tilt,
        trainingPhase.speed,
        trainingPhase.duration,
        trainingPhase.orderNumber,
        trainingPhase.trainingPlanID,
        trainingPhase.ID
    )

}