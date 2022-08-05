package com.example.treadmillassistant.backend.training

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
}