package com.example.treadmillassistant.backend.localDatabase

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.provider.BaseColumns
import com.example.treadmillassistant.backend.Treadmill
import com.example.treadmillassistant.backend.finishPhases
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingTable.MEDIA_LINK
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingTable.TABLE_NAME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingTable.TRAINING_DATE
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingTable.TRAINING_PLAN_ID
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingTable.TRAINING_STATUS
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingTable.TRAINING_TIME
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingTable.TREADMILL_ID
import com.example.treadmillassistant.backend.localDatabase.TrainingDatabaseConstants.TrainingTable.USER_ID
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.backend.workout.Workout
import com.example.treadmillassistant.backend.workout.WorkoutPlan
import com.example.treadmillassistant.backend.workout.WorkoutStatus
import java.text.SimpleDateFormat
import java.util.*

class TrainingService(val context: Context): TrainingDatabaseService(context){

    //returns id for inserted object
    fun insertNewTraining(workout: Workout, db: SQLiteDatabase): Long {
        var simpleDateFormat = SimpleDateFormat("dd.MM.${workout.workoutTime.get(Calendar.YEAR)}")
        val date = simpleDateFormat.format(workout.workoutTime.time)
        simpleDateFormat = SimpleDateFormat("HH:mm")
        val time = simpleDateFormat.format(workout.workoutTime.time)

        val contentValues = ContentValues().apply {
            put(TRAINING_DATE, date)
            put(TRAINING_TIME, time)
            put(MEDIA_LINK, workout.mediaLink)
            put(TRAINING_STATUS, "${workout.workoutStatus}")
            put(TREADMILL_ID, workout.treadmill?.ID)
            put(TRAINING_PLAN_ID, workout.workoutPlan?.ID)
            put(USER_ID, user.ID)
        }

        return db.insert(TABLE_NAME, null, contentValues)
    }

