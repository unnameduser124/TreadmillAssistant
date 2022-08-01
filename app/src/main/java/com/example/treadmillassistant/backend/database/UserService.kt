package com.example.treadmillassistant.backend.database

import android.content.ContentValues
import android.content.Context
import android.provider.BaseColumns
import com.example.treadmillassistant.backend.User
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.UserTable.AGE
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.UserTable.EMAIL
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.UserTable.FIRST_NAME
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.UserTable.LAST_NAME
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.UserTable.PASSWORD
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.UserTable.TABLE_NAME
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.UserTable.USERNAME
import com.example.treadmillassistant.backend.database.TrainingDatabaseConstants.UserTable.WEIGHT

class UserService(context: Context): TrainingDatabaseService(context) {

    //returns id for inserted object
    fun insertNewUser(user: User): Int{
        var db = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(EMAIL, user.email)
            put(PASSWORD, user.password)
            put(FIRST_NAME, user.firstName)
            put(LAST_NAME, user.lastName)
            put(USERNAME, user.username)
            put(AGE, user.age)
            put(WEIGHT, user.weight)
        }

        val newUserID = db.insert(TABLE_NAME, null, contentValues)
        return newUserID.toInt()
    }

    //returns number of rows deleted
    fun deleteUser(id: Int): Int {
        var db = this.writableDatabase

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$id")

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateUser(newUser: User, userID: Int): Int{
        val db = this.writableDatabase

        val contentValues = ContentValues().apply {
            put(FIRST_NAME, newUser.firstName)
            put(LAST_NAME, newUser.lastName)
            put(USERNAME, newUser.username)
            put(AGE, newUser.age)
            put(WEIGHT, newUser.weight)
        }

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$userID")

        return db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }


}