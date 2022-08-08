package com.example.treadmillassistant.backend.training

import com.example.treadmillassistant.backend.SECONDS_IN_HOUR

class TrainingPlan(var name: String = "default name", var trainingPhaseList: MutableList<TrainingPhase> = mutableListOf<TrainingPhase>(), var userID: Long = 0, var ID: Long = -1) {

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
}