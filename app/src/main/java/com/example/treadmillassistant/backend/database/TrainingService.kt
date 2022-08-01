package com.example.treadmillassistant.backend.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingTable.MEDIA_LINK
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingTable.TABLE_NAME
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingTable.TRAINING_DATE
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingTable.TRAINING_PLAN_ID
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingTable.TRAINING_STATUS
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingTable.TRAINING_TIME
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingTable.TREADMILL_ID
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingTable.USER_ID
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.workout.Workout
import java.text.SimpleDateFormat
import java.util.*

class TrainingService(context: Context): TrainingDatabaseService(context) {

    //returns id for inserted object
    fun insertNewTraining(workout: Workout): Int{
        val db = this.writableDatabase
        var simpleDateFormat = SimpleDateFormat("dd.MM.${workout.workoutTime.get(Calendar.YEAR)}")
        val date = simpleDateFormat.format(workout.workoutTime.time)
        simpleDateFormat = SimpleDateFormat("kk:mm")
        val time = simpleDateFormat.format(workout.workoutTime.time)

        val contentValues = ContentValues().apply {
            put(TRAINING_DATE, date)
            put(TRAINING_TIME, time)
            put(MEDIA_LINK, workout.mediaLink)
            put(TRAINING_STATUS, "${workout.workoutStatus}")
            put(TREADMILL_ID, workout.treadmill.ID)
            put(TRAINING_PLAN_ID, workout.workoutPlan.ID)
            put(USER_ID, user.ID)
        }

        val newTrainingID = db.insert(TABLE_NAME, null, contentValues)
        return newTrainingID.toInt()
    }

    //returns number of rows deleted
    fun deleteTraining(id: Int): Int{
        val db = this.writableDatabase

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$id")

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateTraining(newWorkout: Workout, trainingID: Int): Int{
        val db = this.writableDatabase

        var simpleDateFormat = SimpleDateFormat("dd.MM.${newWorkout.workoutTime.get(Calendar.YEAR)}")
        val date = simpleDateFormat.format(newWorkout.workoutTime.time)
        simpleDateFormat = SimpleDateFormat("kk:mm")
        val time = simpleDateFormat.format(newWorkout.workoutTime.time)
        val contentValues = ContentValues().apply {
            put(TRAINING_DATE, date)
            put(TRAINING_TIME, time)
            put(MEDIA_LINK, newWorkout.mediaLink)
            put(TRAINING_STATUS, "${newWorkout.workoutStatus}")
            put(TREADMILL_ID, newWorkout.treadmill.ID)
            put(TRAINING_PLAN_ID, newWorkout.workoutPlan.ID)
            put(USER_ID, user.ID)
        }
        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$trainingID")

        return db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }
}