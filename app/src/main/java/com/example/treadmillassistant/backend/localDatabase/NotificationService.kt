package com.example.treadmillassistant.backend.localDatabase

import android.content.ContentValues
import android.content.Context

class NotificationService(context: Context): TrainingDatabaseService(context) {

    fun insertNewNotification(time: String): Long{
        val db = this.writableDatabase
        val contentValues = ContentValues().apply {
            put(TrainingDatabaseConstants.NotificationTable.notificationTime, time)
        }

        return db.insert(TrainingDatabaseConstants.NotificationTable.TABLE_NAME, null, contentValues)
    }
}