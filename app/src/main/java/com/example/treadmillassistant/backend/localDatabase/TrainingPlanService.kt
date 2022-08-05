package com.example.treadmillassistant.backend.localDatabase

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPlanTable.PLAN_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPlanTable.TABLE_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPlanTable.USER_ID
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.training.TrainingPlan

class TrainingPlanService(val context: Context): TrainingDatabaseService(context){

    //returns id for inserted object
    fun insertNewTrainingPlan(trainingPlan: TrainingPlan): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(PLAN_NAME, trainingPlan.name)
            put(USER_ID, user.ID)
        }

        return db.insert(TABLE_NAME, null, contentValues)
    }

    //returns number of rows deleted
    fun deleteTrainingPlan(id: Int): Int{
        val db = this.writableDatabase
        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$id")

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateTrainingPlan(newTrainingPlan: TrainingPlan, trainingPlanID: Int): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(PLAN_NAME, newTrainingPlan.name)
            put(USER_ID, user.ID)
        }

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$trainingPlanID")

        return db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }

    //returns training plan list without phases
    fun getAllTrainingPlans(): MutableList<TrainingPlan>{
        val db = this.readableDatabase
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

        val planList = mutableListOf<TrainingPlan>()
        with(cursor) {
            while (moveToNext()) {
                val planID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val planName = getString(getColumnIndexOrThrow(PLAN_NAME))
                val userID = getLong(getColumnIndexOrThrow(USER_ID))
                planList.add(TrainingPlan(planName, userID = userID, ID = planID))
            }
        }
        cursor.close()

        return planList
    }

    //returns training plan with phases or null if there is no plan
    fun getTrainingPlanByID(ID: Long): TrainingPlan?{
        val db = this.readableDatabase

        val projection = arrayOf(BaseColumns._ID,
            PLAN_NAME,
            USER_ID
        )

        val sortOrder = BaseColumns._ID

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$ID")

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )

        var plan: TrainingPlan? = null

        if((cursor != null) && (cursor.count > 0)){
            with(cursor) {
                moveToFirst()
                val planID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val planName = getString(getColumnIndexOrThrow(PLAN_NAME))
                val userID = getLong(getColumnIndexOrThrow(USER_ID))

                plan = TrainingPlan(planName, userID = userID, ID = planID)
            }
        }
        if(plan!=null){
            var phaseList = TrainingPhaseService(context).getPhasesForTrainingPlan(ID)
            plan?.trainingPhaseList = phaseList
        }
        cursor.close()

        return plan
    }
}