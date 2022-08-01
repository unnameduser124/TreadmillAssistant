package com.example.treadmillassistant.backend.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.treadmillassistant.backend.Treadmill
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TreadmillTable.MAX_SPEED
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TreadmillTable.MAX_TILT
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TreadmillTable.MIN_SPEED
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TreadmillTable.MIN_TILT
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TreadmillTable.TABLE_NAME
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TreadmillTable.TREADMILL_NAME
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.TreadmillTable.USER_ID
import com.example.treadmillassistant.backend.user

class TreadmillService(context: Context): TrainingDatabaseService(context) {

    //returns id for inserted object
    fun insertNewTreadmill(treadmill: Treadmill): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues().apply{
            put(TREADMILL_NAME, treadmill.name)
            put(MAX_SPEED, treadmill.maxSpeed)
            put(MIN_SPEED, treadmill.minSpeed)
            put(MAX_TILT, treadmill.maxTilt)
            put(MIN_TILT, treadmill.minTilt)
            put(USER_ID, user.ID)
        }

        val newRowID = db.insert(TABLE_NAME, null, contentValues)

        return newRowID.toInt()
    }

    //returns number of rows deleted
    fun deleteTreadmill(id: Int): Int{
        val db = this.writableDatabase

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$id")

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateTreadmill(newTreadmill: Treadmill, treadmillID: Int): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues().apply{
            put(TREADMILL_NAME, newTreadmill.name)
            put(MAX_SPEED, newTreadmill.maxSpeed)
            put(MIN_SPEED, newTreadmill.minSpeed)
            put(MAX_TILT, newTreadmill.maxTilt)
            put(MIN_TILT, newTreadmill.minTilt)
            put(USER_ID, user.ID)
        }

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$treadmillID")

        return db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }
}