package com.example.treadmillassistant.backend.workout

class WorkoutPlanList(var workoutPlanList: MutableList<WorkoutPlan> = mutableListOf<WorkoutPlan>()) {

    fun addWorkoutPlan(workoutPlan: WorkoutPlan){
        workoutPlanList.add(workoutPlan)
    }
    fun removeWorkoutPlan(workoutPlan: WorkoutPlan){
        workoutPlanList.remove(workoutPlan)
    }

    fun getWorkoutPlanNames(): MutableList<String>{
        var workoutPlanNames = mutableListOf<String>()
        workoutPlanList.forEach {
            workoutPlanNames.add(it.name)
        }
        return workoutPlanNames
    }

}