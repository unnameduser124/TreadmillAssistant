package com.example.treadmillassistant.backend.serverDatabase.databaseClasses

import com.example.treadmillassistant.backend.training.Training
import java.text.SimpleDateFormat
import java.util.*

data class ServerTraining(
    var Date: String,
    var Time: String,
    val Link: String,
    val TrainingStatus: String,
    val Treadmill: Long = 0L,
    val TrainingPlanID: Long = 0L,
    var ID: Long = -1L){

    constructor(training: Training): this(
        "",
        "",
        training.mediaLink,
        training.trainingStatus.toString(),
        training.treadmill.ID,
        training.trainingPlan.ID
    ){
        val sdfDate = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        val date = sdfDate.format(training.trainingTime.time)
        Date = date
        val sdfTime = SimpleDateFormat("HH:mm", Locale.ROOT)
        val time = sdfTime.format(training.trainingTime.time)
        Time = time
        ID = training.ID
    }

}