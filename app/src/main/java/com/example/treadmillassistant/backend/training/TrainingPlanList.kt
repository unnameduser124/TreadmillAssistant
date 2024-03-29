package com.example.treadmillassistant.backend.training

import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPlan
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerTrainingPlanService
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.StatusCode
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

    fun updateTrainingPlan(newTrainingPlan: TrainingPlan, id: Long) {
        trainingPlanList.find{it.ID == id}?.updateTrainingPlan(newTrainingPlan)
    }

}