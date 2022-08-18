package com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService

import com.example.treadmillassistant.backend.serialize
import com.example.treadmillassistant.backend.serializeWithExceptions
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPhase
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerConstants.BASE_URL
import com.example.treadmillassistant.backend.user
import com.google.gson.Gson
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class ServerTrainingPhaseService {

    fun getTrainingPhase(trainingPhaseID: Long): Pair<StatusCode, ServerTrainingPhase>{
        val serverTrainingPhase: ServerTrainingPhase
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/get_training_phase/$trainingPhaseID")
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        serverTrainingPhase = Gson().fromJson(response.body.toString(), ServerTrainingPhase::class.java)
        return Pair(getResponseCode(response.code), serverTrainingPhase)
    }

    fun createTrainingPhase(serverTrainingPhase: ServerTrainingPhase): StatusCode{
        val client = OkHttpClient()

        val json = serialize(serverTrainingPhase)
        val body = json.toRequestBody()

        val request = Request.Builder()
            .url("$BASE_URL/create_training_phase/${user.ID}")
            .post(body)
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        return getResponseCode(response.code)
    }

    fun updateTrainingPhase(serverTrainingPhase: ServerTrainingPhase, trainingPhaseID: Long): StatusCode{
        val client = OkHttpClient()

        val exceptions = mutableListOf<String>()
        if(serverTrainingPhase.Tilt == -Double.MAX_VALUE){
            exceptions.add("Tilt")
        }
        else if(serverTrainingPhase.OrderNumber == -1){
            exceptions.add("OrderNumber")
        }
        else if(serverTrainingPhase.Speed < 0){
            exceptions.add("Speed")
        }
        else if(serverTrainingPhase.Duration < 0){
            exceptions.add("Duration")
        }
        
        val json = serializeWithExceptions(serverTrainingPhase, exceptions)
        val body = json.toRequestBody()
        
        val request = Request.Builder()
            .url("$BASE_URL/update_training_phase/$trainingPhaseID")
            .post(body)
            .build()
        
        val call = client.newCall(request)
        val response = call.execute()
        
        return getResponseCode(response.code)
    }

    fun deleteTrainingPhase(trainingPhaseID: Long): StatusCode{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/delete_training_phase/$trainingPhaseID")
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        return getResponseCode(response.code)
    }

}