package com.example.treadmillassistant.backend.localDatabase

import android.content.ContentValues
import android.content.Context
import android.os.Build.ID
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
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.backend.training.TrainingStatus
import java.text.SimpleDateFormat
import java.util.*

class TrainingService(val context: Context): TrainingDatabaseService(context){

    //returns id for inserted object
    fun insertNewTraining(training: PlannedTraining): Long {
        val db = this.writableDatabase
        var simpleDateFormat = SimpleDateFormat("dd.MM.${training.trainingTime.get(Calendar.YEAR)}")
        val date = simpleDateFormat.format(training.trainingTime.time)
        simpleDateFormat = SimpleDateFormat("HH:mm")
        val time = simpleDateFormat.format(training.trainingTime.time)

        val contentValues = ContentValues().apply {
            put(TRAINING_DATE, date)
            put(TRAINING_TIME, time)
            put(MEDIA_LINK, training.mediaLink)
            put(TRAINING_STATUS, "${training.trainingStatus}")
            put(TREADMILL_ID, training.treadmill.ID)
            put(TRAINING_PLAN_ID, training.trainingPlan.ID)
            put(USER_ID, user.ID)
        }

        return db.insert(TABLE_NAME, null, contentValues)
    }

    //returns number of rows deleted
    fun deleteTraining(id: Int): Int{
        val db = this.writableDatabase
        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$id")

        return db.delete(TABLE_NAME, selection, selectionArgs)
    }

    //returns number of rows updated
    fun updateTraining(newTraining: PlannedTraining, trainingID: Int): Int{
        val db = this.writableDatabase
        var simpleDateFormat = SimpleDateFormat("dd.MM.${newTraining.trainingTime.get(Calendar.YEAR)}")
        val date = simpleDateFormat.format(newTraining.trainingTime.time)
        simpleDateFormat = SimpleDateFormat("kk:mm")
        val time = simpleDateFormat.format(newTraining.trainingTime.time)
        val contentValues = ContentValues().apply {
            put(TRAINING_DATE, date)
            put(TRAINING_TIME, time)
            put(MEDIA_LINK, newTraining.mediaLink)
            put(TRAINING_STATUS, "${newTraining.trainingStatus}")
            put(TREADMILL_ID, newTraining.treadmill.ID)
            put(TRAINING_PLAN_ID, newTraining.trainingPlan.ID)
            put(USER_ID, user.ID)
        }
        val selection = "${BaseColumns._ID} = ?"

        val selectionArgs = arrayOf("$trainingID")

        return db.update(TABLE_NAME, contentValues, selection, selectionArgs)
    }

    //return all trainings without treadmill or workout plan (just their IDs) sorted by ID
    fun getAllTrainings(): MutableList<Training>{
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID,
            TRAINING_DATE,
            TRAINING_TIME,
            MEDIA_LINK,
            TRAINING_STATUS,
            TREADMILL_ID,
            TRAINING_PLAN_ID
        )

        val sortOrder = BaseColumns._ID

        val cursor = db.query(
            TABLE_NAME,
            projection,
            null,
            null,
            null,
            null,
            sortOrder
        )

        val trainingList = mutableListOf<Training>()
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

                var trainingStatus: TrainingStatus

                trainingStatus = if(status=="Upcoming"){
                    TrainingStatus.Upcoming
                } else if(status=="Finished"){
                    TrainingStatus.Finished
                } else if(status=="InProgress"){
                    TrainingStatus.InProgress
                } else if(status=="Paused"){
                    TrainingStatus.Paused
                } else {
                    TrainingStatus.Abandoned
                }

                val training = PlannedTraining(calendar,
                    Treadmill(ID = treadmillID),
                    mediaLink,
                    trainingStatus,
                    if(TrainingPlanService(context).getTrainingPlanByID(trainingPlanID)!=null) {
                        TrainingPlanService(context).getTrainingPlanByID(trainingPlanID)!!
                    }
                    else{ TrainingPlan(ID = trainingPlanID,
                        trainingPhaseList = TrainingPhaseService(context).getPhasesForTrainingPlan(trainingPlanID))
                    },
                    ID = trainingID)