    //returns number of rows deleted
    fun deleteTraining(id: Int, db: SQLiteDatabase): Int{

        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$id")

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateTraining(newWorkout: Workout, trainingID: Int, db: SQLiteDatabase): Int{

        var simpleDateFormat = SimpleDateFormat("dd.MM.${newWorkout.workoutTime.get(Calendar.YEAR)}")
        val date = simpleDateFormat.format(newWorkout.workoutTime.time)
        simpleDateFormat = SimpleDateFormat("kk:mm")
        val time = simpleDateFormat.format(newWorkout.workoutTime.time)
        val contentValues = ContentValues().apply {
            put(TRAINING_DATE, date)
            put(TRAINING_TIME, time)
            put(MEDIA_LINK, newWorkout.mediaLink)
            put(TRAINING_STATUS, "${newWorkout.workoutStatus}")
            put(TREADMILL_ID, newWorkout.treadmill?.ID)
            put(TRAINING_PLAN_ID, newWorkout.workoutPlan?.ID)
            put(USER_ID, user.ID)
        }
        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$trainingID")

        return db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }

    //return all trainings without treadmill or workout plan (just their IDs) sorted by ID
    fun getAllTrainings(): MutableList<Workout>{
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID,
            TRAINING_DATE,
            TRAINING_TIME,
            MEDIA_LINK,
            TRAINING_STATUS,
            TREADMILL_ID,
            TRAINING_PLAN_ID
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

        val trainingList = mutableListOf<Workout>()
        with(cursor) {
            while (moveToNext()) {
                val trainingID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val trainingDate = getString(getColumnIndexOrThrow(TRAINING_DATE))
                val trainingTime = getString(getColumnIndexOrThrow(TRAINING_TIME))
                val mediaLink = getString(getColumnIndexOrThrow(MEDIA_LINK))
                val status = getString(getColumnIndexOrThrow(TRAINING_STATUS))
                val treadmillID = getLong(getColumnIndexOrThrow(TREADMILL_ID))
                val trainingPlanID = getLong(getColumnIndexOrThrow(TRAINING_PLAN_ID))

                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val date = simpleDateFormat.parse("$trainingDate $trainingTime")

                val calendar = Calendar.getInstance()
                calendar.time = date

                var trainingStatus: WorkoutStatus

                trainingStatus = if(status=="Upcoming"){
                    WorkoutStatus.Upcoming
                } else if(status=="Finished"){
                    WorkoutStatus.Finished
                } else if(status=="InProgress"){
                    WorkoutStatus.InProgress
                } else if(status=="Paused"){
                    WorkoutStatus.Paused
                } else {
                    WorkoutStatus.Abandoned
                }

                val training = Workout(calendar,
                    Treadmill(ID = treadmillID),
                    mediaLink,
                    trainingStatus,
                    WorkoutPlan(ID = trainingPlanID,
                        workoutPhaseList = TrainingPhaseService(context).getPhasesForTrainingPlan(trainingPlanID)),
                    ID = trainingID,
                    planned = true)

                trainingList.add(training)
                if(training!!.workoutStatus == WorkoutStatus.Finished){
                    finishPhases(training!!)
                }
            }
        }
        cursor.close()

        return trainingList
    }

    //return trainings for date without treadmill or workout plan (just their IDs) sorted by ID
    fun getTrainingForDate(date: Calendar): MutableList<Workout>{
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID,
            TRAINING_DATE,
            TRAINING_TIME,
            MEDIA_LINK,
            TRAINING_STATUS,
            TREADMILL_ID,
            TRAINING_PLAN_ID
        )

        val sortOrder = "${BaseColumns._ID}"

        val selection = "$TRAINING_DATE = ?"

        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val dateFormatted = sdf.format(date.time)

        val selectionArgs = arrayOf(dateFormatted)

        val cursor = db.query(
            TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )

        val trainingList = mutableListOf<Workout>()
        with(cursor) {
            while (moveToNext()) {
                val trainingID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val trainingDate = getString(getColumnIndexOrThrow(TRAINING_DATE))
                val trainingTime = getString(getColumnIndexOrThrow(TRAINING_TIME))
                val mediaLink = getString(getColumnIndexOrThrow(MEDIA_LINK))
                val status = getString(getColumnIndexOrThrow(TRAINING_STATUS))
                val treadmillID = getLong(getColumnIndexOrThrow(TREADMILL_ID))
                val trainingPlanID = getLong(getColumnIndexOrThrow(TRAINING_PLAN_ID))

                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val date = simpleDateFormat.parse("$trainingDate $trainingTime")

                val calendar = Calendar.getInstance()
                calendar.time = date

                var trainingStatus: WorkoutStatus

                trainingStatus = if(status=="Upcoming"){
                    WorkoutStatus.Upcoming
                } else if(status=="Finished"){
                    WorkoutStatus.Finished
                } else if(status=="InProgress"){
                    WorkoutStatus.InProgress
                } else if(status=="Paused"){
                    WorkoutStatus.Paused
                } else {
                    WorkoutStatus.Abandoned
                }

                val training = Workout(calendar,
                    Treadmill(ID = treadmillID),
                    mediaLink,
                    trainingStatus,
                    WorkoutPlan(ID = trainingPlanID,
                        workoutPhaseList = TrainingPhaseService(context).getPhasesForTrainingPlan(trainingPlanID)),
                    ID = trainingID,
                    planned = true)

                trainingList.add(training)
                if(training!!.workoutStatus == WorkoutStatus.Finished){
                    finishPhases(training!!)
                }
            }
        }
        cursor.close()

        return trainingList
    }

