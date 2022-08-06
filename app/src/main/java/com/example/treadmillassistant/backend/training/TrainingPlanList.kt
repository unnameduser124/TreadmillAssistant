package com.example.treadmillassistant.backend.training

class TrainingPlanList(var trainingPlanLists: MutableList<TrainingPlan> = mutableListOf()) {

    fun addTrainingPlan(trainingPlan: TrainingPlan){
        trainingPlanLists.add(trainingPlan)
    }
    fun removeTrainingPlan(trainingPlan: TrainingPlan){
        trainingPlanLists.remove(trainingPlan)
    }

    fun getTrainingPlanByID(planID: Long): TrainingPlan{
        return trainingPlanLists.first{it.ID == planID}
    }

    fun getTrainingPlanNames(): MutableList<String>{
        var trainingPlanNames = mutableListOf<String>()
        trainingPlanLists.forEach {
            trainingPlanNames.add(it.name)
        }
        return trainingPlanNames
    }

}