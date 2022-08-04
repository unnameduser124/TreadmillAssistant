package com.example.treadmillassistant.backend

import com.example.treadmillassistant.backend.workout.Workout

fun round(variableRounded: Double, multiplier: Double): Double{
    return Math.round(variableRounded * multiplier) / multiplier
}

fun secondsToMinutes(duration: Int): Double{
    return duration.toDouble() / SECONDS_IN_MINUTE
}

fun minutesToSeconds(duration: Double): Int{
    return round(duration * SECONDS_IN_MINUTE.toDouble(), 1.0).toInt()
}

fun millisecondsToSeconds(millis: Long): Long{
    return (millis / MILLIS_IN_SECOND)
}

fun secondsToHours(duration: Double): Double{
    return round((duration / SECONDS_IN_HOUR.toDouble()), DURATION_ROUND_MULTIPLIER)
}

fun secondsToHoursNotRounded(duration: Int): Double{
    return (duration / SECONDS_IN_HOUR.toDouble())
}

fun durationBetweenMillisToSeconds(start: Long, end: Long): Int{
    return (millisecondsToSeconds(start)- millisecondsToSeconds(end)).toInt()
}

fun calculateCaloriesForOngoingWorkout(durationInSeconds: Int): Int{
    val MET = 8
    return (((MET * 3.5 * user.weight)/200).toInt() * secondsToMinutes(durationInSeconds)).toInt()
}

fun finishPhases(workout: Workout){
    workout.workoutPlan.workoutPhaseList.forEach {
        it.isFinished = true
    }
}
fun unfinishPhases(workout: Workout){
    workout.workoutPlan.workoutPhaseList.forEach {
        it.isFinished = false
    }
}
