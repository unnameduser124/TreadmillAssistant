package com.example.treadmillassistant.backend.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingPlanTable.PLAN_NAME
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingPlanTable.TABLE_NAME
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TrainingPlanTable.USER_ID
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.workout.WorkoutPlan

class TrainingPlanService(context: Context): TrainingDatabaseService(context) {

    //returns id for inserted object
    fun insertNewTrainingPlan(trainingPlan: WorkoutPlan): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(PLAN_NAME, trainingPlan.name)
            put(USER_ID, user.ID)
        }

        val newRowID = db.insert(TABLE_NAME, null, contentValues)
        return newRowID.toInt()
    }

    //returns number of rows deleted
    fun deleteTrainingPlan(id: Int): Int{
        val db = this.writableDatabase

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$id")

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateTrainingPlan(newTrainingPlan: WorkoutPlan, trainingPlanID: Int): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(PLAN_NAME, newTrainingPlan.name)
            put(USER_ID, user.ID)
        }

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$trainingPlanID")

        return db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }
}