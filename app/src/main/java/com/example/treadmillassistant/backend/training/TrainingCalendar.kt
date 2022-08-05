package com.example.treadmillassistant.backend.training

import java.util.*

class TrainingCalendar(var currentDate: Date = Date(), var trainingLists: MutableList<Training> = mutableListOf()) {

    fun getTrainingsForDateRange(start: Calendar, end: Calendar): MutableList<Training>{
        return trainingLists.filter{ it.trainingTime.time>start.time && it.trainingTime.time<end.time}.toMutableList()
    }

    fun addNewTraining(training: Training){
        trainingLists.add(training)
        sortCalendar()
    }

    fun removeTraining(training: Training){
        trainingLists.remove(training)
    }

    fun updateTraining(oldTraining: Training?, newTraining: Training){
        var training = getTraining(oldTraining!!.ID)
        training!!.trainingTime = newTraining.trainingTime
        training!!.treadmill = newTraining.treadmill
        training!!.trainingPlan = newTraining.trainingPlan
        training!!.mediaLink = newTraining.mediaLink
    }

    fun getTraining(trainingID: Long): Training? {
        return trainingLists.find{ it.ID == trainingID}
    }

    fun sortCalendar(){
        trainingLists.sortBy{ it.trainingTime }
    }
}