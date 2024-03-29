package com.example.treadmillassistant.backend.localDatabase

import android.content.ContentValues
import android.content.Context
import android.os.Build.ID
import android.provider.BaseColumns
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPlanTable.MODIFICATION_FLAG
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPlanTable.PLAN_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPlanTable.TABLE_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPlanTable.USER_ID
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPhase
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPlan
import com.example.treadmillassistant.backend.training.TrainingPhase
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.backend.user

class TrainingPlanService(val context: Context): TrainingDatabaseService(context){

    //returns id for inserted object
    fun insertNewTrainingPlan(trainingPlan: TrainingPlan): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(PLAN_NAME, trainingPlan.name)
            put(USER_ID, user.ID)
            put(MODIFICATION_FLAG, "C")
        }

        return db.insert(TABLE_NAME, null, contentValues)
    }

    //returns number of rows deleted
    fun deleteTrainingPlan(trainingPlan: TrainingPlan): Int{
        val db = this.writableDatabase
        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("${trainingPlan.ID}")
        val phaseService = TrainingPhaseService(context)

        trainingPlan.trainingPhaseList.forEach {
            phaseService.deleteTrainingPhase(it.ID)
        }
        db.execSQL(" UPDATE ${TrainingDatabaseConstants.TrainingTable.TABLE_NAME} " +
                "SET ${TrainingDatabaseConstants.TrainingTable.TRAINING_PLAN_ID} = -1 " +
                "WHERE ${TrainingDatabaseConstants.TrainingTable.TRAINING_PLAN_ID} = ${trainingPlan.ID}")


        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateTrainingPlan(newTrainingPlan: TrainingPlan, trainingPlanID: Long): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(PLAN_NAME, newTrainingPlan.name)
            put(USER_ID, user.ID)
            put(MODIFICATION_FLAG, "U")
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

        val sortOrder = BaseColumns._ID

        val selection = "$PLAN_NAME != ? "

        val selectionArgs = arrayOf("GenericTrainingPlan")

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
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

        val selection = "${BaseColumns._ID} = ? AND $PLAN_NAME != ? "

        val selectionArgs = arrayOf("$ID", "GenericTrainingPlan")

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
            val phaseList = TrainingPhaseService(context).getPhasesForTrainingPlan(ID)
            plan?.trainingPhaseList = phaseList
        }
        cursor.close()

        return plan
    }
}