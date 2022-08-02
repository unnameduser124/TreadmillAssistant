package com.example.treadmillassistant.backend.localDatabase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.example.treadmillassistant.backend.User
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.UserTable.AGE
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.UserTable.EMAIL
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.UserTable.FIRST_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.UserTable.LAST_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.UserTable.PASSWORD
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.UserTable.TABLE_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.UserTable.USERNAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.UserTable.WEIGHT
import com.example.treadmillassistant.backend.user

class UserService(){

    //returns id for inserted object
    fun insertNewUser(user: User, db: SQLiteDatabase): Int{

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
    fun deleteUser(id: Int, db: SQLiteDatabase): Int {

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$id")

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateUser(newUser: User, userID: Int, db: SQLiteDatabase): Int{

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

    //loads user without treadmill list, workout plan list or workout calendar
    fun loadUser(db: SQLiteDatabase): User?{

        val projection = arrayOf(BaseColumns._ID,
            EMAIL,
            FIRST_NAME,
            LAST_NAME,
            USERNAME,
            AGE,
            WEIGHT
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

        var user: User? = null
        if((cursor != null) && (cursor.count > 0)){
            with(cursor) {
                moveToFirst()
                val userID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val email = getString(getColumnIndexOrThrow(EMAIL))
                val firstName = getString(getColumnIndexOrThrow(FIRST_NAME))
                val lastName = getString(getColumnIndexOrThrow(LAST_NAME))
                val username = getString(getColumnIndexOrThrow(USERNAME))
                val age = getInt(getColumnIndexOrThrow(AGE))
                val weight = getDouble(getColumnIndexOrThrow(WEIGHT))

                user = User(email = email,
                firstName = firstName,
                lastName = lastName,
                username = username,
                age = age,
                weight = weight,
                ID = userID)
            }
        }

        cursor.close()

        return user
    }

}