    fun getTrainingByID(ID: Long): Workout?{
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID,
            TRAINING_DATE,
            TRAINING_TIME,
            MEDIA_LINK,
            TRAINING_STATUS,
            TREADMILL_ID,
            TRAINING_PLAN_ID
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

        var training: Workout? = null
        if((cursor != null) && (cursor.count > 0)){
            with(cursor) {
                moveToFirst()
                val trainingID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val trainingDate = getString(getColumnIndexOrThrow(TRAINING_DATE))
                val trainingTime = getString(getColumnIndexOrThrow(TRAINING_TIME))
                val mediaLink = getString(getColumnIndexOrThrow(MEDIA_LINK))
                val status = getString(getColumnIndexOrThrow(TRAINING_STATUS))
                val treadmillID = getLong(getColumnIndexOrThrow(TREADMILL_ID))
                val trainingPlanID = getLong(getColumnIndexOrThrow(TRAINING_PLAN_ID))

                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val date = simpleDateFormat.parse("$trainingDate $trainingTime")

                val calendar = Calendar.getInstance()
                val currentDate = Calendar.getInstance()
                currentDate.set(Calendar.HOUR_OF_DAY, 0)
                currentDate.set(Calendar.MINUTE, 0)
                calendar.time = date

                var trainingStatus: WorkoutStatus

                trainingStatus = if(calendar.time < currentDate.time){
                    WorkoutStatus.Finished
                }
                else if(status=="Upcoming"){
                    WorkoutStatus.Upcoming
                } else if(status=="Finished"){
                    WorkoutStatus.Finished
                } else if(status=="InProgress"){
                    WorkoutStatus.InProgress
                } else if(status=="Paused"){
                    WorkoutStatus.Paused
                } else {
                    WorkoutStatus.Abandoned
                }



                val treadmill = TreadmillService(context).getTreadmillByID(treadmillID)
                val workoutPlan = TrainingPlanService(context).getTrainingPlanForTraining(trainingPlanID)


                if(treadmill!=null && workoutPlan!=null){
                    training = Workout(calendar,
                        treadmill!!,
                        mediaLink,
                        trainingStatus,
                        workoutPlan!!,
                        ID = trainingID,
                        planned = true)
                    if(training!!.workoutStatus == WorkoutStatus.Finished){
                        finishPhases(training!!)
                    }
                }
                else{
                    training = Workout(calendar,
                        Treadmill(ID=treadmillID),
                        mediaLink,
                        trainingStatus,
                        WorkoutPlan(ID=trainingPlanID),
                        ID = trainingID,
                        planned = true)
                }
            }
        }

        cursor.close()

        return training
    }

    fun getTrainingForDateRange(startDate: Calendar, endDate: Calendar, db: SQLiteDatabase): MutableList<Workout> {

        val projection = arrayOf(BaseColumns._ID,
            TRAINING_DATE,
            TRAINING_TIME,
            MEDIA_LINK,
            TRAINING_STATUS,
            TREADMILL_ID,
            TRAINING_PLAN_ID
        )

        val sortOrder = "${BaseColumns._ID}"

        val selection = "$TRAINING_DATE >= ? AND $TRAINING_DATE <= ?"

        val sdf = SimpleDateFormat("dd.MM.yyyy")
        val start = sdf.format(startDate.time)
        val end = sdf.format(endDate.time)

        val selectionArgs = arrayOf(start, end)

        val cursor = db.query(
            TrainingDatabaseConstants.TrainingPlanTable.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        )

        val trainingList = mutableListOf<Workout>()
        with(cursor) {
            while (moveToNext()) {
                val trainingID = getLong(getColumnIndexOrThrow(BaseColumns._ID))
                val trainingDate = getString(getColumnIndexOrThrow(TRAINING_DATE))
                val trainingTime = getString(getColumnIndexOrThrow(TRAINING_TIME))
                val mediaLink = getString(getColumnIndexOrThrow(MEDIA_LINK))
                val status = getString(getColumnIndexOrThrow(TRAINING_STATUS))
                val treadmillID = getLong(getColumnIndexOrThrow(TREADMILL_ID))
                val trainingPlanID = getLong(getColumnIndexOrThrow(TRAINING_PLAN_ID))

                val simpleDateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm")
                val date = simpleDateFormat.parse("$trainingDate $trainingTime")

                val calendar = Calendar.getInstance()
                calendar.time = date

                var trainingStatus: WorkoutStatus

                trainingStatus = if(status=="Upcoming"){
                    WorkoutStatus.Upcoming
                } else if(status=="Finished"){
                    WorkoutStatus.Finished
                } else if(status=="InProgress"){
                    WorkoutStatus.InProgress
                } else if(status=="Paused"){
                    WorkoutStatus.Paused
                } else {
                    WorkoutStatus.Abandoned
                }

                trainingList.add(Workout(calendar,
                    Treadmill(ID = treadmillID),
                    mediaLink,
                    trainingStatus,
                    WorkoutPlan(ID = trainingPlanID),
                    ID = trainingID,
                    planned = true))
            }
        }
        cursor.close()

        return trainingList
    }
}