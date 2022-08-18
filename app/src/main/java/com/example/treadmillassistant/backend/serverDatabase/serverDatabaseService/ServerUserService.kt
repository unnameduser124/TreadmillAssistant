package com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService

import com.example.treadmillassistant.backend.serialize
import com.example.treadmillassistant.backend.serializeWithExceptions
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerUser
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerConstants.BASE_URL
import com.example.treadmillassistant.backend.user
import com.google.gson.Gson
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException


class ServerUserService {

    fun createUser(serverUser: ServerUser): StatusCode {
        val json = serialize(serverUser)
        val client = OkHttpClient()

        val body: RequestBody = json
            .toRequestBody(json.toMediaTypeOrNull())

        val request: Request = Request.Builder()
            .url("$BASE_URL/create_user")
            .post(body)
            .build()

        val call: Call = client.newCall(request)
        val response: Response = call.execute()
        return getResponseCode(response.code)
    }

    fun updateUser(serverUser: ServerUser, userID: Long): StatusCode{

        val client = OkHttpClient()
        val json = serverUserJsonBuilder(serverUser)

        val body: RequestBody = json.toRequestBody()

        val request: Request = Request.Builder()
            .url( "$BASE_URL/update_user/${userID}")
            .post(body)
            .build()

        val call: Call = client.newCall(request)
        val response: Response = call.execute()

        return getResponseCode(response.code)
    }

    fun deleteUser(userID: Long): StatusCode{
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url("$BASE_URL/delete_user/$userID")
            .build()

        val call: Call = client.newCall(request)
        val response: Response = call.execute()

        return getResponseCode(response.code)
    }

    fun checkUserCredentials(email: String, password: String): StatusCode {
        val json = serialize(object{ val Password = password })

        val client = OkHttpClient()

        val body = json.toRequestBody()

        val request: Request = Request.Builder()
            .url("$BASE_URL/check_user_credentials/$email")
            .post(body)
            .build()


        val call: Call = client.newCall(request)
        val response: Response = call.execute()
        if(response.code == StatusCode.OK.code){
            val id = response.body.toString().toLong()
            user.ID = id
        }
        return getResponseCode(response.code)
    }

    fun getUser(userID: Long): Pair<StatusCode, ServerUser>{
        val client = OkHttpClient()
        var code: StatusCode = StatusCode.Unknown
        var deserializedUser = ServerUser("", "", "", -1, -1.0, "", "")

        val request = Request.Builder()
            .url("$BASE_URL/get_user/$userID")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if(response.code==StatusCode.OK.code){
                    response.use {
                        val userJson = response.body!!.string()
                        deserializedUser = Gson().fromJson(userJson, ServerUser::class.java)
                    }
                }
                code = getResponseCode(response.code)
            }
        })
        return Pair(code, deserializedUser)
    }

    fun updateUserPassword(password: String, userID: Long): StatusCode{
        val client = OkHttpClient()
        val json = serialize(object{ val Password = password})

        val requestBody = json.toRequestBody()

        val request: Request = Request.Builder()
            .url("$BASE_URL/update_user_password/${userID}")
            .post(requestBody)
            .build()

        val call: Call = client.newCall(request)
        val response: Response = call.execute()
        return getResponseCode(response.code)
    }

    private fun serverUserJsonBuilder(serverUser: ServerUser): String {
        val exceptions = mutableListOf<String>()

        if (serverUser.FirstName == "") {
            exceptions.add("FirstName")
        }
        if (serverUser.LastName == "") {
            exceptions.add("LastName")
        }
        if (serverUser.Age == -1) {
            exceptions.add("Age")
        }
        if (serverUser.Nick == "") {
            exceptions.add("Nick")
        }
        if (serverUser.Weight == -1.0) {
            exceptions.add("Weight")
        }
        if (serverUser.Email == "") {
            exceptions.add("Email")
        }

        return serializeWithExceptions(serverUser, exceptions)
    }
}