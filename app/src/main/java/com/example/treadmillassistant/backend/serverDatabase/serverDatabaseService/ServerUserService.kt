package com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService

import com.example.treadmillassistant.backend.User
import com.example.treadmillassistant.backend.serialize
import com.example.treadmillassistant.backend.serializeWithExceptions
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.LoginUserID
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.NewID
import com.example.treadmillassistant.backend.serverDatabase.databaseClasses.ServerUser
import com.example.treadmillassistant.backend.serverDatabase.serverDatabaseService.ServerConstants.BASE_URL
import com.example.treadmillassistant.backend.user
import com.example.treadmillassistant.hashMessage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody


class ServerUserService {

    fun createUser(serverUser: ServerUser): StatusCode {
        val json = serialize(serverUser)
        val client = OkHttpClient()
        var code: StatusCode = StatusCode.Unknown

        val body: RequestBody = json
            .toRequestBody(json.toMediaTypeOrNull())

        val request: Request = Request.Builder()
            .url("$BASE_URL/create_user")
            .post(body)
            .build()
        val call: Call = client.newCall(request)
        val response: Response = call.execute()
        code = getResponseCode(response.code)
        if(code == StatusCode.Created){
            val json = response.body!!.string()
            val id = Gson().fromJson(json, NewID::class.java)
            user.ID = id.id
        }

        return code
    }

    fun updateUser(serverUser: ServerUser, userID: Long): StatusCode{

        val client = OkHttpClient()
        val json = serverUserJsonBuilder(serverUser)

        val body: RequestBody = json.toRequestBody()

        val request: Request = Request.Builder()
            .url( "$BASE_URL/update_user/${userID}")
            .put(body)
            .build()

        val call: Call = client.newCall(request)
        val response: Response = call.execute()

        return getResponseCode(response.code)
    }

    fun deleteUser(userID: Long): StatusCode{
        val client = OkHttpClient()

        val request: Request = Request.Builder()
            .url("$BASE_URL/delete_user/$userID")
            .delete()
            .build()

        println(request.url)
        val call: Call = client.newCall(request)
        val response: Response = call.execute()
        println(response.code)
        return getResponseCode(response.code)
    }

    fun checkUserCredentials(email: String, password: String): StatusCode {
        val json = serialize(object{ val Password = hashMessage(password) })

        val client = OkHttpClient()

        val body = json.toRequestBody()

        val request: Request = Request.Builder()
            .url("$BASE_URL/check_user_credentials/$email")
            .post(body)
            .build()


        val call: Call = client.newCall(request)
        val response: Response = call.execute()
        if(response.code == StatusCode.OK.code){
            val id = Gson().fromJson(response.body!!.string(), LoginUserID::class.java)
            user.ID = id.UserID
        }
        return getResponseCode(response.code)
    }

    fun getUser(userID: Long): Pair<StatusCode, User>{
        val client = OkHttpClient()
        var code: StatusCode = StatusCode.Unknown
        var deserializedUser = mutableListOf<ServerUser>()

        val request = Request.Builder()
            .url("$BASE_URL/get_user/$userID")
            .build()

        val call = client.newCall(request)
        val response = call.execute()
        code = getResponseCode(response.code)
        if(code==StatusCode.OK){
            val userJson = response.body!!.string()
            deserializedUser = Gson().fromJson(userJson, object: TypeToken<List<ServerUser>>(){}.type)
        }
        var user = User()
        if(deserializedUser.isNotEmpty()){
            user = User(deserializedUser.first(), userID)
        }
        return Pair(code, user)
    }

    fun updateUserPassword(password: String, userID: Long): StatusCode{
        val client = OkHttpClient()
        val json = serialize(object{ val Password = password})

        val requestBody = json.toRequestBody()

        val request: Request = Request.Builder()
            .url("$BASE_URL/update_user_password/${userID}")
            .put(requestBody)
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
        if(serverUser.Password == ""){
            exceptions.add("Password")
        }

        return serializeWithExceptions(serverUser, exceptions)
    }
}