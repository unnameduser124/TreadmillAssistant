package com.example.treadmillassistant.backend.localDatabase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.example.treadmillassistant.backend.Treadmill
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TreadmillTable.MAX_SPEED
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TreadmillTable.MAX_TILT
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TreadmillTable.MIN_SPEED
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TreadmillTable.MIN_TILT
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TreadmillTable.TABLE_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TreadmillTable.TREADMILL_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TreadmillTable.USER_ID
import com.example.treadmillassistant.backend.user

class TreadmillService(context: Context): TrainingDatabaseService(context){

    //returns id for inserted object
    fun insertNewTreadmill(treadmill: Treadmill): Long {
        val db = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(TREADMILL_NAME, treadmill.name)
            put(MAX_SPEED, treadmill.maxSpeed)
            put(MIN_SPEED, treadmill.minSpeed)
            put(MAX_TILT, treadmill.maxTilt)
            put(MIN_TILT, treadmill.minTilt)
            put(USER_ID, user.ID)
        }

        return db.insert(TABLE_NAME, null, contentValues)
    }

    //returns number of rows deleted
    fun deleteTreadmill(id: Int, db: SQLiteDatabase): Int{

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$id")

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateTreadmill(newTreadmill: Treadmill, treadmillID: Int, db: SQLiteDatabase): Int{

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

    fun getUserTreadmills(): MutableList<Treadmill>{
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID,
            TREADMILL_NAME,
            MAX_SPEED,
            MIN_SPEED,
            MAX_TILT,
            MIN_TILT
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

        val treadmillList = mutableListOf<Treadmill>()
        with(cursor) {
            while (moveToNext()) {

                val treadmillID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val treadmillName = getString(getColumnIndexOrThrow(TREADMILL_NAME))
                val maxSpeed = getDouble(getColumnIndexOrThrow(MAX_SPEED))
                val minSpeed = getDouble(getColumnIndexOrThrow(MIN_SPEED))
                val maxTilt = getDouble(getColumnIndexOrThrow(MAX_TILT))
                val minTilt = getDouble(getColumnIndexOrThrow(MIN_TILT))

                treadmillList.add(Treadmill(name=treadmillName,
                    maxSpeed = maxSpeed,
                    minSpeed = minSpeed,
                    maxTilt = maxTilt,
                    minTilt = minTilt,
                    ID = treadmillID))
            }
        }
        cursor.close()

        return treadmillList
    }

    fun getTreadmillByID(ID: Long): Treadmill? {
        val db = this.readableDatabase

        val projection = arrayOf(BaseColumns._ID,
            TREADMILL_NAME,
            MAX_SPEED,
            MIN_SPEED,
            MAX_TILT,
            MIN_TILT
        )

        val sortOrder = "${BaseColumns._ID}"

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

        var treadmill: Treadmill? = null
        if((cursor != null) && (cursor.count > 0)){
            with(cursor) {
                moveToFirst()
                val treadmillID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val treadmillName = getString(getColumnIndexOrThrow(TREADMILL_NAME))
                val maxSpeed = getDouble(getColumnIndexOrThrow(MAX_SPEED))
                val minSpeed = getDouble(getColumnIndexOrThrow(MIN_SPEED))
                val maxTilt = getDouble(getColumnIndexOrThrow(MAX_TILT))
                val minTilt = getDouble(getColumnIndexOrThrow(MIN_TILT))

                treadmill = Treadmill(name=treadmillName,
                    maxSpeed = maxSpeed,
                    minSpeed = minSpeed,
                    maxTilt = maxTilt,
                    minTilt = minTilt,
                    ID = treadmillID)
            }
        }

        cursor.close()

        return treadmill
    }
}