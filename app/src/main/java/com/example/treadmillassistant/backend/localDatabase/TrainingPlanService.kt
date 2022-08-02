package com.example.treadmillassistant.backend.localDatabase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPlanTable.PLAN_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPlanTable.TABLE_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPlanTable.USER_ID
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.workout.WorkoutPlan

class TrainingPlanService(){

    //returns id for inserted object
    fun insertNewTrainingPlan(trainingPlan: WorkoutPlan, db: SQLiteDatabase): Long {

        val contentValues = ContentValues().apply {
            put(PLAN_NAME, trainingPlan.name)
            put(USER_ID, user.ID)
        }

        return db.insert(TABLE_NAME, null, contentValues)
    }

    //returns number of rows deleted
    fun deleteTrainingPlan(id: Int, db: SQLiteDatabase): Int{

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$id")

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateTrainingPlan(newTrainingPlan: WorkoutPlan, trainingPlanID: Int, db: SQLiteDatabase): Int{

        val contentValues = ContentValues().apply {
            put(PLAN_NAME, newTrainingPlan.name)
            put(USER_ID, user.ID)
        }

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$trainingPlanID")

        return db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }

    //returns training plan list without phases
    fun getAllTrainingPlans(db: SQLiteDatabase): MutableList<WorkoutPlan>{

        val projection = arrayOf(BaseColumns._ID,
            PLAN_NAME,
            USER_ID
        )

        val sortOrder = "${BaseColumns._ID}"

        val cursor = db.query(
            TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )

        val planList = mutableListOf<WorkoutPlan>()
        with(cursor) {
            while (moveToNext()) {
                val planID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val planName = getString(getColumnIndexOrThrow(PLAN_NAME))
                val userID = getLong(getColumnIndexOrThrow(USER_ID))


                planList.add(WorkoutPlan(planName, userID = userID, ID = planID))
            }
        }
        cursor.close()

        return planList
    }

    //returns training plan with phases or null if there is no plan
    fun getTrainingPlanForTraining(trainingID: Int, db: SQLiteDatabase): WorkoutPlan?{

        val projection = arrayOf(BaseColumns._ID,
            PLAN_NAME,
            USER_ID
        )

        val sortOrder = "${BaseColumns._ID}"

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$trainingID")

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )

        var plan: WorkoutPlan? = null

        if((cursor != null) && (cursor.count > 0)){
            with(cursor) {
                moveToFirst()
                val planID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val planName = getString(getColumnIndexOrThrow(PLAN_NAME))
                val userID = getLong(getColumnIndexOrThrow(USER_ID))


                plan = WorkoutPlan(planName, userID = userID, ID = planID)
            }
        }
        cursor.close()

        return plan
    }
}