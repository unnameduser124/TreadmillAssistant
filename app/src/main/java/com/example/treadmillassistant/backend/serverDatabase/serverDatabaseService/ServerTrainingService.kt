package com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService

import com.example.treadmillassistant.backend.serialize
import com.example.treadmillassistant.backend.serializeWithExceptions
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTraining
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerConstants.BASE_URL
import com.example.treadmillassistant.backend.training.Training
import com.example.treadmillassistant.backend.user
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ServerTrainingService {

    fun getTrainingByID(trainingID: String): Pair<StatusCode, ServerTraining>{

        val client = OkHttpClient()
        var serverTraining = ServerTraining("", "", "", "", -1, -1)

        val request: Request = Request.Builder()
            .url("$BASE_URL/get_training/$trainingID")
            .build()


        val call: Call = client.newCall(request)
        val response: Response = call.execute()
        if(response.code == StatusCode.OK.code){
            serverTraining = Gson().fromJson(response.body.toString(), ServerTraining::class.java)
        }
        return Pair(getResponseCode(response.code), serverTraining)
    }

    fun getTrainingsForDay(calendar: Calendar, skip: Int, limit: Int): Pair<StatusCode, MutableList<Training>>{
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.ROOT)
        val date = sdf.format(calendar)

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/get_all_user_trainings/${user.ID}/%$date?skip=$skip&limit=$limit")
            .build()

        var code = StatusCode.Unknown
        var trainingList = mutableListOf<Training>()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.code==StatusCode.OK.code){
                    response.use {
                        val trainingJson = response.body!!.string()
                        trainingList = Gson().fromJson(trainingJson, object: TypeToken<List<Training>>(){}.type)
                    }
                }
                code = getResponseCode(response.code)
            }
        })

        return Pair(code, trainingList)
    }

    fun getTrainingsForMonth(calendar: Calendar, skip: Int, limit: Int): Pair<StatusCode, MutableList<Training>>{
        val sdf = SimpleDateFormat("%MM-yyyy", Locale.ROOT)
        val date = sdf.format(calendar)

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/get_all_user_trainings/${user.ID}/%$date?skip=$skip&limit=$limit")
            .build()

        var code = StatusCode.Unknown
        var trainingList = mutableListOf<Training>()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.code==StatusCode.OK.code){
                    response.use {
                        val trainingListJson = response.body!!.string()
                        trainingList = Gson().fromJson(trainingListJson, object: TypeToken<List<Training>>(){}.type)
                    }
                }
                code = getResponseCode(response.code)
            }
        })

        return Pair(code, trainingList)
    }

    fun createTraining(serverTraining: ServerTraining): StatusCode{
        val client = OkHttpClient()

        val json = serialize(serverTraining)
        val body = json.toRequestBody()

        val request: Request = Request.Builder()
            .url("$BASE_URL/create_training/${user.ID}")
            .post(body)
            .build()


        val call: Call = client.newCall(request)
        val response: Response = call.execute()

        return getResponseCode(response.code)
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
        else if(serverTraining.Link == " "){
            exceptions.add("Link")
        }

        val json = serializeWithExceptions(serverTraining, exceptions)
        val body = json.toRequestBody()

        val request: Request = Request.Builder()
            .url("$BASE_URL/update_training/$trainingID")
            .post(body)
            .build()


        val call: Call = client.newCall(request)
        val response: Response = call.execute()

        return getResponseCode(response.code)
    }

    fun deleteTraining(trainingID: Long): StatusCode{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/delete_training/$trainingID")
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        return getResponseCode(response.code)
    }

    fun clearUserTrainingHistory(): StatusCode{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("""$BASE_URL/clear_user_training_history/${user.ID}""")
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        return getResponseCode(response.code)
    }

}