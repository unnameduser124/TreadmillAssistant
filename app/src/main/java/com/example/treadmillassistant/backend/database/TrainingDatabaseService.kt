package com.example.treadmillassistant.backend.database

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.treadmillassistant.backend.User
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.SQL_CREATE_TRAINING_PHASE_TABLE
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.SQL_CREATE_TRAINING_PLAN_TABLE
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.SQL_CREATE_TRAINING_TABLE
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.SQL_CREATE_TREADMILL_TABLE
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.SQL_CREATE_USER_TABLE

open class TrainingDatabaseService(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        println("DATABASE CREATED")
        db.execSQL(SQL_CREATE_USER_TABLE)
        db.execSQL(SQL_CREATE_TRAINING_PHASE_TABLE)
        db.execSQL(SQL_CREATE_TRAINING_PLAN_TABLE)
        db.execSQL(SQL_CREATE_TREADMILL_TABLE)
        db.execSQL(SQL_CREATE_TRAINING_TABLE)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {

    }


    companion object {
        // If you change the database schema, you must increment the database version.
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "TrainingDB.db"
    }
}