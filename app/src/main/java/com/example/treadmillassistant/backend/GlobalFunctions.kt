package com.example.treadmillassistant.backend

import android.widget.DatePicker
import android.widget.TextView
import android.widget.TimePicker
import com.example.treadmillassistant.R
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.google.gson.ExclusionStrategy
import com.google.gson.FieldAttributes
import com.google.gson.Gson
import com.google.gson.GsonBuilder
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

fun serialize(item: Any): String{
    return Gson().toJson(item)
}

fun serializeWithExceptions(item: Any, exceptions: List<String>): String{
    val strategy: ExclusionStrategy = object : ExclusionStrategy {
        override fun shouldSkipField(field: FieldAttributes): Boolean {
            return exceptions.contains(field.name)
        }

        override fun shouldSkipClass(clazz: Class<*>?): Boolean {
            return false
        }
    }

    val gsonBuilder = GsonBuilder()
        .addSerializationExclusionStrategy(strategy)
        .create()

    return gsonBuilder.toJson(item)
}

fun getTrainingPlansWithPagination(start: Int, offset: Int, list: MutableList<TrainingPlan>): MutableList<TrainingPlan>{
    return if(start + offset < list.size){
        list.filter { list.indexOf(it) >= start && list.indexOf(it) < start + offset }.toMutableList()
    }
    else if (start < list.size){
        list.filter { list.indexOf(it) >= start && list.indexOf(it) < list.size }.toMutableList()
    }
    else{
        return mutableListOf()
    }
}

fun getTreadmillsWithPagination(start: Int, offset: Int, list: MutableList<Treadmill>): MutableList<Treadmill>{
    return if(start + offset < list.size){
        list.filter { list.indexOf(it) >= start && list.indexOf(it) < start + offset }.toMutableList()
    }
    else if (start < list.size){
        list.filter { list.indexOf(it) >= start && list.indexOf(it) < list.size }.toMutableList()
    }
    else{
        return mutableListOf()
    }
}

fun setUpDurationView(textView: TextView, duration: Int){
    if(duration>=6000){
        val minutes = duration / 60
        textView.text = String.format(
            textView.context.getString(R.string.duration_minutes),
            minutes
        )
    }
    if(duration<60){
        textView.text = String.format(textView.context.getString(R.string.duration_seconds), duration)
    }
    else{
        val seconds = duration % 60
        val minutes = duration / 60
        if(seconds == 0){
            textView.text = String.format(
                textView.context.getString(R.string.duration_minutes),
                minutes
            )
        }
        else if(seconds<10){
            textView.text = String.format(
                textView.context.getString(R.string.current_duration_less_than_10_seconds_calendar),
                minutes,
                seconds
            )
        }
        else {
            textView.text = String.format(
                textView.context.getString(R.string.current_duration_more_than_10_seconds_calendar),
                minutes,
                seconds
            )
        }
    }
}