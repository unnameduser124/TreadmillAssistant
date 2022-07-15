package com.example.treadmillassistant.backend.workout

class WorkoutPlan(var name: String = "default name", var workoutPhaseList: MutableList<WorkoutPhase> = mutableListOf<WorkoutPhase>(), var userID: Int = 0) {

    fun addWorkoutPhaseList(workoutPhaseList: MutableList<WorkoutPhase>){
        this.workoutPhaseList = workoutPhaseList
    }

    fun addNewPhase(workoutPhase: WorkoutPhase){
        workoutPhaseList.add(workoutPhase)
    }

    fun removePhase(workoutPhase: WorkoutPhase){
        workoutPhaseList.remove(workoutPhase)
    }

    fun getPhase(phaseID: Int): WorkoutPhase{
        return workoutPhaseList.first {it.ID == phaseID}
    }
}