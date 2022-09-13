package com.example.treadmillassistant.backend.training

import com.example.treadmillassistant.backend.SECONDS_IN_HOUR
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPhase
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPlan

class TrainingPlan(
    var name: String = "default name",
    var trainingPhaseList: MutableList<TrainingPhase> = mutableListOf(),
    var userID: Long = 0,
    var ID: Long = -1
) {

    fun addTrainingPhaseList(trainingPhaseList: MutableList<TrainingPhase>){
        this.trainingPhaseList = trainingPhaseList
    }

    fun addNewPhase(trainingPhase: TrainingPhase){
        trainingPhaseList.add(trainingPhase)
    }

    fun removePhase(trainingPhase: TrainingPhase){
        trainingPhaseList.remove(trainingPhase)
    }

    fun getPhase(phaseID: Long): TrainingPhase{
        return trainingPhaseList.first {it.ID == phaseID}
    }

    fun updateTrainingPlan(newTrainingPlan: TrainingPlan){
        name = newTrainingPlan.name
        trainingPhaseList = newTrainingPlan.trainingPhaseList
        userID = newTrainingPlan.userID
    }

    fun getDuration(): Int{
        var duration = 0

        trainingPhaseList.forEach {
            duration += it.duration
        }

        return duration
    }

    fun getDistance(): Double{
        var distance = 0.0

        trainingPhaseList.forEach {
                distance += (it.duration.toDouble()/ SECONDS_IN_HOUR.toDouble())*it.speed
            }

        return distance
    }

    fun fromServerTrainingPlan(serverPlan: ServerTrainingPlan, phaseList: MutableList<ServerTrainingPhase>){
        name = serverPlan.Name
        ID = serverPlan.ID
        phaseList.forEach {
            trainingPhaseList.add(TrainingPhase(it))
        }
    }

    fun copyPhaseList(): MutableList<TrainingPhase>{
        val list = mutableListOf<TrainingPhase>()

        trainingPhaseList.forEach {
            list.add(it.copy())
        }

        return list
    }
}