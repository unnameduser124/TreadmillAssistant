package com.example.treadmillassistant.backend.training

import com.example.treadmillassistant.backend.user
import java.util.*
import kotlin.NoSuchElementException

class TrainingCalendar(var currentDate: Date = Date(), var trainingList: MutableList<Training> = mutableListOf()) {

    fun getTrainingsForDateRange(start: Calendar, end: Calendar): MutableList<Training>{
        return trainingList.filter{ it.trainingTime.time>start.time && it.trainingTime.time<end.time}.toMutableList()
    }

    fun addNewTraining(training: Training){
        trainingList.add(training)
        sortCalendar()
    }

    fun removeTraining(training: Training){
        trainingList.remove(training)
    }

    fun updateTraining(oldTraining: Training?, newTraining: Training){
        val training = getTraining(oldTraining!!.ID)
        training!!.trainingTime = newTraining.trainingTime
        training.treadmill = newTraining.treadmill
        training.trainingPlan = newTraining.trainingPlan
        training.mediaLink = newTraining.mediaLink
    }

    fun getTraining(trainingID: Long): Training? {
        return try{
            trainingList.first{ it.ID == trainingID }
        }
        catch (exception: NoSuchElementException){
            null
        }
    }

    fun sortCalendar(){
        trainingList.sortBy{ it.trainingTime }
    }

    fun getNewID(): Long {
        return if (user.trainingSchedule.trainingList.isEmpty()) {
            1L
        } else {
            user.trainingSchedule.trainingList.sortBy { it.ID }
            val ID = user.trainingSchedule.trainingList.last().ID + 1
            user.trainingSchedule.sortCalendar()
            ID
        }
    }

    fun getTrainingsForDate(calendar: Calendar): MutableList<Training> {
        return user.trainingSchedule.trainingList.filter{ it.trainingTime.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH)
                && it.trainingTime.get(Calendar.MONTH) == calendar.get(Calendar.MONTH)
                && it.trainingTime.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)}.toMutableList()
    }
}