                trainingList.add(training)
                if(training.trainingStatus == TrainingStatus.Finished){
                    finishPhases(training)
                }
            }
        }
        cursor.close()

        return trainingList
    }

    //return trainings for date without treadmill or workout plan (just their IDs) sorted by ID
    fun getTrainingForDate(date: Calendar): MutableList<PlannedTraining>{
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID,
            TRAINING_DATE,
            TRAINING_TIME,
            MEDIA_LINK,
            TRAINING_STATUS,
            TREADMILL_ID,
            TRAINING_PLAN_ID
        )

        val sortOrder = BaseColumns._ID

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

        val trainingList = mutableListOf<PlannedTraining>()
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

                var trainingStatus: TrainingStatus

                trainingStatus = if(status=="Upcoming"){
                    TrainingStatus.Upcoming
                } else if(status=="Finished"){
                    TrainingStatus.Finished
                } else if(status=="InProgress"){
                    TrainingStatus.InProgress
                } else if(status=="Paused"){
                    TrainingStatus.Paused
                } else {
                    TrainingStatus.Abandoned
                }

                val training = PlannedTraining(calendar,
                    Treadmill(ID = treadmillID),
                    mediaLink,
                    trainingStatus,
                    TrainingPlan(ID = trainingPlanID,
                        trainingPhaseList = TrainingPhaseService(context).getPhasesForTrainingPlan(trainingPlanID)),
                    ID = trainingID)

                trainingList.add(training)
                if(training.trainingStatus == TrainingStatus.Finished){
                    finishPhases(training)
                }
            }
        }
        cursor.close()

        return trainingList
    }

    fun getTrainingByID(ID: Long): PlannedTraining?{
        val db = this.readableDatabase
        val projection = arrayOf(BaseColumns._ID,
            TRAINING_DATE,
            TRAINING_TIME,
            MEDIA_LINK,
            TRAINING_STATUS,
            TREADMILL_ID,
            TRAINING_PLAN_ID
        )

        val sortOrder = BaseColumns._ID

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

        var training: PlannedTraining? = null
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
                if (date != null) {
                    calendar.time = date
                }

                val trainingStatus: TrainingStatus = if(calendar.time < currentDate.time){
                    TrainingStatus.Finished
                }
                else if(status=="Upcoming"){
                    TrainingStatus.Upcoming
                } else if(status=="Finished"){
                    TrainingStatus.Finished
                } else if(status=="InProgress"){
                    TrainingStatus.InProgress
                } else if(status=="Paused"){
                    TrainingStatus.Paused
                } else {
                    TrainingStatus.Abandoned
                }



                val treadmill = TreadmillService(context).getTreadmillByID(treadmillID)
                val workoutPlan = TrainingPlanService(context).getTrainingPlanByID(trainingPlanID)


                if(treadmill!=null && workoutPlan!=null){
                    training = PlannedTraining(calendar,
                        treadmill,
                        mediaLink,
                        trainingStatus,
                        workoutPlan,
                        ID = trainingID)
                    if(training!!.trainingStatus == TrainingStatus.Finished){
                        finishPhases(training!!)
                    }
                }
                else{
                    training = PlannedTraining(calendar,
                        Treadmill(ID=treadmillID),
                        mediaLink,
                        trainingStatus,
                        TrainingPlan(ID=trainingPlanID),
                        ID = trainingID)
                }
            }
        }

        cursor.close()

        return training
    }

    fun getTrainingForDateRange(startDate: Calendar, endDate: Calendar): MutableList<PlannedTraining> {
        val db = this.readableDatabase

        val projection = arrayOf(BaseColumns._ID,
            TRAINING_DATE,
            TRAINING_TIME,
            MEDIA_LINK,
            TRAINING_STATUS,
            TREADMILL_ID,
            TRAINING_PLAN_ID
        )

        val sortOrder = BaseColumns._ID

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

        val trainingList = mutableListOf<PlannedTraining>()
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
                if (date != null) {
                    calendar.time = date
                }

                var trainingStatus: TrainingStatus = if(status=="Upcoming"){
                    TrainingStatus.Upcoming
                } else if(status=="Finished"){
                    TrainingStatus.Finished
                } else if(status=="InProgress"){
                    TrainingStatus.InProgress
                } else if(status=="Paused"){
                    TrainingStatus.Paused
                } else {
                    TrainingStatus.Abandoned
                }

                trainingList.add(PlannedTraining(calendar,
                    Treadmill(ID = treadmillID),
                    mediaLink,
                    trainingStatus,
                    TrainingPlan(ID = trainingPlanID),
                    ID = trainingID))
            }
        }
        cursor.close()

        return trainingList
    }
}