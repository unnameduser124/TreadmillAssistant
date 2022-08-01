package com.example.treadmillassistant.backend.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingPhaseTable.DURATION
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingPhaseTable.ORDER_NUMBER
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingPhaseTable.SPEED
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingPhaseTable.TABLE_NAME
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingPhaseTable.TILT
import com.example.treadmillassistant.backend.workout.WorkoutPhase

class TrainingPhaseService(context: Context): TrainingDatabaseService(context) {

    //returns id for inserted object
    fun insertNewTrainingPhase(trainingPhase: WorkoutPhase): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(SPEED, trainingPhase.speed)
            put(TILT, trainingPhase.tilt)
            put(DURATION, trainingPhase.duration)
            put(ORDER_NUMBER, trainingPhase.orderNumber)
        }

        val newRowId = db.insert(TABLE_NAME, null, contentValues)

        return newRowId.toInt()
    }

    //returns number of rows deleted
    fun deleteTrainingPhase(id: Int): Int{
        val db = this.writableDatabase

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$id")

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateTrainingPhase(newTrainingPhase: WorkoutPhase, trainingPhaseID: Int): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(SPEED, newTrainingPhase.speed)
            put(TILT, newTrainingPhase.tilt)
            put(DURATION, newTrainingPhase.duration)
            put(ORDER_NUMBER, newTrainingPhase.orderNumber)
        }
        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$trainingPhaseID")

        return db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }

}