package com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService

import com.example.treadmillassistant.backend.serializeWithExceptions
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.NewID
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPhase
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerTrainingPlan
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerConstants.BASE_URL
import com.example.treadmillassistant.backend.training.TrainingPhase
import com.example.treadmillassistant.backend.training.TrainingPlan
import com.example.treadmillassistant.backend.user
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody


class ServerTrainingPlanService {

    fun getTrainingPlan(trainingPlanID: Long): Pair<StatusCode, TrainingPlan> {
        val code: StatusCode
        var trainingPlan = TrainingPlan()

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/get_training_plan/$trainingPlanID")
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        code = getResponseCode(response.code)

        if(code == StatusCode.OK){
            val json = response.body!!.string()
            var tempData= ""
            run breaking@{
                json.forEach {
                    if(it != ']' && it != '['){
                        tempData += it
                    }
                    else if(it == ']'){
                        return@breaking
                    }
                }
            }
            val plan = Gson().fromJson(tempData, ServerTrainingPlan::class.java)

            tempData = ""
            var list = false
            run breaking@{
                json.forEach {
                    if(list){
                        tempData += it
                    }
                    if(it == ']' && list){
                        return@breaking
                    }
                    if(it == ','){
                        list = true
                    }
                }
            }
            var planTrainingList = mutableListOf<ServerTrainingPhase>()
            if(tempData!="[]"){
                planTrainingList = Gson().fromJson(tempData, object: TypeToken<List<ServerTrainingPhase>>(){}.type)
                plan.ID = planTrainingList.first().TrainingPlanID
            }
            trainingPlan = toTrainingPlan(plan, planTrainingList)
        }

        return Pair(code, trainingPlan)
    }

    fun getAllTrainingPlans(skip: Int, limit: Int): Pair<StatusCode, MutableList<TrainingPlan>>{

        val client = OkHttpClient()

        val request = Request.Builder()
            .url("$BASE_URL/get_all_user_training_plans/${user.ID}?skip=$skip&limit=$limit")
            .build()

        val code: StatusCode
        var trainingPlanList = mutableListOf<ServerTrainingPlan>()

        val call = client.newCall(request)
        val response = call.execute()
        code = getResponseCode(response.code)
        if(code == StatusCode.OK){
            val trainingPlanListJson = response.body!!.string()
            trainingPlanList = Gson().fromJson(trainingPlanListJson, object: TypeToken<List<ServerTrainingPlan>>(){}.type)
        }
        val filtered = trainingPlanList.filter { !it.Name.startsWith("genericTrainingPlan") }.toMutableList()
        return Pair(code, listToTrainingPlan(filtered))
    }

    fun createTrainingPlan(serverTrainingPlan: ServerTrainingPlan): Pair<StatusCode, Long>{
        val client = OkHttpClient()

        val json = serializeWithExceptions(serverTrainingPlan, mutableListOf("ID"))
        val body = json.toRequestBody()

        val request = Request.Builder()
            .url("$BASE_URL/create_training_plan/${user.ID}")
            .post(body)
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        val code = getResponseCode(response.code)
        var id = -1L
        if(code == StatusCode.Created){
            val responseJson = response.body!!.string()
            id = Gson().fromJson(responseJson, NewID::class.java).id
        }

        return Pair(getResponseCode(response.code), id)
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

    private fun toTrainingPlan(serverPlan: ServerTrainingPlan, phaseList: MutableList<ServerTrainingPhase>): TrainingPlan {
        val trainingPlan = TrainingPlan()
        trainingPlan.name = serverPlan.Name
        trainingPlan.ID = serverPlan.ID
        phaseList.forEach {
            trainingPlan.trainingPhaseList.add(TrainingPhase(it))
        }
        return trainingPlan
    }

    private fun listToTrainingPlan(serverPlanList: List<ServerTrainingPlan>): MutableList<TrainingPlan>{
        val planList = mutableListOf<TrainingPlan>()

        serverPlanList.forEach {
            planList.add(TrainingPlan(it))
        }

        return planList
    }
}