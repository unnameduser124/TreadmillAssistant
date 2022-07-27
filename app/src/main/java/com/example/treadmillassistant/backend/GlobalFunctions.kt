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

fun calculateCaloriesForWorkout(workout: Workout): Int{
    val MET = getMETvalue(workout.getTotalDistance() / secondsToHoursNotRounded(workout.getTotalDuration()))
    return (((MET * 3.5 * user.weight)/200).toInt() * secondsToMinutes(workout.getTotalDuration())).toInt()
}

fun getMETvalue(speed: Double): Double{
    if(speed<6.43){
        return 6.0
    }
    else if(speed<8.0){
        return 8.3
    }
    else if(speed<9.6){
        return 9.8
    }
    else if(speed<11.27){
        return 11.0
    }
    else if(speed<12.87){
        return 11.8
    }
    else if(speed<14.48){
        return 12.8
    }
    else if(speed<16.09){
        return 14.5
    }
    else if(speed<17.7){
        return 16.0
    }
    else if(speed<19.31){
        return 19.0
    }
    else if(speed<20.92){
        return 19.8
    }
    else{
        return 23.0
    }
}
