package com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService

import com.example.treadmillassistant.backend.serialize
import com.example.treadmillassistant.backend.serializeWithExceptions
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.NewID
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTraining
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerConstants.BASE_URL
import com.example.treadmillassistant.backend.training.PlannedTraining
import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.backend.user
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import java.text.SimpleDateFormat
import java.util.*

class ServerTrainingService {

    fun getTrainingByID(trainingID: Long): Pair<StatusCode, Training>{

        val client = OkHttpClient()
        var serverTraining = ServerTraining("", "", "", "", -1, -1)

        val request: Request = Request.Builder()
            .url("$BASE_URL/get_training/$trainingID")
            .build()


        val call: Call = client.newCall(request)
        val response: Response = call.execute()

        if(response.code == StatusCode.OK.code){
            val json = response.body!!.string()
            serverTraining = Gson().fromJson(json.subSequence(1, json.length-1).toString(), ServerTraining::class.java)
            serverTraining.ID = trainingID
        }
        val plannedTraining = PlannedTraining(serverTraining)
        return Pair(getResponseCode(response.code), plannedTraining)
    }

    fun getTrainingsForDay(calendar: Calendar, skip: Int, limit: Int): Pair<StatusCode, MutableList<Training>>{
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        val date = sdf.format(calendar.time)

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/get_all_user_trainings/${user.ID}/$date?skip=$skip&limit=$limit")
            .build()

        val code: StatusCode
        var trainingList = mutableListOf<ServerTraining>()

        val call = client.newCall(request)
        val response = call.execute()

        code = getResponseCode(response.code)
        if(code == StatusCode.OK){
            val trainingJson = response.body!!.string()
            trainingList = Gson().fromJson(trainingJson, object: TypeToken<List<ServerTraining>>(){}.type)
        }

        return Pair(code, serverTrainingListToTrainingList(trainingList))
    }

    fun getTrainingsForMonth(calendar: Calendar, skip: Int, limit: Int): Pair<StatusCode, MutableList<Training>>{
        val sdf = SimpleDateFormat("MM-yyyy", Locale.ROOT)
        val date = sdf.format(calendar.time)

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("""$BASE_URL/get_all_user_trainings/${user.ID}/%$date?skip=$skip&limit=$limit""")
            .build()
        val code: StatusCode
        var trainingList = mutableListOf<ServerTraining>()

        val call = client.newCall(request)
        val response = call.execute()
        code = getResponseCode(response.code)
        if(code == StatusCode.OK){
            val trainingListJson = response.body!!.string()
            trainingList = Gson().fromJson(trainingListJson, object: TypeToken<List<ServerTraining>>(){}.type)
        }

        return Pair(code, serverTrainingListToTrainingList(trainingList))
    }

    fun createTraining(serverTraining: ServerTraining): Pair<StatusCode, Long>{
        val client = OkHttpClient()

        val json = serialize(serverTraining)
        val body = json.toRequestBody()

        val request: Request = Request.Builder()
            .url("$BASE_URL/create_training/${user.ID}")
            .post(body)
            .build()

        val call: Call = client.newCall(request)
        val response: Response = call.execute()
        val code = getResponseCode(response.code)
        var id = -1L
        if(code == StatusCode.Created){
            val json = response.body!!.string()
            id = Gson().fromJson(json, NewID::class.java).id
        }

        return Pair(code, id)
    }

    fun updateTraining(serverTraining: ServerTraining, trainingID: Long): StatusCode{
        val client = OkHttpClient()

        val exceptions = mutableListOf<String>()

        if(serverTraining.Date == ""){
            exceptions.add("Date")
        }
        else if(serverTraining.Time == ""){
            exceptions.add("Time")
        }
        else if(serverTraining.Treadmill == -1L){
            exceptions.add("Treadmill")
        }
        else if(serverTraining.TrainingStatus == ""){
            exceptions.add("TrainingStatus")
        }
        else if(serverTraining.TrainingPlanID == -1L){
            exceptions.add("TrainingPlanID")
        }
        else if(serverTraining.Link == ""){
            exceptions.add("Link")
        }
        exceptions.add("ID")

        val json = serializeWithExceptions(serverTraining, exceptions)
        val body = json.toRequestBody()

        val request: Request = Request.Builder()
            .url("$BASE_URL/update_training/$trainingID")
            .put(body)
            .build()


        val call: Call = client.newCall(request)
        val response: Response = call.execute()

        return getResponseCode(response.code)
    }

    fun deleteTraining(trainingID: Long): StatusCode{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/delete_training/$trainingID")
            .delete()
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        return getResponseCode(response.code)
    }

    fun clearUserTrainingHistory(): StatusCode{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/clear_user_training_history/${user.ID}")
            .delete()
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        println(response.code)
        return getResponseCode(response.code)
    }

    private fun serverTrainingListToTrainingList(serverTrainingList: List<ServerTraining>): MutableList<Training>{
        val trainingList = mutableListOf<Training>()

        serverTrainingList.forEach {
            trainingList.add(PlannedTraining(it))
        }

        return trainingList
    }

}