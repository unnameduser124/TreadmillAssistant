package com.example.treadmillassistant.backend.training

class TrainingPlanList(var trainingPlanList: MutableList<TrainingPlan> = mutableListOf()) {

    fun addTrainingPlan(trainingPlan: TrainingPlan){
        trainingPlanList.add(trainingPlan)
    }
    fun removeTrainingPlan(trainingPlan: TrainingPlan){
        trainingPlanList.remove(trainingPlan)
    }

    fun getTrainingPlanByID(planID: Long): TrainingPlan{
        return trainingPlanList.first{it.ID == planID}
    }

    fun getTrainingPlanNames(): MutableList<String>{
        var trainingPlanNames = mutableListOf<String>()
        trainingPlanList.forEach {
            trainingPlanNames.add(it.name)
        }
        return trainingPlanNames
    }

}