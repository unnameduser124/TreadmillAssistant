package com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService

import com.example.treadmillassistant.backend.serialize
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPlan
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerConstants.BASE_URL
import com.example.treadmillassistant.backend.user
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody


class ServerTrainingPlanService {

    fun getTrainingPlan(trainingPlanID: Long): Pair<StatusCode, List<List<Any>>> {
        val code = StatusCode.Unknown
        val trainingPlan: List<List<Any>>

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/get_training_plan/$trainingPlanID")
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        trainingPlan = Gson().fromJson(response.body.toString(), object : TypeToken<List<List<Any>>>(){}.type)

        return Pair(code, trainingPlan)
    }

    fun createTrainingPlan(serverTrainingPlan: ServerTrainingPlan): StatusCode{
        val client = OkHttpClient()

        val json = serialize(serverTrainingPlan)
        val body = json.toRequestBody()

        val request = Request.Builder()
            .url("$BASE_URL/create_training_plan/${user.ID}")
            .post(body)
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        return getResponseCode(response.code)
    }

    fun updateTrainingPlan(serverTrainingPlan: ServerTrainingPlan, trainingPlanID: Long): StatusCode{
        val client = OkHttpClient()

        val json = Gson().toJson(serverTrainingPlan)
        val body = json.toRequestBody()

        val request = Request.Builder()
            .url("$BASE_URL/update_training_plan/$trainingPlanID")
            .post(body)
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        return getResponseCode(response.code)
    }

    fun deleteTrainingPlan(trainingPlanID: Long): StatusCode{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/delete_training_plan/$trainingPlanID")
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        return getResponseCode(response.code)
    }

}