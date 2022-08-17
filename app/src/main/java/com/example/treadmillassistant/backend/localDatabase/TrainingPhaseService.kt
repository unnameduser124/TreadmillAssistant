package com.example.treadmillassistant.backend.localDatabase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPhaseTable.DURATION
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPhaseTable.MODIFICATION_FLAG
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPhaseTable.ORDER_NUMBER
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPhaseTable.SPEED
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPhaseTable.TABLE_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPhaseTable.TILT
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingPhaseTable.TRAINING_PLAN_ID
import com.example.treadmillassistant.backend.training.TrainingPhase

class TrainingPhaseService(context: Context): TrainingDatabaseService(context){

    //returns id for inserted object
    fun insertNewTrainingPhase(trainingPhase: TrainingPhase): Long {
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(SPEED, trainingPhase.speed)
            put(TILT, trainingPhase.tilt)
            put(DURATION, trainingPhase.duration)
            put(ORDER_NUMBER, trainingPhase.orderNumber)
            put(TRAINING_PLAN_ID, trainingPhase.trainingPlanID)
            put(MODIFICATION_FLAG, "C")
        }

        return db.insert(TABLE_NAME, null, contentValues)
    }

    //returns number of rows deleted
    fun deleteTrainingPhase(id: Int): Int{//TODO("Needs to modify the flag not delete item, item is deleted after delete confirmation from server")

        val db = this.writableDatabase
        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$id")

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateTrainingPhase(newTrainingPhase: TrainingPhase, trainingPhaseID: Long): Int{
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(SPEED, newTrainingPhase.speed)
            put(TILT, newTrainingPhase.tilt)
            put(DURATION, newTrainingPhase.duration)
            put(ORDER_NUMBER, newTrainingPhase.orderNumber)
            put(TRAINING_PLAN_ID, newTrainingPhase.trainingPlanID)
            put(MODIFICATION_FLAG, "U")
        }
        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$trainingPhaseID")

        return db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }

    fun getPhasesForTrainingPlan(workoutPlanID: Long): MutableList<TrainingPhase>{
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID, SPEED, TILT, DURATION, ORDER_NUMBER, TRAINING_PLAN_ID)

        val selection = "$TRAINING_PLAN_ID = ?"
        val selectionArgs = arrayOf("$workoutPlanID")

        val sortOrder = ORDER_NUMBER

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )

        val phaseList = mutableListOf<TrainingPhase>()
        with(cursor) {
            while (moveToNext()) {
                val phaseID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val phaseSpeed = getDouble((getColumnIndexOrThrow(SPEED)))
                val phaseTilt = getDouble(getColumnIndexOrThrow(TILT))
                val phaseDuration = getInt(getColumnIndexOrThrow(DURATION))
                val orderNumber = getInt(getColumnIndexOrThrow(ORDER_NUMBER))
                val trainingPlanID = getLong(getColumnIndexOrThrow(TRAINING_PLAN_ID))
                phaseList.add(TrainingPhase(phaseDuration, phaseSpeed, phaseTilt, trainingPlanID, orderNumber, ID = phaseID))
            }
        }
        cursor.close()

        return phaseList
    }

}