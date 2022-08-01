package com.example.treadmillassistant.backend.database

import android.provider.BaseColumns

object TrainingDatabaseConstants {

    const val SQL_CREATE_TRAINING_TABLE = "CREATE TABLE ${TrainingTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${TrainingTable.TRAINING_DATE} TEXT NOT NULL, " +
            "${TrainingTable.TRAINING_TIME} TEXT NOT NULL, " +
            "${TrainingTable.MEDIA_LINK} TEXT, " +
            "${TrainingTable.TRAINING_STATUS} TEXT NOT NULL, " +
            "${TrainingTable.TREADMILL_ID} INTEGER NOT NULL, " +
            "${TrainingTable.TRAINING_PLAN_ID} INTEGER NOT NULL, " +
            "${TrainingTable.USER_ID} INTEGER NOT NULL, " +
            "FOREIGN KEY (${TrainingTable.TREADMILL_ID}) REFERENCES ${TreadmillTable.TABLE_NAME}(${BaseColumns._ID}), " +
            "FOREIGN KEY (${TrainingTable.TRAINING_PLAN_ID}) REFERENCES ${TrainingPlanTable.TABLE_NAME}(${BaseColumns._ID}), " +
            "FOREIGN KEY (${TrainingTable.USER_ID}) REFERENCES ${UserTable.TABLE_NAME}(${BaseColumns._ID}))"

    const val SQL_CREATE_TRAINING_PHASE_TABLE = "CREATE TABLE ${TrainingPhaseTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${TrainingPhaseTable.DURATION} INTEGER NOT NULL, " +
            "${TrainingPhaseTable.SPEED} REAL NOT NULL, " +
            "${TrainingPhaseTable.TILT} REAL NOT NULL, " +
            "${TrainingPhaseTable.ORDER_NUMBER} INTEGER NOT NULL)"

    const val SQL_CREATE_TRAINING_PLAN_TABLE = "CREATE TABLE ${TrainingPlanTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${TrainingPlanTable.PLAN_NAME} TEXT NOT NULL, " +
            "${TrainingPlanTable.USER_ID} INTEGER NOT NULL, " +
            "FOREIGN KEY (${TrainingPlanTable.USER_ID}) REFERENCES ${UserTable.TABLE_NAME}(${BaseColumns._ID}))"


    const val SQL_CREATE_TREADMILL_TABLE = "CREATE TABLE ${TreadmillTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${TreadmillTable.TREADMILL_NAME} TEXT NOT NULL, " +
            "${TreadmillTable.MAX_SPEED} REAL NOT NULL, " +
            "${TreadmillTable.MIN_SPEED} REAL NOT NULL, " +
            "${TreadmillTable.MAX_TILT} REAL NOT NULL, " +
            "${TreadmillTable.MIN_TILT} REAL NOT NULL, " +
            "${TreadmillTable.USER_ID} INTEGER NOT NULL, " +
            "FOREIGN KEY (${TreadmillTable.USER_ID}) REFERENCES ${UserTable.TABLE_NAME}(${BaseColumns._ID}))"


    const val SQL_CREATE_USER_TABLE = "CREATE TABLE ${UserTable.TABLE_NAME} (" +
            "${BaseColumns._ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${UserTable.EMAIL} TEXT NOT NULL, " +
            "${UserTable.PASSWORD} TEXT NOT NULL, " +
            "${UserTable.FIRST_NAME} TEXT NOT NULL, " +
            "${UserTable.LAST_NAME} TEXT NOT NULL, " +
            "${UserTable.USERNAME} TEXT NOT NULL, " +
            "${UserTable.AGE} INTEGER NOT NULL, " +
            "${UserTable.WEIGHT} REAL NOT NULL)"


    object TrainingTable: BaseColumns {
        const val TABLE_NAME = "Training"
        const val TRAINING_DATE = "TrainingDate"
        const val TRAINING_TIME = "TrainingTime"
        const val MEDIA_LINK =  "MediaLink"
        const val TRAINING_STATUS = "Status"
        const val TREADMILL_ID = "TreadmillID"
        const val TRAINING_PLAN_ID = "TrainingPlanID"
        const val USER_ID = "UserID"
    }

    object TrainingPhaseTable: BaseColumns{
        const val TABLE_NAME = "TrainingPhase"
        const val SPEED = "Speed"
        const val TILT = "Tilt"
        const val DURATION = "Duration"
        const val ORDER_NUMBER = "OrderNumber"
    }

    object TrainingPlanTable: BaseColumns{
        const val TABLE_NAME = "TrainingPlan"
        const val PLAN_NAME = "Name"
        const val USER_ID = "UserID"
    }

    object TreadmillTable: BaseColumns{
        const val TABLE_NAME = "Treadmill"
        const val TREADMILL_NAME = "Name"
        const val MAX_SPEED = "MaxSpeed"
        const val MIN_SPEED = "MinSpeed"
        const val MAX_TILT = "MaxTilt"
        const val MIN_TILT = "MinTilt"
        const val USER_ID = "UserID"
    }

    object UserTable: BaseColumns{
        const val TABLE_NAME = "User"
        const val EMAIL = "Email"
        const val PASSWORD = "Password"
        const val FIRST_NAME = "FirstName"
        const val LAST_NAME = "LastName"
        const val USERNAME = "Username"
        const val AGE = "Age"
        const val WEIGHT = "Weight"
    }
}