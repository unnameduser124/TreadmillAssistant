package com.example.treadmillassistant.backend

import android.widget.DatePicker
import android.widget.TimePicker
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.Training
import java.util.*

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

fun calculateCaloriesForOngoingTraining(durationInSeconds: Int): Int{
    val MET = 8
    return (((MET * 3.5 * user.weight)/200).toInt() * secondsToMinutes(durationInSeconds)).toInt()
}

fun finishPhases(training: PlannedTraining){
    training.trainingPlan.trainingPhaseList.forEach {
        it.isFinished = true
    }
}
fun unfinishPhases(training: Training){
    training.trainingPlan.trainingPhaseList.forEach {
        it.isFinished = false
    }
}

fun setUpDatePicker(datePicker: DatePicker, date: Calendar){
    datePicker.updateDate(date.get(Calendar.YEAR), date.get(Calendar.MONTH), date.get(Calendar.DAY_OF_MONTH))
}

fun setUpTimePicker(timePicker: TimePicker, date: Calendar){
    timePicker.hour = date.get(Calendar.HOUR_OF_DAY)
    timePicker.minute = date.get(Calendar.MINUTE)
}

fun setUpDayCalendarCopy(calendar: Calendar): Calendar {
    val newCalendar = Calendar.getInstance()
    newCalendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH))
    newCalendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR))
    newCalendar.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH))
    return newCalendar
}

