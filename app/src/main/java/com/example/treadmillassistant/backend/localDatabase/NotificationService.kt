package com.example.treadmillassistant.backend.localDatabase

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns

class NotificationService(context: Context): TrainingDatabaseService(context) {

    fun insertNewNotification(trainingID: Long): Long{
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(TrainingDatabaseConstants.NotificationTable.TRAINING_ID, trainingID)
        }

        return db.insert(TrainingDatabaseConstants.NotificationTable.TABLE_NAME, null, contentValues)
    }

    fun getNotificationByTrainingID(trainingID: Long): Pair<Long, String>{
        val db = this.readableDatabase
        val projection = arrayOf(
            BaseColumns._ID,
            TrainingDatabaseConstants.NotificationTable.NOTIFICATION_TIME
        )

        val selection = "${TrainingDatabaseConstants.NotificationTable.TRAINING_ID} = ?"
        val selectionArgs = arrayOf("$trainingID")

        val sortOrder = BaseColumns._ID

        val cursor = db.query(
            TrainingDatabaseConstants.NotificationTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )

        var pair = Pair(-1L, "99:99")

        with(cursor){
            if(moveToFirst()){
                pair = Pair(getLong(getColumnIndexOrThrow(BaseColumns._ID)), getString(getColumnIndexOrThrow(TrainingDatabaseConstants.NotificationTable.NOTIFICATION_TIME)))
            }
        }

        return pair
    }
}