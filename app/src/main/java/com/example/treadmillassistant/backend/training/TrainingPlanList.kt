package com.example.treadmillassistant.backend.training

import com.example.treadmillassistant.backend.user

class TrainingPlanList(var trainingPlanList: MutableList<TrainingPlan> = mutableListOf()) {

    fun addTrainingPlan(trainingPlan: TrainingPlan){
        trainingPlanList.add(trainingPlan)
    }
    fun removeTrainingPlan(trainingPlan: TrainingPlan){
        user.trainingSchedule.trainingList.filter{ it.trainingPlan.ID == trainingPlan.ID }.forEach {
            it.trainingPlan = TrainingPlan(name = "select training plan")
        }
        trainingPlanList.remove(trainingPlan)
    }

    fun getTrainingPlanByID(planID: Long): TrainingPlan?{
        return try{
            trainingPlanList.first{it.ID == planID}
        } catch (exception: NoSuchElementException){
            null
        }

    }

    fun getTrainingPlanNames(): MutableList<String>{
        val trainingPlanNames = mutableListOf<String>()
        trainingPlanList.forEach {
            trainingPlanNames.add(it.name)
        }
        return trainingPlanNames
    }

    fun updateTrainingPlan(newTrainingPlan: TrainingPlan, id: Long) {
        trainingPlanList.find{it.ID == id}?.updateTrainingPlan(newTrainingPlan)
    }

}