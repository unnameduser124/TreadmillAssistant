package com.example.treadmillassistant.backend.workout

class WorkoutPlan(var workoutPhaseList: MutableList<WorkoutPhase> = mutableListOf<WorkoutPhase>()) {

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

    fun getTotalDistance(): Double{
        var distance = 0.0
        workoutPhaseList.forEach {
            distance += (it.duration.toDouble()/3600.0)*it.speed
        }
        return distance
    }

    fun getTotalDuration(): Int{
        var duration = 0
        workoutPhaseList.forEach{
            duration += it.duration
        }
        return duration
    }
}