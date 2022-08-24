package com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService

import com.example.treadmillassistant.backend.serialize
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPlan
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerConstants.BASE_URL
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.backend.user
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


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

    fun getAllTrainingPlans(skip: Int, limit: Int): Pair<StatusCode, MutableList<TrainingPlan>>{

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/get_all_user_training_plans/${user.ID}?skip=$skip&limit=$limit")
            .build()

        var code = StatusCode.Unknown
        var trainingPlanList = mutableListOf<TrainingPlan>()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.code==StatusCode.OK.code){
                    response.use {
                        val trainingPlanListJson = response.body!!.string()
                        trainingPlanList = Gson().fromJson(trainingPlanListJson, object: TypeToken<List<ServerTrainingPlan>>(){}.type)
                    }
                }
                code = getResponseCode(response.code)
            }
        })
        return Pair(code, trainingPlanList)
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
            .put(body)
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        return getResponseCode(response.code)
    }

    fun deleteTrainingPlan(trainingPlanID: Long): StatusCode{
        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/delete_training_plan/$trainingPlanID")
            .delete()
            .build()

        val call = client.newCall(request)
        val response = call.execute()

        return getResponseCode(response.code)
    }

}