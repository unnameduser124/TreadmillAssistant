package com.example.treadmillassistant.backend.serverDatabase.databaseClasses

data class ServerTraining(val Date: String,
                     val Time: String,
                     val Link: String,
                     val TrainingStatus: String,
                     val Treadmill: Int = 0,
                     val TrainingPlanID: Int = 0) {
}